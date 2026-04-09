package mx.edu.utez.modules.core.imports;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.edu.utez.modules.core.assets.Assets;
import mx.edu.utez.modules.core.assets.AssetsRepository;
import mx.edu.utez.modules.reporting.bitacora.BitacoraService;
import mx.edu.utez.modules.location.campus.Campus;
import mx.edu.utez.modules.location.campus.CampusRepository;
import mx.edu.utez.modules.location.edificios.Edificio;
import mx.edu.utez.modules.location.edificios.EdificioRepository;
import mx.edu.utez.modules.location.espacios.Espacio;
import mx.edu.utez.modules.location.espacios.EspacioRepository;
import mx.edu.utez.modules.core.tipo_activos.TipoActivo;
import mx.edu.utez.modules.core.tipo_activos.TipoActivoRepository;
import mx.edu.utez.util.CustomException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicInteger;

import java.time.LocalDate;

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
    private final BitacoraService bitacoraService;
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
    public ImportResult save(MultipartFile file) {

        final String SHEETS_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

        if (file == null || file.isEmpty())
            throw new CustomException("Archivo no proporcionado o vacío");

        if (!SHEETS_TYPE.equals(file.getContentType())) // || Objects.requireNonNull(file.getOriginalFilename()).toLowerCase().endsWith(".xlsx") si no jalaba era por esto XD
            throw new CustomException("Tipo de archivo no soportado. Debe ser .xlsx o similar");

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
     * Se encarga de procesar el acrhivo de excel, y aplicar las reglas de negocio que estan en el DFR.
     * @param file Tipo <code>Multifile</code> que es el archiivo de excel.
     * @return <code>ApiResponse</code> o sea un resumen de las inserciones rechazos (si hubo) y su respectivo estatus Http
     */
    private ImportResult handleExcel(MultipartFile file) throws ImportException {

        AtomicInteger inserciones   = new AtomicInteger();
        List<String>  errores       = new ArrayList<>();

        Set<String> etiquetasVistas = new HashSet<>();
        Set<String> seriesVistas    = new HashSet<>();

        List<String[]> batch = new ArrayList<>();
        final int BATCH_SIZE = 500; // Procesamos de 500 en 500 para no reventar la RAM

        try(InputStream inputStream = file.getInputStream(); // Aqui lo abrimos en un inputStream
            OPCPackage pkg      = OPCPackage.open(inputStream) // Y aqui lo convertimos a un paquete de excel, se llama PKG PQ POR LO GENERLA SE COMPRIMEN EN ZIP LOS EXCEL Xd
        ) {
            XSSFReader xssfReader = new XSSFReader(pkg);
            StylesTable styles  = xssfReader.getStylesTable();
            
            // Obtiene la tabla de strings compartidos, que es donde se guardan los valores de texto para optimizar el tamaño del archivo.
            ReadOnlySharedStringsTable sst = new ReadOnlySharedStringsTable(pkg);
            
            // Se encarga de parsear el xml compreso que esta en ls archivo sde excel, y o cargar la ram xd
            XMLReader parser = XMLHelper.newXMLReader();

            // El handler es el que se encarga de convertir los eventos del parser en filas de strings planos, y pasarlas al lote
            parser.setContentHandler(new XSSFSheetXMLHandler(
                    styles, null, sst,
                    new RowCollectorHandler(row -> {
                        batch.add(row);

                        if (batch.size() >= BATCH_SIZE)
                            inserciones.addAndGet(processBatch(batch, errores, etiquetasVistas, seriesVistas));

                    }),
                    new DataFormatter(),
                    false
            ));

            XSSFReader.SheetIterator sheets = (XSSFReader.SheetIterator) xssfReader.getSheetsData();

            // si tiene otra hoja pide que la lea, pero como el DFR solo habla de una hoja
            // solo se procesa la primera y ya, si hay más se ignoran
            if (sheets.hasNext())
                parser.parse(new InputSource(sheets.next())); // es la primera hoja del fokin excel

            if (!batch.isEmpty()) // Si al terminar de leer el archivo quedan filas en el lote, las procesamos
                inserciones.addAndGet(processBatch(batch, errores, etiquetasVistas, seriesVistas));

        } catch (Exception e) {
            log.error("Error al leer el archivo Excel", e);
            throw new CustomException("Error interno al leer el archivo Excel", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (inserciones.get() == 0 && errores.isEmpty())
            throw new CustomException("El archivo no contiene datos.");


        // FASE 6: RESPUESTA
        // Mandarle el mensajemediante ImportResult pal front y ya xD
        return new ImportResult(inserciones.get(), errores.size(), errores);
    }

    private int processBatch(List<String[]> batch, List<String> errores, Set<String> etiquetasVistas, Set<String> seriesVistas) {
        Set<String> etiquetasLote = new HashSet<>();
        Set<String> seriesLote    = new HashSet<>();
        Set<String> campusLote    = new HashSet<>();
        Set<String> tipoKeyLote   = new HashSet<>(); 

        // Recolectar llaves foráneas y datos únicos de las filas de este lote 
        // para hacer búsquedas masivas en la BD y evitar hacer 1000 iteraciones
        for (String[] r : batch) {
            if (!r[1].isEmpty()) etiquetasLote.add(r[1]);
            if (!r[2].isEmpty()) seriesLote.add(r[2]);
            if (!r[7].isEmpty()) campusLote.add(r[7]);
            if (!r[3].isEmpty() && !r[4].isEmpty() && !r[5].isEmpty() && !r[6].isEmpty())
                tipoKeyLote.add(r[3] + "-" + r[4] + "-" + r[5] + "-" + r[6]);
        }

        Set<String> etiquetasEnBD = etiquetasLote.isEmpty() ? new HashSet<>() :
                assetsRepository.findEtiquetasExistentes(etiquetasLote);
        Set<String> seriesEnBD = seriesLote.isEmpty() ? new HashSet<>() :
                assetsRepository.findSeriesExistentes(seriesLote);

        Map<String, Campus> campusCache = campusRepository.findByNombreIn(campusLote)
                .stream().collect(Collectors.toMap(Campus::getNombre, Function.identity()));

        Set<Long> campusIds = campusCache.values().stream()
                .map(Campus::getId).collect(Collectors.toSet());

        Map<String, Edificio> edificioCache = campusIds.isEmpty() ? new HashMap<>() :
                edificioRepository.findByCampusIdIn(campusIds).stream()
                .collect(Collectors.toMap(
                        e -> e.getCampus().getId() + "-" + e.getNombre(),
                        Function.identity()
                ));

        Set<Long> edificioIds = edificioCache.values().stream()
                .map(Edificio::getId).collect(Collectors.toSet());

        Map<String, Espacio> espacioCache = edificioIds.isEmpty() ? new HashMap<>() :
                espacioRepository.findByEdificioIdIn(edificioIds).stream()
                .collect(Collectors.toMap(
                        e -> e.getEdificio().getId() + "-" + e.getNombreEspacio(),
                        Function.identity()
                ));

        Map<String, TipoActivo> tipoCache = tipoKeyLote.isEmpty() ? new HashMap<>() :
                tipoActivoRepository.findByCompositeKeys(tipoKeyLote).stream()
                .collect(Collectors.toMap(
                        tA -> tA.getNombre() + "-" + tA.getMarca() + "-" +
                                tA.getTipoBien() + "-" + tA.getModelo(),
                        Function.identity()
                ));

        List<Assets> activosGuardar = new ArrayList<>();

        for (String[] r : batch) {
            int rowNum = Integer.parseInt(r[0]) + 1;
            try {
                activosGuardar.add(buildAsset(r, etiquetasVistas, seriesVistas, etiquetasEnBD, seriesEnBD, tipoCache, campusCache, edificioCache, espacioCache));
            } catch (ImportException e) {
                log.error("Error en fila {}: {}", rowNum, e.getMessage());
                errores.add("Fila " + rowNum + ": " + e.getMessage());
            }
        }

        // ── FASE 5: PERSISTENCIA
        // Terminamos de validar, ahora a guardar todo ya a la ñonga wn
        // ──────────────────────────────────────────────────────────────────────
        if (!activosGuardar.isEmpty()) {
            assetsRepository.saveAll(activosGuardar);
            bitacoraService.registrarEvento(activosGuardar);
            
            // Register successfully saved assets mapping tags to DB logic across batches if needed
            // Registramos lo que se acaba de guardar para que el siguiente lote sepa que ya existe
            for(Assets a: activosGuardar) {
                etiquetasEnBD.add(a.getEtiqueta());
                seriesEnBD.add(a.getNumeroSerie());
            }
        }
        
        batch.clear();
        return activosGuardar.size();
    }

    private Assets buildAsset(String[] r, 
                              Set<String> etiquetasVistas, Set<String> seriesVistas, 
                              Set<String> etiquetasEnBD, Set<String> seriesEnBD, 
                              Map<String, TipoActivo> tipoCache, Map<String, Campus> campusCache, 
                              Map<String, Edificio> edificioCache, Map<String, Espacio> espacioCache) throws ImportException {
        
        String etiqueta    = r[1];
        String serie       = r[2];
        String nombreStr   = r[3];
        String marcaStr    = r[4];
        String bienStr     = r[5];
        String modeloStr   = r[6];
        String campusStr   = r[7];
        String edificioStr = r[8];
        String espacioStr  = r[9];

        if (etiqueta.isEmpty() || serie.isEmpty() || nombreStr.isEmpty() || marcaStr.isEmpty() || 
            bienStr.isEmpty() || modeloStr.isEmpty() || campusStr.isEmpty() || edificioStr.isEmpty() || espacioStr.isEmpty())
            throw new ImportException("Faltan campos obligatorios (*)");

        if (!etiquetasVistas.add(etiqueta))

            throw new ImportException("La etiqueta '" + etiqueta + "' está repetida en el archivo.");
        if (!seriesVistas.add(serie))
            throw new ImportException("El número de serie '" + serie + "' está repetido en el archivo.");

        if (etiquetasEnBD.contains(etiqueta))
            throw new ImportException("La etiqueta '" + etiqueta + "' ya existe en el sistema.");

        if (seriesEnBD.contains(serie))
            throw new ImportException("El número de serie '" + serie + "' ya existe en el sistema.");

        String tipoKey = nombreStr + "-" + marcaStr + "-" + bienStr + "-" + modeloStr;
        TipoActivo tipoActivo = tipoCache.get(tipoKey);
        if (tipoActivo == null)
            throw new ImportException("No existe el TipoActivo con nombre '" + nombreStr + 
                    "', marca '" + marcaStr + "', bien '" + bienStr + "' y modelo '" + modeloStr + "'.");

        Campus campus = campusCache.get(campusStr);
        if (campus == null)
            throw new ImportException("El Campus '" + campusStr + "' no existe.");

        Edificio edificio = edificioCache.get(campus.getId() + "-" + edificioStr);
        if (edificio == null)
            throw new ImportException("El Edificio '" + edificioStr + "' no existe en ese campus.");

        Espacio espacio = espacioCache.get(edificio.getId() + "-" + espacioStr);
        if (espacio == null)
            throw new ImportException("El Espacio '" + espacioStr + "' no existe en ese edificio.");

        Assets asset = new Assets();
        asset.setEtiqueta(etiqueta);
        asset.setNumeroSerie(serie);
        asset.setTipoActivo(tipoActivo);
        asset.setEspacio(espacio);
        asset.setFechaAlta(LocalDate.now());
        asset.setEsActivo(true);
        asset.setEstadoCustodia("Disponible");
        asset.setEstadoOperativo("OK");

        return asset;
    }

}