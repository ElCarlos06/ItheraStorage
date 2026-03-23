package mx.edu.utez.modules.reportes;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.assets.Assets;
import mx.edu.utez.modules.assets.AssetsRepository;
import mx.edu.utez.modules.assets.AssetsService;
import mx.edu.utez.modules.bitacora.BitacoraService;
import mx.edu.utez.modules.prioridades.Prioridad;
import mx.edu.utez.modules.prioridades.PrioridadRepository;
import mx.edu.utez.modules.tipo_fallas.TipoFalla;
import mx.edu.utez.modules.tipo_fallas.TipoFallaRepository;
import mx.edu.utez.modules.users.User;
import mx.edu.utez.modules.users.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de negocio para la gestión de Reportes de Incidencias.
 * Contiene la lógica para la creación y seguimiento de reportes sobre activos dañados o con fallas.
 * @author Ithera Team
 */
@AllArgsConstructor
@Service
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final AssetsRepository assetsRepository;
    private final AssetsService assetsService;
    private final BitacoraService bitacoraService;
    private final UserRepository userRepository;
    private final TipoFallaRepository tipoFallaRepository;
    private final PrioridadRepository prioridadRepository;

    /**
     * Recupera una lista paginada de todos los reportes.
     * @param pageable Configuración de la paginación.
     * @return ApiResponse con la página de reportes.
     */
    @Transactional(readOnly = true)
    public ApiResponse findAll(Pageable pageable) {
        Page<Reporte> page = reporteRepository.findAll(pageable);
        return new ApiResponse("OK", page, HttpStatus.OK);
    }

    /**
     * Busca un reporte específico por su ID.
     * @param id Identificador del reporte.
     * @return ApiResponse con el reporte encontrado o error.
     */
    @Transactional(readOnly = true)
    public ApiResponse findById(Long id) {
        Optional<Reporte> found = reporteRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Reporte no encontrado", true, HttpStatus.NOT_FOUND);
        return new ApiResponse("OK", found.get(), HttpStatus.OK);
    }

    /**
     * Busca todos los reportes asociados a un activo.
     * @param activoId Identificador del activo.
     * @return ApiResponse con la lista de reportes.
     */
    @Transactional(readOnly = true)
    public ApiResponse findByActivo(Long activoId) {
        List<Reporte> list = reporteRepository.findByActivoId(activoId);
        return new ApiResponse("OK", list, HttpStatus.OK);
    }

    /**
     * Registra un nuevo reporte en el sistema.
     * Valida que existan el activo, usuario, tipo de falla y prioridad.
     * @param dto DTO con los datos del reporte.
     * @return ApiResponse con el reporte creado.
     */
    @Transactional
    public ApiResponse save(ReporteDTO dto) {
        Optional<Assets> activo = assetsRepository.findById(dto.getIdActivo());
        if (activo.isEmpty())
            return new ApiResponse("Activo no encontrado", true, HttpStatus.NOT_FOUND);
        Optional<User> usuario = userRepository.findById(dto.getIdUsuarioReporta());
        if (usuario.isEmpty())
            return new ApiResponse("Usuario no encontrado", true, HttpStatus.NOT_FOUND);
        Optional<TipoFalla> tipoFalla = tipoFallaRepository.findById(dto.getIdTipoFalla());
        if (tipoFalla.isEmpty())
            return new ApiResponse("Tipo de falla no encontrado", true, HttpStatus.NOT_FOUND);
        Optional<Prioridad> prioridad = prioridadRepository.findById(dto.getIdPrioridad());
        if (prioridad.isEmpty())
            return new ApiResponse("Prioridad no encontrada", true, HttpStatus.NOT_FOUND);

        Reporte entity = new Reporte();
        entity.setActivo(activo.get());
        entity.setUsuarioReporta(usuario.get());
        entity.setTipoFalla(tipoFalla.get());
        entity.setPrioridad(prioridad.get());
        entity.setDescripcionFalla(dto.getDescripcionFalla());
        entity.setEstadoReporte("Pendiente");
        reporteRepository.save(entity);

        // DFR: Reportado = custodia='Resguardado' + operativo='Reportado'
        Long activoId = activo.get().getId();
        String opAnt = activo.get().getEstadoOperativo();
        assetsRepository.updateEstadoOperativo(activoId, "Reportado");
        assetsService.evictAssetCache(activoId);
        String desc = dto.getDescripcionFalla();
        String descCorta = desc != null && desc.length() > 100 ? desc.substring(0, 100) + "…" : (desc != null ? desc : "Daño reportado");
        bitacoraService.registrarEvento(activoId, dto.getIdUsuarioReporta(), "Reporte Daño",
                "Reporte: " + descCorta, null, null, opAnt, "Reportado");

        return new ApiResponse("Reporte registrado", entity, HttpStatus.CREATED);
    }

    /**
     * Actualiza la información de un reporte existente.
     * Permite modificar descripción, estado, tipo de falla y prioridad.
     * @param id Identificador del reporte a actualizar.
     * @param dto DTO con los nuevos datos.
     * @return ApiResponse con el reporte actualizado.
     */
    @Transactional
    public ApiResponse update(Long id, ReporteDTO dto) {
        Optional<Reporte> found = reporteRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Reporte no encontrado", true, HttpStatus.NOT_FOUND);
        Reporte entity = found.get();
        if (dto.getDescripcionFalla() != null) entity.setDescripcionFalla(dto.getDescripcionFalla());
        if (dto.getEstadoReporte() != null) entity.setEstadoReporte(dto.getEstadoReporte());
        if (dto.getIdTipoFalla() != null) {
            Optional<TipoFalla> tf = tipoFallaRepository.findById(dto.getIdTipoFalla());
            tf.ifPresent(entity::setTipoFalla);
        }
        if (dto.getIdPrioridad() != null) {
            Optional<Prioridad> p = prioridadRepository.findById(dto.getIdPrioridad());
            p.ifPresent(entity::setPrioridad);
        }
        reporteRepository.save(entity);
        return new ApiResponse("Reporte actualizado", entity, HttpStatus.OK);
    }
}
