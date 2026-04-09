package mx.edu.utez.modules.core.solicitud_bajas;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.core.assets.Assets;
import mx.edu.utez.modules.core.assets.AssetsRepository;
import mx.edu.utez.modules.reporting.bitacora.BitacoraService;
import mx.edu.utez.modules.maintenance.mantenimientos.Mantenimiento;
import mx.edu.utez.modules.maintenance.mantenimientos.MantenimientoRepository;
import mx.edu.utez.modules.security.users.User;
import mx.edu.utez.modules.security.users.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio que contiene la lógica de negocio para la gestión de solicitudes de baja de activos.
 *
 * @author Ithera Team
 */
@Service
@AllArgsConstructor
public class SolicitudBajaService {

    private final SolicitudBajaRepository solicitudBajaRepository;
    private final AssetsRepository assetsRepository;
    private final MantenimientoRepository mantenimientoRepository;
    private final UserRepository userRepository;
    private final BitacoraService bitacoraService;

    /**
     * Recupera una lista paginada general de todas las solicitudes de baja.
     *
     * @param pageable Configuración de paginación.
     * @return ApiResponse con la información empaquetada de la base de datos.
     */
    @Transactional(readOnly = true)
    public ApiResponse findAll(Pageable pageable) {
        Page<SolicitudBaja> page = solicitudBajaRepository.findAll(pageable);
        return new ApiResponse("OK", page, HttpStatus.OK);
    }

    /**
     * Busca y retorna la especificación completa de una solicitud de baja.
     *
     * @param id Identificador de la solicitud.
     * @return ApiResponse indicando éxito o no localización.
     */
    @Transactional(readOnly = true)
    public ApiResponse findById(Long id) {
        Optional<SolicitudBaja> found = solicitudBajaRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Solicitud de baja no encontrada", true, HttpStatus.NOT_FOUND);
        return new ApiResponse("OK", found.get(), HttpStatus.OK);
    }

    /**
     * Recupera solicitudes filtrándolas por su etapa actual.
     *
     * @param estado Puede ser "Pendiente", "Aprobada" o "Rechazada".
     * @return Objeto englobando las listas coincidentes.
     */
    @Transactional(readOnly = true)
    public ApiResponse findByEstado(String estado) {
        List<SolicitudBaja> list = solicitudBajaRepository.findByEstado(estado);
        return new ApiResponse("OK", list, HttpStatus.OK);
    }

    /**
     * Empieza y construye una solicitud inicial justificada por razones de mantenimiento irrecuperable.
     * Evita crear solicitudes múltiples concurrentes si el mantenimiento base ya la amparaba.
     *
     * @param dto Detalles necesarios propuestos por un técnico validador de los daños operacionales.
     * @return Respuesta afirmativa confirmando registro incipiente en espera de revisión administrativa.
     */
    @Transactional
    public ApiResponse save(SolicitudBajaDTO dto) {
        Optional<Assets> activo = assetsRepository.findById(dto.getIdActivo());
        if (activo.isEmpty())
            return new ApiResponse("Activo no encontrado", true, HttpStatus.NOT_FOUND);
        Optional<Mantenimiento> mant = mantenimientoRepository.findById(dto.getIdMantenimiento());
        if (mant.isEmpty())
            return new ApiResponse("Mantenimiento no encontrado", true, HttpStatus.NOT_FOUND);
        if (solicitudBajaRepository.findByMantenimientoId(dto.getIdMantenimiento()).isPresent())
            return new ApiResponse("Ya existe una solicitud de baja para ese mantenimiento", true, HttpStatus.CONFLICT);

        SolicitudBaja entity = new SolicitudBaja();
        entity.setActivo(activo.get());
        entity.setMantenimiento(mant.get());
        entity.setJustificacion(dto.getJustificacion());
        entity.setEstado("Pendiente");
        solicitudBajaRepository.save(entity);
        Long activoId = activo.get().getId();
        bitacoraService.registrarEvento(activoId, null, "Solicitud Baja",
                "Solicitud de baja por mantenimiento irreparable",
                null, null, null, null);
        return new ApiResponse("Solicitud de baja registrada", entity, HttpStatus.CREATED);
    }

    /**
     * Revaloriza o sentencia una solicitud (aprobación/negación) marcando las observaciones de quien adminstre.
     *
     * @param id Id en base a cual basarse.
     * @param dto Resoluciones finales de aceptación emitidas al campo "estado".
     * @return Informe con los datos guardados finalizándolo.
     */
    @Transactional
    public ApiResponse update(Long id, SolicitudBajaDTO dto) {
        Optional<SolicitudBaja> found = solicitudBajaRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Solicitud de baja no encontrada", true, HttpStatus.NOT_FOUND);
        SolicitudBaja entity = found.get();
        if (dto.getEstado() != null) {
            entity.setEstado(dto.getEstado());
            if ("Aprobada".equals(dto.getEstado()) || "Rechazada".equals(dto.getEstado()))
                entity.setFechaResolucion(LocalDateTime.now());
        }
        if (dto.getObservacionesAdmin() != null) entity.setObservacionesAdmin(dto.getObservacionesAdmin());
        if (dto.getIdUsuarioAdmin() != null) {
            Optional<User> admin = userRepository.findById(dto.getIdUsuarioAdmin());
            admin.ifPresent(entity::setUsuarioAdmin);
        }
        solicitudBajaRepository.save(entity);
        return new ApiResponse("Solicitud de baja actualizada", entity, HttpStatus.OK);
    }

}
