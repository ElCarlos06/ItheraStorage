package mx.edu.utez.modules.assets;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.espacios.Espacio;
import mx.edu.utez.modules.espacios.EspacioRepository;
import mx.edu.utez.modules.imagen_activo.ImagenActivo;
import mx.edu.utez.modules.imagen_activo.ImagenActivoRepository;
import mx.edu.utez.modules.bitacora.BitacoraService;
import mx.edu.utez.modules.resguardos.ResguardoRepository;
import mx.edu.utez.modules.tipo_activos.TipoActivo;
import mx.edu.utez.modules.tipo_activos.TipoActivoRepository;
import mx.edu.utez.modules.qr.QRService;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

/**
 * Servicio de negocio para la gestión de Activos Fijos.
 * Contiene la lógica para registrar, consultar, actualizar y desactivar activos,
 * así como para enriquecer la respuesta con imágenes asociadas.
 *
 * @author Ithera Team
 */
@AllArgsConstructor
@Service
public class AssetsService {

    private final AssetsRepository assetsRepository;
    private final BitacoraService bitacoraService;
    private final TipoActivoRepository tipoActivoRepository;
    private final EspacioRepository espacioRepository;
    private final ResguardoRepository resguardosRepository;
    // Inyectamos repositorio de imágenes para detalle extendido
    private final ImagenActivoRepository imagenActivoRepository;
    private final QRService qrService;

    private static final String QR_FILENAME = "QR_CODE";

    /**
     * Obtiene una lista paginada de todos los activos que tienen estatus 'activo' (true).
     *
     * @param pageable Configuración de paginación.
     * @return ApiResponse con la página de activos encontrados.
     */
    @Transactional(readOnly = true)
    public ApiResponse findAll(Pageable pageable) {

        Page<Assets> page = assetsRepository.findByEsActivoTrue(pageable);
        List<String> estados = List.of("Pendiente", "Confirmado");

        // Mapear cada activo a DTO con su resguardo activo
        Page<AssetsDTO> dtoPage = page.map(asset -> {
            AssetsDTO dto = toDTO(asset);

            // Buscar resguardo activo (Pendiente o Confirmado)
            resguardosRepository
                    .findFirstByActivoIdAndEstadoResguardoIn(
                            asset.getId(),
                            estados
                    )
                    .ifPresent(r -> {
                        String nombre = null;
                        if (r.getUsuarioEmpleado() != null) {
                            nombre = r.getUsuarioEmpleado().getNombreCompleto();
                        }
                        dto.setAsignadoA(nombre);
                        dto.setIdResguardo(r.getId());
                    });

            return dto;
        });

        return new ApiResponse("OK", dtoPage, HttpStatus.OK);
    }

    /**
     * Busca un activo por ID y enriquece la respuesta con las URLs de sus imágenes asociadas.
     *
     * @param id Identificador del activo.
     * @return ApiResponse con el DTO del activo (incluyendo imágenes) o mensaje de error.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "assets", key = "#id", unless = "#result.error")
    public ApiResponse findById(Long id) {
        Optional<Assets> found = assetsRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Activo no encontrado", true, HttpStatus.NOT_FOUND);

        Assets asset = found.get();
        AssetsDTO dto = toDTO(asset);
        
        // Enriquecer DTO con imágenes
        enrichWithImages(dto);
        
        return new ApiResponse("OK", dto, HttpStatus.OK);
    }

    /**
     * Enriquece el DTO del activo con las URLs de imágenes de perfil.
     * Excluye la imagen que corresponde al código QR generado.
     *
     * @param dto DTO del activo a enriquecer.
     */
    private void enrichWithImages(AssetsDTO dto) {
        if (dto.getId() == null) return;
        
        // Imágenes Activo (excluyendo QR)
        List<String> perfilImgs = imagenActivoRepository.findByActivoId(dto.getId()).stream()
                .filter(img -> !QR_FILENAME.equals(img.getNombreArchivo()))
                .map(ImagenActivo::getUrlCloudinary)
                .toList();
        dto.setImagenesPerfil(perfilImgs);
    }
    
