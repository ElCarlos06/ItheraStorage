package mx.edu.utez.modules.core.assets;

import lombok.AllArgsConstructor;

import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.core.assets.projections.AssetsProjection;
import mx.edu.utez.modules.location.espacios.Espacio;
import mx.edu.utez.modules.location.espacios.EspacioRepository;
import mx.edu.utez.modules.media.imagen_activo.ImagenActivo;
import mx.edu.utez.modules.media.imagen_activo.ImagenActivoRepository;
import mx.edu.utez.modules.reporting.bitacora.BitacoraService;
import mx.edu.utez.modules.core.resguardos.ResguardoRepository;
import mx.edu.utez.modules.core.tipo_activos.TipoActivo;
import mx.edu.utez.modules.core.tipo_activos.TipoActivoRepository;
import mx.edu.utez.modules.core.qr.QRService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import static mx.edu.utez.modules.core.assets.utils.AssetsUtils.*;

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
    private static final List<String> ESTADOS_RESGUARDO_ACTIVO = List.of("Pendiente", "Confirmado");

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
            enrichWithResguardo(dto);
            return dto;
        });

        return new ApiResponse("OK", dtoPage, HttpStatus.OK);
    }

    /**
     * Busca un activo por ID y enriquece la respuesta con las URLs de sus imágenes
     * y el resguardo activo asociado.
     *
     * @param id Identificador del activo.
     * @return ApiResponse con el DTO del activo o mensaje de error.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "assets", key = "#id", unless = "#result.error")
    public ApiResponse findById(Long id) {
        Optional<Assets> found = assetsRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Activo no encontrado", true, HttpStatus.NOT_FOUND);

        Assets asset = found.get();
        AssetsDTO dto = toDTO(asset);

        // FIX: ahora findById también enriquece con imágenes Y resguardo
        enrichWithImages(dto);
        enrichWithResguardo(dto);

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
     * Enriquece el DTO del activo con el nombre del empleado resguardante activo
     * y el ID del resguardo correspondiente.
     * Un resguardo activo es aquel con estado "Pendiente" o "Confirmado".
     *
     * @param dto DTO del activo a enriquecer.
     */
    private void enrichWithResguardo(AssetsDTO dto) {
        if (dto.getId() == null) return;

        resguardosRepository
                .findFirstByActivoIdAndEstadoResguardoIn(dto.getId(), ESTADOS_RESGUARDO_ACTIVO)
                .ifPresent(r -> {
                    if (r.getUsuarioEmpleado() != null)
                        dto.setAsignadoA(r.getUsuarioEmpleado().getNombreCompleto());
                    dto.setIdResguardo(r.getId());
                });
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

        TipoActivo tipoActivoActual = tipoActivo.get();
        Espacio espacioActual = espacio.get();

        dto.setTipoActivo(tipoActivoActual);
        dto.setEspacio(espacioActual);

        Assets entity = new Assets();
        entity.setEtiqueta(generateProductTag(dto));
        entity.setNumeroSerie(truncate(dto.getNumeroSerie(), 100));
        entity.setTipoActivo(tipoActivoActual);
        entity.setEspacio(espacioActual);
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
     * @param id  Identificador del activo.
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
     * Se encarga de juntar las estadisticas de los activos de esta semana vs la semana pasada,
     * para luego calcular el porcentaje de cambio semanal.
     *
     * @return ApiResponse con las estadisticas mapeadas
     */
    @Transactional(readOnly = true)
    public ApiResponse getAssetsStats() {
        LocalDate week = LocalDate.now().minusWeeks(1);

        AssetsProjection global = assetsRepository.findAssetsStatsGlobal();
        AssetsProjection lastWeek = assetsRepository.findAssetsStatsOfLastWeek(week);

        Map<String, Long> json = getJson(global, lastWeek);

        return new ApiResponse("Estadísticas de Activos", json, HttpStatus.OK);
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

    /**
     * Actualiza el estado operativo del activo en una transacción propia e independiente
     * (REQUIRES_NEW) para evitar conflictos de lock con la transacción padre.
     * Al confirmar inmediatamente el commit, el lock de fila se libera de forma temprana
     * y se evitan timeouts de tipo "Lock wait timeout exceeded" en TiDB/MySQL.
     *
     * @param id     Identificador del activo.
     * @param estado Nuevo valor de estadoOperativo.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void cambiarEstadoOperativoIndependiente(Long id, String estado) {
        assetsRepository.updateEstadoOperativo(id, estado);
        evictAssetCache(id);
    }

}
