package mx.edu.utez.modules.imports;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.assets.Assets;
import mx.edu.utez.modules.assets.AssetsRepository;
import mx.edu.utez.modules.campus.Campus;
import mx.edu.utez.modules.campus.CampusRepository;
import mx.edu.utez.modules.edificios.Edificio;
import mx.edu.utez.modules.edificios.EdificioRepository;
import mx.edu.utez.modules.espacios.Espacio;
import mx.edu.utez.modules.espacios.EspacioRepository;
import mx.edu.utez.modules.tipo_activos.TipoActivo;
import mx.edu.utez.modules.tipo_activos.TipoActivoRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

/**
 * Servicio especializado en la importación masiva de activos desde archivos Excel (.xlsx).
 * Implementa validaciones exhaustivas, manejo de errores detallado y optimizaciones
 * para garantizar una importación eficiente y confiable.
 *
 * <p><b>Formato esperado del archivo .xlsx (fila 1 = encabezados, datos desde fila 2):</b></p>
 * <pre>
 * Etiqueta*, Número de Serie*, Tipo(Nombre*, Marca*, Bien*, Modelo*)*, Campus*, Edificio*, Espacio*
 * </pre>
 *
 * @author Ithera Team
 */
@Log4j2
@Service
@AllArgsConstructor
public class ImportService {

    private final AssetsRepository assetsRepository;
    private final TipoActivoRepository tipoActivoRepository;
    private final CampusRepository campusRepository;
    private final EdificioRepository edificioRepository;
    private final EspacioRepository espacioRepository;

    /**
     * Recibe y valida un archivo Excel multipart para la importación masiva de activos.
     *
     * @param file Archivo Excel con formato .xlsx
     * @return ApiResponse con el resultado de la operación (éxito o lista de errores)
     */
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse save(MultipartFile file) {

        final String SHEETS_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

        if (file == null || file.isEmpty())
            return new ApiResponse("Archivo no proporcionado o vacío", true, HttpStatus.BAD_REQUEST);

        if (!SHEETS_TYPE.equals(file.getContentType()))
            return new ApiResponse("Tipo de archivo no soportado. Debe ser .xlsx o similar", true, HttpStatus.BAD_REQUEST);

        return handleExcel(file);
    }

    // ========================================================= \\
    //  _    _  _        _____   ______  _____    _____          \\
    // | |  | || |      |  __ \ |  ____||  __ \  / ____|         \\
    // | |__| || |      | |__) || |__   | |__) || (___           \\
    // |  __  || |      |  ___/ |  __|  |  _  /  \___ \          \\
    // | |  | || |____  | |     | |____ | | \ \  ____) |         \\
    // |_|  |_||______| |_|     |______||_|  \_\|_____/          \\
    // ========================================================= //