    /**
     * Convierte una entidad Assets a su DTO correspondiente.
     *
     * @param entity Entidad Assets.
     * @return AssetsDTO con los datos de la entidad.
     */
    private AssetsDTO toDTO(Assets entity) {
        AssetsDTO dto = new AssetsDTO();
        dto.setId(entity.getId());
        dto.setEtiqueta(entity.getEtiqueta());
        dto.setNumeroSerie(entity.getNumeroSerie());
        dto.setIdTipoActivo(entity.getTipoActivo().getId());
        dto.setIdEspacio(entity.getEspacio().getId());
        dto.setEstadoCustodia(entity.getEstadoCustodia());
        dto.setEstadoOperativo(entity.getEstadoOperativo());
        dto.setDescripcion(entity.getDescripcion());
        dto.setCosto(entity.getCosto());
        dto.setQrCodigo(entity.getQrCodigo());
        dto.setFechaAlta(entity.getFechaAlta().toString());
        dto.setEsActivo(entity.getEsActivo());

        dto.setTipoActivo(entity.getTipoActivo());   // nombre, marca, modelo, tipoBien
        dto.setEspacio(entity.getEspacio());         // nombreEspacio, edificio, campus

        // IDs para forms de edición
        dto.setIdTipoActivo(entity.getTipoActivo().getId());
        dto.setIdEspacio(entity.getEspacio().getId());

        return dto;
    }

    /**
     * Registra un nuevo activo en el sistema.
     * Valida unicidad de número de serie y existencia de catálogos (tipo, modelo, espacio).
     *
     * @param dto Datos del nuevo activo.
     * @return ApiResponse con el activo creado.
     */
    @Transactional
    @CacheEvict(value = {"assets", "assets_page"}, allEntries = true)
    public ApiResponse save(AssetsDTO dto) {
        if (assetsRepository.existsByNumeroSerie(dto.getNumeroSerie()))
            return new ApiResponse("Ya existe un activo con ese número de serie", true, HttpStatus.CONFLICT);
        Optional<TipoActivo> tipoActivo = tipoActivoRepository.findById(dto.getIdTipoActivo());
        if (tipoActivo.isEmpty())
            return new ApiResponse("Tipo de activo no encontrado", true, HttpStatus.NOT_FOUND);
        Optional<Espacio> espacio = espacioRepository.findById(dto.getIdEspacio());
        if (espacio.isEmpty())
            return new ApiResponse("Espacio no encontrado", true, HttpStatus.NOT_FOUND);

        Assets entity = new Assets();
        entity.setEtiqueta(truncate(dto.getEtiqueta(), 50));
        entity.setNumeroSerie(truncate(dto.getNumeroSerie(), 100));
        entity.setTipoActivo(tipoActivo.get());
        entity.setEspacio(espacio.get());
        entity.setEstadoCustodia(normalizeEstadoCustodia(dto.getEstadoCustodia() != null ? dto.getEstadoCustodia() : "Disponible"));
        entity.setEstadoOperativo(truncate(dto.getEstadoOperativo() != null ? dto.getEstadoOperativo() : "OK", 10));
        entity.setDescripcion(truncate(dto.getDescripcion(), 255));
        entity.setCosto(safeCosto(dto.getCosto()));
        entity.setQrCodigo(truncate(dto.getQrCodigo(), 255));
        entity.setFechaAlta(dto.getFechaAlta() != null ? LocalDate.parse(dto.getFechaAlta()) : LocalDate.now());
        entity.setEsActivo(true);
        assetsRepository.save(entity);
        bitacoraService.registrarEvento(
                entity.getId(), null,
                "Registro Activo",
                "Activo " + entity.getEtiqueta() + " registrado en el sistema",
                null, "Disponible", null, "OK"
        );
        return new ApiResponse("Activo registrado", entity, HttpStatus.CREATED);
    }

