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
import mx.edu.utez.modules.marcas.Marca;
import mx.edu.utez.modules.marcas.MarcaRepository;
import mx.edu.utez.modules.modelos.Modelo;
import mx.edu.utez.modules.modelos.ModeloRepository;
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
 * Implementa validaciones exhaustivas, manejo de errores detallado y optimizaciones para garantizar una importación eficiente y confiable.
 *  @author: Ithera Team
 */
@Log4j2
@Service
@AllArgsConstructor
public class ImportService {

    private final AssetsRepository assetsRepository;
    private final TipoActivoRepository tipoActivoRepository;
    private final MarcaRepository marcaRepository;
    private final ModeloRepository modeloRepository;
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
            return new ApiResponse("Tipo de archivo no soportado. Debe ser .xlsx", true, HttpStatus.BAD_REQUEST);

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
    private ApiResponse handleExcel(MultipartFile file) {
        List<Assets> activosGuardar = new ArrayList<>();
        List<String> errores = new ArrayList<>();

        Set<String> etiquetasEnLote = new HashSet<>();
        Set<String> seriesEnLote = new HashSet<>();

        final Map<String, TipoActivo> tipoCache = new HashMap<>();
        final Map<String, Marca> marcaCache = new HashMap<>();
        final Map<String, Modelo> modeloCache = new HashMap<>();
        final Map<String, Campus> campusCache = new HashMap<>();
        final Map<String, Edificio> edificioCache = new HashMap<>();
        final Map<String, Espacio> espacioCache = new HashMap<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            // Iterar filas
            for (Row row : sheet) {

                if (row.getRowNum() == 0 || isRowEmpty(row))
                    continue; // Saltar cabecera y filas vacías


                try {
                    // 1. Extraer valores básicos
                    String etiqueta = getCellValue(row.getCell(0));
                    String serie = getCellValue(row.getCell(1));
                    String tipoStr = getCellValue(row.getCell(2));
                    String marcaStr = getCellValue(row.getCell(3));
                    String modeloStr = getCellValue(row.getCell(4));
                    String campusStr = getCellValue(row.getCell(5));
                    String edificioStr = getCellValue(row.getCell(6));
                    String espacioStr = getCellValue(row.getCell(7));

                    // 2. Validar campos obligatorios
                    if (etiqueta.isEmpty() ||
                            serie.isEmpty() ||
                            tipoStr.isEmpty() ||
                            marcaStr.isEmpty() ||
                            modeloStr.isEmpty() ||
                            campusStr.isEmpty() ||
                            edificioStr.isEmpty() ||
                            espacioStr.isEmpty()
                    )
                        throw new IllegalArgumentException("Faltan campos obligatorios (*)");


                    // 3. Validar duplicados en el LOTE (usando HashSet)
                    if (!etiquetasEnLote.add(etiqueta))
                        throw new IllegalArgumentException("La etiqueta '" + etiqueta + "' está repetida en el archivo.");

                    if (!seriesEnLote.add(serie))
                        throw new IllegalArgumentException("El número de serie '" + serie + "' está repetido en el archivo.");


                    // 4. Validar duplicados en la BD
                    if (assetsRepository.existsByEtiqueta(etiqueta))
                        throw new IllegalArgumentException("La etiqueta '" + etiqueta + "' ya existe en el sistema.");

                    if (assetsRepository.existsByNumeroSerie(serie))
                        throw new IllegalArgumentException("El número de serie '" + serie + "' ya existe en el sistema.");


                    // 5. Resolución de catálogos optimizada (usando HashMap Caché)
                    TipoActivo tipoActivo = tipoCache.computeIfAbsent(tipoStr, k ->
                            tipoActivoRepository.findByNombre(k).orElseThrow(() -> new IllegalArgumentException("El Tipo '" + k + "' no existe."))
                    );

                    Marca marca = marcaCache.computeIfAbsent(marcaStr, k ->
                            marcaRepository.findByNombre(k).orElseThrow(() -> new IllegalArgumentException("La Marca '" + k + "' no existe."))
                    );

                    String modeloKey = marca.getId() + "-" + modeloStr;
                    Modelo modelo = modeloCache.computeIfAbsent(modeloKey, k ->
                            modeloRepository.findFirstByMarcaIdAndNombre(marca.getId(), modeloStr)
                                    .orElseThrow(() -> new IllegalArgumentException("El Modelo '" + modeloStr + "' no existe para esa marca."))
                    );

                    Campus campus = campusCache.computeIfAbsent(campusStr, k ->
                            campusRepository.findByNombre(k).orElseThrow(() -> new IllegalArgumentException("El Campus '" + k + "' no existe."))
                    );

                    String edificioKey = campus.getId() + "-" + edificioStr; // Llave compuesta
                    Edificio edificio = edificioCache.computeIfAbsent(edificioKey, k ->
                            edificioRepository.findByCampusIdAndNombre(campus.getId(), edificioStr)
                                    .orElseThrow(() -> new IllegalArgumentException("El Edificio '" + edificioStr + "' no existe en ese campus."))
                    );

                    String espacioKey = edificio.getId() + "-" + espacioStr; // Llave compuesta
                    Espacio espacio = espacioCache.computeIfAbsent(espacioKey, k ->
                            espacioRepository.findByEdificioIdAndNombreEspacio(edificio.getId(), espacioStr)
                                    .orElseThrow(() -> new IllegalArgumentException("El Espacio '" + espacioStr + "' no existe en ese edificio."))
                    );

                    // 6. Construir el objeto si todo es válido
                    Assets asset = new Assets();
                    asset.setEtiqueta(etiqueta);
                    asset.setNumeroSerie(serie);
                    asset.setTipoActivo(tipoActivo);
                    asset.setModelo(modelo);
                    asset.setEspacio(espacio);
                    asset.setFechaAlta(LocalDate.now());
                    asset.setEsActivo(true);
                    asset.setEstadoCustodia("Disponible");
                    asset.setEstadoOperativo("OK");

                    activosGuardar.add(asset);

                } catch (IllegalArgumentException e) {
                    errores.add("Fila " + (row.getRowNum() + 1) + ": " + e.getMessage());
                }
            }

            // 7. Decisión final: Guardar o Rechazar todo
            if (!errores.isEmpty())
                return new ApiResponse("Errores detectados. No se guardó nada.\n" + String.join("\n", errores), true, HttpStatus.BAD_REQUEST);


            assetsRepository.saveAll(activosGuardar);
            return new ApiResponse("Importación exitosa. Se registraron " + activosGuardar.size() + " activos.", false, HttpStatus.OK);

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
                    yield cell.getDateCellValue().toString(); // Al parecer yield retirna el valor, algo que solo funcion a en un switch XD

                double val = cell.getNumericCellValue();

                if (val == (long) val)
                    yield String.valueOf((long) val); // Quita el .0 de los enteros

                else
                    yield String.valueOf(val);

            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    /**
     * Verifica si una fila del archivo Excel está completamente vacía.
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