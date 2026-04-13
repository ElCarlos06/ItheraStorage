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
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicInteger;

import java.time.LocalDate;
import java.util.LinkedHashMap;

/**
 * Servicio especializado en la importación masiva de activos desde archivos Excel (.xlsx).
 * Implementa validaciones exhaustivas, manejo de errores detallado y optimizaciones
 * para garantizar una importación eficiente y confiable.
 *
 * <p><b>Formato esperado del archivo .xlsx (fila 1 = encabezados, datos desde fila 2):</b></p>
 * <pre>
 * Número de Serie* | Campus* | Tipo de Activo* | Marca** | Bien** | Modelo** | Edificio* | Espacio*
 * (* obligatorio siempre, ** obligatorio solo si el Tipo de Activo no existe en catálogo — se crea automáticamente)
 * La Etiqueta se genera automáticamente con la misma lógica del alta manual.
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
        // Columnas: [0]=fila, [1]=Serie, [2]=Campus, [3]=TipoActivo,
        //           [4]=Marca, [5]=Bien, [6]=Modelo, [7]=Edificio, [8]=Espacio
        //           (Etiqueta se genera automáticamente)
        Set<String> seriesLote  = new HashSet<>();
        Set<String> campusLote  = new HashSet<>();
        Set<String> nombresLote = new HashSet<>();

        // Guardamos datos de creación de tipo: nombre → {marca, bien, modelo}
        // (primera aparición por nombre, ya que nombre es único)
        Map<String, String[]> tipoCreacionDatos = new LinkedHashMap<>();

        for (String[] r : batch) {
            if (!r[1].isEmpty()) seriesLote.add(r[1]);
            if (!r[2].isEmpty()) campusLote.add(r[2]);
            if (!r[3].isEmpty()) {
                nombresLote.add(r[3]);
                tipoCreacionDatos.putIfAbsent(r[3], new String[]{r[4], r[5], r[6]});
            }
        }

        // Solo series de activos ACTIVOS bloquean la importación
        Set<String> seriesEnBD = seriesLote.isEmpty() ? new HashSet<>() :
                assetsRepository.findSeriesExistentes(seriesLote);

        // Activos desactivados con esas series → se reactivarán en vez de rechazarse
        Map<String, Assets> inactivosMap = seriesLote.isEmpty() ? new HashMap<>() :
                assetsRepository.findInactivosByNumeroSerieIn(seriesLote).stream()
                .collect(Collectors.toMap(Assets::getNumeroSerie, Function.identity()));

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

        // Buscar tipos existentes por nombre (único en BD)
        Map<String, TipoActivo> tipoCache = new HashMap<>(
                nombresLote.isEmpty() ? new HashMap<>() :
                tipoActivoRepository.findByNombreIn(nombresLote).stream()
                .collect(Collectors.toMap(TipoActivo::getNombre, Function.identity()))
        );

        // Auto-crear tipos que no existen si se proporcionó Marca + Bien + Modelo
        List<TipoActivo> nuevosTipos = new ArrayList<>();
        for (Map.Entry<String, String[]> entry : tipoCreacionDatos.entrySet()) {
            String nombre = entry.getKey();
            if (tipoCache.containsKey(nombre)) continue;

            String[] datos = entry.getValue();
            String marca  = datos[0];
            String bien   = datos[1];
            String modelo = datos[2];

            if (!marca.isEmpty() && !bien.isEmpty() && !modelo.isEmpty()) {
                TipoActivo nuevo = new TipoActivo();
                nuevo.setNombre(nombre);
                nuevo.setMarca(marca);
                nuevo.setTipoBien(bien);
                nuevo.setModelo(modelo);
                nuevo.setEsActivo(true);
                nuevosTipos.add(nuevo);
            }
        }

        if (!nuevosTipos.isEmpty()) {
            tipoActivoRepository.saveAll(nuevosTipos);
            nuevosTipos.forEach(t -> tipoCache.put(t.getNombre(), t));
            log.info("Importación: {} tipo(s) de activo creado(s) automáticamente.", nuevosTipos.size());
        }

        List<Assets> activosNuevos      = new ArrayList<>();
        List<Assets> activosReactivados = new ArrayList<>();

        for (String[] r : batch) {
            int rowNum = Integer.parseInt(r[0]) + 1;
            String serie = r[1];
            try {
                if (!serie.isEmpty() && inactivosMap.containsKey(serie) && !seriesVistas.contains(serie)) {
                    // Activo previamente desactivado → reactivar
                    activosReactivados.add(reactivarAsset(inactivosMap.get(serie), r, tipoCache, campusCache, edificioCache, espacioCache));
                    seriesVistas.add(serie);
                } else {
                    activosNuevos.add(buildAsset(r, seriesVistas, seriesEnBD, tipoCache, campusCache, edificioCache, espacioCache));
                }
            } catch (ImportException e) {
                log.error("Error en fila {}: {}", rowNum, e.getMessage());
                errores.add("Fila " + rowNum + ": " + e.getMessage());
            }
        }

        if (!activosNuevos.isEmpty()) {
            assetsRepository.saveAll(activosNuevos);
            bitacoraService.registrarEvento(activosNuevos);
            activosNuevos.forEach(a -> seriesEnBD.add(a.getNumeroSerie()));
        }

        if (!activosReactivados.isEmpty()) {
            assetsRepository.saveAll(activosReactivados);
            log.info("Importación: {} activo(s) desactivado(s) reactivado(s).", activosReactivados.size());
        }

        batch.clear();
        return activosNuevos.size() + activosReactivados.size();
    }

    private Assets buildAsset(String[] r,
                              Set<String> seriesVistas,
                              Set<String> seriesEnBD,
                              Map<String, TipoActivo> tipoCache, Map<String, Campus> campusCache,
                              Map<String, Edificio> edificioCache, Map<String, Espacio> espacioCache) throws ImportException {

        // Columnas: [0]=fila, [1]=Serie, [2]=Campus, [3]=TipoActivo,
        //           [4]=Marca*, [5]=Bien*, [6]=Modelo*, [7]=Edificio, [8]=Espacio
        //           (Etiqueta se genera automáticamente como en el alta manual)
        String serie       = r[1];
        String campusStr   = r[2];
        String tipoNombre  = r[3];
        String edificioStr = r[7];
        String espacioStr  = r[8];

        if (serie.isEmpty() || campusStr.isEmpty() || tipoNombre.isEmpty() ||
            edificioStr.isEmpty() || espacioStr.isEmpty())
            throw new ImportException("Faltan campos obligatorios (Serie, Campus, Tipo, Edificio, Espacio).");

        if (!seriesVistas.add(serie))
            throw new ImportException("El número de serie '" + serie + "' está repetido en el archivo.");

        if (seriesEnBD.contains(serie))
            throw new ImportException("El número de serie '" + serie + "' ya existe en el sistema.");

        TipoActivo tipoActivo = tipoCache.get(tipoNombre);
        if (tipoActivo == null)
            throw new ImportException("El tipo de activo '" + tipoNombre + "' no existe en el catálogo y no se proporcionaron Marca, Bien y Modelo para crearlo.");

        Campus campus = campusCache.get(campusStr);
        if (campus == null)
            throw new ImportException("El campus '" + campusStr + "' no existe.");

        Edificio edificio = edificioCache.get(campus.getId() + "-" + edificioStr);
        if (edificio == null)
            throw new ImportException("El edificio '" + edificioStr + "' no existe en el campus '" + campusStr + "'.");

        Espacio espacio = espacioCache.get(edificio.getId() + "-" + espacioStr);
        if (espacio == null)
            throw new ImportException("El espacio '" + espacioStr + "' no existe en el edificio '" + edificioStr + "'.");

        // Generar etiqueta automáticamente con la misma lógica del alta manual
        String etiqueta = generarEtiqueta(tipoActivo, campusStr, edificioStr, espacioStr);

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

    /**
     * Reactiva un activo que estaba desactivado (esActivo = false) actualizando su
     * ubicación y tipo según los datos de la fila del Excel.
     */
    private Assets reactivarAsset(Assets activo, String[] r,
                                   Map<String, TipoActivo> tipoCache,
                                   Map<String, Campus> campusCache,
                                   Map<String, Edificio> edificioCache,
                                   Map<String, Espacio> espacioCache) throws ImportException {

        String campusStr   = r[2];
        String tipoNombre  = r[3];
        String edificioStr = r[7];
        String espacioStr  = r[8];

        if (campusStr.isEmpty() || tipoNombre.isEmpty() || edificioStr.isEmpty() || espacioStr.isEmpty())
            throw new ImportException("Faltan campos obligatorios (Campus, Tipo, Edificio, Espacio).");

        TipoActivo tipo = tipoCache.get(tipoNombre);
        if (tipo == null)
            throw new ImportException("El tipo de activo '" + tipoNombre + "' no existe.");

        Campus campus = campusCache.get(campusStr);
        if (campus == null)
            throw new ImportException("El campus '" + campusStr + "' no existe.");

        Edificio edificio = edificioCache.get(campus.getId() + "-" + edificioStr);
        if (edificio == null)
            throw new ImportException("El edificio '" + edificioStr + "' no existe en el campus '" + campusStr + "'.");

        Espacio espacio = espacioCache.get(edificio.getId() + "-" + espacioStr);
        if (espacio == null)
            throw new ImportException("El espacio '" + espacioStr + "' no existe en el edificio '" + edificioStr + "'.");

        activo.setEsActivo(true);
        activo.setTipoActivo(tipo);
        activo.setEspacio(espacio);
        activo.setFechaAlta(LocalDate.now());
        activo.setEstadoCustodia("Disponible");
        activo.setEstadoOperativo("OK");
        return activo;
    }

    /**
     * Genera la etiqueta del activo con la misma lógica que el alta manual:
     * 2 chars del nombre + 2 chars de la marca + inicial de espacio + edificio + campus + UUID 6 chars.
     */
    private String generarEtiqueta(TipoActivo tipo, String campusStr, String edificioStr, String espacioStr) {
        String nombre = tipo.getNombre() != null ? tipo.getNombre() : "";
        String marca  = tipo.getMarca()  != null ? tipo.getMarca()  : "";

        String tagNombre   = nombre.length() >= 2 ? nombre.substring(0, 2).toUpperCase() : (nombre + "X").toUpperCase();
        String tagMarca    = marca.length()  >= 2 ? marca.substring(0, 2).toUpperCase()  : (marca  + "X").toUpperCase();
        char   tagEspacio  = !espacioStr.isEmpty()  ? Character.toUpperCase(espacioStr.charAt(0))  : 'X';
        char   tagEdificio = !edificioStr.isEmpty() ? Character.toUpperCase(edificioStr.charAt(0)) : 'X';
        char   tagCampus   = !campusStr.isEmpty()   ? Character.toUpperCase(campusStr.charAt(0))   : 'X';

        String uid = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return tagNombre + tagMarca + tagEspacio + tagEdificio + tagCampus + "-" + uid;
    }

}