    /**
     * Actualiza la información de un activo existente.
     *
     * @param id Identificador del activo.
     * @param dto Nuevos datos a actualizar.
     * @return ApiResponse con el activo actualizado.
     */
    @Transactional
    @CacheEvict(value = {"assets", "assets_page"}, allEntries = true)
    public ApiResponse update(Long id, AssetsDTO dto) {
        Optional<Assets> found = assetsRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Activo no encontrado", true, HttpStatus.NOT_FOUND);
        Optional<TipoActivo> tipoActivo = tipoActivoRepository.findById(dto.getIdTipoActivo());
        if (tipoActivo.isEmpty())
            return new ApiResponse("Tipo de activo no encontrado", true, HttpStatus.NOT_FOUND);
        Optional<Espacio> espacio = espacioRepository.findById(dto.getIdEspacio());
        if (espacio.isEmpty())
            return new ApiResponse("Espacio no encontrado", true, HttpStatus.NOT_FOUND);

        Assets entity = found.get();
        String custAnt = entity.getEstadoCustodia();
        String opAnt = entity.getEstadoOperativo();
        entity.setEtiqueta(truncate(dto.getEtiqueta(), 50));
        entity.setNumeroSerie(truncate(dto.getNumeroSerie(), 100));
        entity.setTipoActivo(tipoActivo.get());
        entity.setEspacio(espacio.get());
        if (dto.getEstadoCustodia() != null) entity.setEstadoCustodia(normalizeEstadoCustodia(dto.getEstadoCustodia()));
        if (dto.getEstadoOperativo() != null) entity.setEstadoOperativo(truncate(dto.getEstadoOperativo(), 10));
        entity.setDescripcion(truncate(dto.getDescripcion(), 255));
        entity.setCosto(safeCosto(dto.getCosto()));
        entity.setQrCodigo(truncate(dto.getQrCodigo(), 255));
        if (dto.getEsActivo() != null) entity.setEsActivo(dto.getEsActivo());
        assetsRepository.save(entity);
        String custNuevo = entity.getEstadoCustodia();
        String opNuevo = entity.getEstadoOperativo();
        bitacoraService.registrarEvento(id, null, "Actualizacion Activo",
                "Actualización de datos del activo", custAnt, custNuevo, opAnt, opNuevo);
        return new ApiResponse("Activo actualizado", entity, HttpStatus.OK);
    }

    /**
     * Invalida la caché de un activo tras cambios de estado (custodia/operativo).
     * Llamar desde ResguardoService y ReporteService después de actualizar.
     *
     * @param id Identificador del activo al que se le aplicará el evict en caché.
     */
    @Caching(evict = {
        @CacheEvict(value = "assets", key = "#id"),
        @CacheEvict(value = "assets_page", allEntries = true)
    })
    public void evictAssetCache(Long id) {
        // La anotación realiza la evicción
    }

    /** Trunca un string al máximo de caracteres para evitar Data truncated en MySQL.
     *
     * @param s Cadena original a truncar.
     * @param maxLen Límite máximo permitido.
     * @return Cadena truncada de acuerdo con el límite estipulado.
     */
    private static String truncate(String s, int maxLen) {
        if (s == null) return null;
        if (s.length() <= maxLen) return s;
        return s.substring(0, maxLen);
    }

    /** Normaliza estado_custodia a valores exactos del ENUM en BD: Disponible | En Proceso | Resguardado | Baja
     *
     * @param s Cadena de estatus recibida desde el cliente.
     * @return Equivalente normalizado acorde a las constantes reales.
     */
    private static String normalizeEstadoCustodia(String s) {
        if (s == null) return "Disponible";
        String lower = s.trim().toLowerCase();
        if (lower.contains("disponible") || "disp".equals(lower)) return "Disponible";
        if (lower.contains("proceso") || "proc".equals(lower)) return "En Proceso";
        if (lower.contains("resguard") || "resg".equals(lower)) return "Resguardado";
        if (lower.contains("baja")) return "Baja";
        return s;
    }