    /**
     * Procesa el contenido del archivo Excel, validando reglas de negocio,
     * evitando duplicados mediante HashSet y optimizando consultas con HashMap.
     *
     * @param file Archivo Excel validado
     * @return ApiResponse con confirmación o detalle de errores por fila
     */
    @Transactional(rollbackFor = {Exception.class})
    protected ApiResponse handleExcel(MultipartFile file) {
        List<Assets> activosGuardar = new ArrayList<>();
        List<String> errores = new ArrayList<>();

        // Sets para detectar duplicados dentro del propio archivo sin consultar la BD
        Set<String> etiquetasEnLote = new HashSet<>();
        Set<String> seriesEnLote = new HashSet<>();

        // Cachés en memoria para evitar consultas repetidas a los catálogos.
        // La clave del tipoCache combina tipo+marca+modelo para identificar
        // el TipoActivo, ya que dos tipos pueden tener el mismo nombre pero distinta marca/modelo.
        final Map<String, TipoActivo> tipoCache = new HashMap<>();
        final Map<String, Campus> campusCache = new HashMap<>();
        final Map<String, Edificio> edificioCache = new HashMap<>();
        final Map<String, Espacio> espacioCache = new HashMap<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {

                if (row.getRowNum() == 0 || isRowEmpty(row))
                    continue; // Saltar cabecera y filas vacías

                try {
                    // 1. Extraer valores de cada celda según su columna
                    String etiqueta    = getCellValue(row.getCell(0));
                    String serie       = getCellValue(row.getCell(1));
                    String nombreStr   = getCellValue(row.getCell(2)); // Atributo de TipoActivo
                    String marcaStr    = getCellValue(row.getCell(3)); // Atributo de TipoActivo
                    String bienStr     = getCellValue(row.getCell(4)); // Atributo de TipoActivo (era "tipoStr")
                    String modeloStr   = getCellValue(row.getCell(5)); // Atributo de TipoActivo
                    String campusStr   = getCellValue(row.getCell(6));
                    String edificioStr = getCellValue(row.getCell(7));
                    String espacioStr  = getCellValue(row.getCell(8));

                    // 2. Validar que todos los campos obligatorios (*) tengan valor
                    if (etiqueta.isEmpty()    ||
                            serie.isEmpty()       ||
                            nombreStr.isEmpty()   ||
                            marcaStr.isEmpty()    ||
                            bienStr.isEmpty()     ||
                            modeloStr.isEmpty()   ||
                            campusStr.isEmpty()   ||
                            edificioStr.isEmpty() ||
                            espacioStr.isEmpty()
                    )
                        throw new IllegalArgumentException("Faltan campos obligatorios (*)");

                    // 3. Validar duplicados dentro del lote (sin tocar la BD)
                    if (!etiquetasEnLote.add(etiqueta))
                        throw new IllegalArgumentException("La etiqueta '" + etiqueta + "' está repetida en el archivo.");

                    if (!seriesEnLote.add(serie))
                        throw new IllegalArgumentException("El número de serie '" + serie + "' está repetido en el archivo.");

                    // 4. Validar duplicados contra la BD
                    if (assetsRepository.existsByEtiqueta(etiqueta))
                        throw new IllegalArgumentException("La etiqueta '" + etiqueta + "' ya existe en el sistema.");

                    if (assetsRepository.existsByNumeroSerie(serie))
                        throw new IllegalArgumentException("El número de serie '" + serie + "' ya existe en el sistema.");

                    // 5. Resolución de catálogos con caché (HashMap para evitar N+1)

                    // TipoActivo: la clave combina tipo+marca+modelo porque marca y modelo
                    // son atributos propios de TipoActivo, no entidades independientes.
                    // Así se garantiza que dos filas con mismo tipo pero distinta marca/modelo
                    // no colisionen en el caché.
                    String tipoKey = nombreStr + "-" + marcaStr + "-" + bienStr + "-" + modeloStr;
                    TipoActivo tipoActivo = tipoCache.computeIfAbsent(tipoKey, k ->
                            tipoActivoRepository.findByNombreAndMarcaAndTipoBienAndModelo(nombreStr, marcaStr, bienStr, modeloStr)
                                    .orElseThrow(() -> new IllegalArgumentException(
                                            "No existe el TipoActivo con nombre '" + nombreStr +
                                                    "', marca '" + marcaStr +
                                                    "', bien '" + bienStr +
                                                    "' y modelo '" + modeloStr + "."))
                    );

                    Campus campus = campusCache.computeIfAbsent(campusStr, k ->
                            campusRepository.findByNombre(k)
                                    .orElseThrow(() -> new IllegalArgumentException("El Campus '" + k + "' no existe."))
                    );

                    // Clave compuesta campusId+edificio para soportar edificios con igual nombre en distintos campus
                    String edificioKey = campus.getId() + "-" + edificioStr;
                    Edificio edificio = edificioCache.computeIfAbsent(edificioKey, k ->
                            edificioRepository.findByCampusIdAndNombre(campus.getId(), edificioStr)
                                    .orElseThrow(() -> new IllegalArgumentException("El Edificio '" + edificioStr + "' no existe en ese campus."))
                    );

                    // Clave compuesta edificioId+espacio para soportar espacios con igual nombre en distintos edificios
                    String espacioKey = edificio.getId() + "-" + espacioStr;
                    Espacio espacio = espacioCache.computeIfAbsent(espacioKey, k ->
                            espacioRepository.findByEdificioIdAndNombreEspacio(edificio.getId(), espacioStr)
                                    .orElseThrow(() -> new IllegalArgumentException("El Espacio '" + espacioStr + "' no existe en ese edificio."))
                    );

                    // 6. Construir el activo si todas las validaciones pasaron
                    Assets asset = new Assets();
                    asset.setEtiqueta(etiqueta);
                    asset.setNumeroSerie(serie);
                    asset.setTipoActivo(tipoActivo);
                    asset.setEspacio(espacio);
                    asset.setFechaAlta(LocalDate.now());
                    asset.setEsActivo(true);
                    asset.setEstadoCustodia("Disponible");
                    asset.setEstadoOperativo("OK");

                    activosGuardar.add(asset);

                } catch (IllegalArgumentException e) {
                    log.error("Error en fila {}: {}", (row.getRowNum() + 1), e.getMessage());
                    errores.add("Fila " + (row.getRowNum() + 1) + ": " + e.getMessage());
                }
            }

            // 7. Guardar en BD solo los activos que pasaron todas las validaciones (inserción parcial)
            if (!activosGuardar.isEmpty())
                assetsRepository.saveAll(activosGuardar);

            // 8. Construir el mensaje cumpliendo los criterios de aceptación
            int inserciones = activosGuardar.size();
            int rechazos = errores.size();

            StringBuilder mensaje = new StringBuilder();
            mensaje.append("Importación realizada exitosamente.\n");
            mensaje.append("Total de inserciones: ").append(inserciones).append("\n");
            mensaje.append("Total de rechazos: ").append(rechazos);

            // Si hubo rechazos, anexamos el detalle para que el usuario sepa qué filas fallaron
            if (rechazos > 0) {
                mensaje.append("\n\nDetalle de rechazos:\n").append(String.join("\n", errores));

                // Retornamos OK porque el proceso terminó bien, pero mandamos flag `true` de error
                // para que el frontend sepa que hubo filas omitidas (o puedes cambiar a BAD_REQUEST según tu API).
                return new ApiResponse(mensaje.toString(), true, HttpStatus.OK);
            }

            // Si todo fue perfecto y no hubo rechazos
            return new ApiResponse(mensaje.toString(), false, HttpStatus.OK);

        } catch (IOException e) {
            log.error("Error al leer el archivo Excel", e);
            return new ApiResponse("Error interno al leer el archivo Excel", true, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Extrae el valor de una celda de Excel de forma segura, independientemente de su tipo.
     *
     * @param cell Celda a evaluar
     * @return Valor de la celda como String (sin espacios al inicio/fin)
     */
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {

            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {

                if (DateUtil.isCellDateFormatted(cell))
                    yield cell.getDateCellValue().toString();

                double val = cell.getNumericCellValue();

                if (val == (long) val)
                    yield String.valueOf((long) val); // Elimina el .0 innecesario en enteros

                else
                    yield String.valueOf(val);

            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    /**
     * Verifica si una fila del archivo Excel está completamente vacía.
     * Se usa para ignorar filas en blanco intercaladas sin generar errores de validación.
     *
     * @param row Fila a evaluar
     * @return true si la fila está vacía o es nula, false en caso contrario
     */
    private boolean isRowEmpty(Row row) {
        if (row == null) return true;
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK && !getCellValue(cell).isEmpty())
                return false;
        }
        return true;
    }
}