    /** Redondea costo a 2 decimales y limita a DECIMAL(10,2) para evitar Data truncated.
     *
     * @param costo Valor original de tipo BigDecimal.
     * @return Valor decimal formateado de forma segura.
     */
    private static java.math.BigDecimal safeCosto(java.math.BigDecimal costo) {
        if (costo == null) return null;
        return costo.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Realiza una baja lógica del activo (cambia esActivo a false).
     *
     * @param id Identificador del activo.
     * @return ApiResponse confirmando la desactivación.
     */
    @Transactional
    @CacheEvict(value = {"assets", "assets_page"}, allEntries = true)
    public ApiResponse toggleStatus(Long id) {
        Optional<Assets> found = assetsRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Activo no encontrado", true, HttpStatus.NOT_FOUND);
        Assets entity = found.get();
        String custAnt = entity.getEstadoCustodia();
        String opAnt = entity.getEstadoOperativo();

        qrService.deleteQrByAssetId(id);
        assetsRepository.updateEstadoYActivo(id, "Disponible", false);
        entity.setEstadoCustodia("Disponible");
        entity.setEsActivo(false);
        bitacoraService.registrarEvento(id, null, "Baja Aprobada",
                "Activo dado de baja (desactivado)", custAnt, "Disponible", opAnt, "OK");
        return new ApiResponse("Activo desactivado", entity, HttpStatus.OK);
    }

    /**
     * Se encarga de juntar las estadisticas de los activos de esta semana vs la semana pasada, para luego calcular el porcentaje de cambio semanal.
     * @return <code>ApiResponse</code> con las estadisticas mapeadas
     */
    @Transactional(readOnly = true)
    public ApiResponse getAssetsStats() {
        // Semana actual
        // LocalDate hoy = LocalDate.now();
        // LocalDate inicioEstaSemana = hoy.with(DayOfWeek.MONDAY);
        // Obtiene la semana pasada a la actual xd
        LocalDate week =  LocalDate.now().minusWeeks(1);
        // Semana anterior del lunes a domingo del pasao
        // LocalDate inicioSemanaAnterior = inicioEstaSemana.minusWeeks(1);
        // LocalDate finSemanaAnterior = inicioEstaSemana.minusDays(1);

        // Proyecciones de la bd XD
        AssetsProjection global = assetsRepository.findAssetsStatsGlobal(); // Totales globales sin filtro de fecha
        AssetsProjection lastWeek = assetsRepository.findAssetsStatsOfLastWeek(week); // Totales de la semana anterior
        // AssetsProjection estaSemana = assetsRepository.findAssetsStatsByWeek(inicioEstaSemana, hoy);
        // AssetsProjection semanaAnterior = assetsRepository.findAssetsStatsByWeek(inicioSemanaAnterior, finSemanaAnterior);

        Map<String, Long> json = getJson(global, lastWeek); // La global y la semana anetrior

        return new ApiResponse("Estadísticas de Activos", json, HttpStatus.OK);
    }

    /**
     * Pone en un Map los resultados que obtuvimos de la qiuery y ya, si lo dejaba en getAssetStats se iba a ver bien largo XD
     * @param global Proyecciones globales de los activos actualmente
     * @param lastWeek Proyecciones de los activos de la semana pasada a la actual
     * @return Un <code>Map<String, Long></code> con los resultados mapeaos
     */
    private Map<String, Long> getJson(AssetsProjection global, AssetsProjection lastWeek) {
        Map<String, Long> json = new HashMap<>();

        // Valores actuales globales
        json.put("total", global.getTotal());
        json.put("disponibles", global.getDisponibles());
        json.put("resguardados", global.getResguardados());
        json.put("enMantenimiento", global.getEnMantenimiento());
        json.put("reportados", global.getReportados());

        // % cambio semanal por categoría
        json.put("pctTotal",        calcPct(global.getTotal(), lastWeek.getTotal()));
        json.put("pctResguardados", calcPct(global.getResguardados(), lastWeek.getResguardados()));
        json.put("pctMantenimiento",calcPct(global.getEnMantenimiento(), lastWeek.getEnMantenimiento()));
        json.put("pctReportados",   calcPct(global.getReportados(), lastWeek.getReportados()));

        return json;
    }

    /**
     * Retorna el porcentaje de las proyecciones actual vs anterior, redondeado al entero más cercano.
     * Si el valor anterior es 0, retorna 100% si el actual es >0, o 0% si el actual es 0.
     * @param actual Total de proyecciones para la semana actual
     * @param anterior Total de proyecciones para la semana pasada
     * @return <code>Long</code> con el "porcentaje" XD
     */
    private long calcPct(long actual, long anterior) {
        if (anterior == 0) return actual > 0 ? 100 : 0;
        return Math.round(((actual - anterior) * 100.0) / anterior);
    }

}
