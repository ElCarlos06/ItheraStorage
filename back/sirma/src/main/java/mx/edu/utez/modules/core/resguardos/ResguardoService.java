package mx.edu.utez.modules.core.resguardos;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.core.assets.utils.AssetEstados;
import mx.edu.utez.modules.core.assets.Assets;
import mx.edu.utez.modules.core.assets.AssetsRepository;
import mx.edu.utez.modules.core.assets.AssetsService;
import mx.edu.utez.modules.reporting.bitacora.BitacoraService;
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
 * Servicio que contiene la lógica de negocio para la gestión de resguardos de activos.
 *
 * @author Ithera Team
 */
@Slf4j
@Service
@AllArgsConstructor
public class ResguardoService {

    private final ResguardoRepository resguardoRepository;
    private final AssetsRepository assetsRepository;
    private final AssetsService assetsService;
    private final BitacoraService bitacoraService;
    private final UserRepository userRepository;

    /**
     * Obtiene una página listando todos los resguardos registrados en el sistema.
     *
     * @param pageable Información de paginación y ordenamiento.
     * @return ApiResponse con la página de resultados.
     */
    @Transactional(readOnly = true)
    public ApiResponse findAll(Pageable pageable) {
        Page<Resguardo> page = resguardoRepository.findAll(pageable);
        return new ApiResponse("OK", page, HttpStatus.OK);
    }

    /**
     * Busca un resguardo específico por su identificador.
     *
     * @param id Identificador único del resguardo.
     * @return ApiResponse con el resguardo encontrado o un mensaje de error si no existe.
     */
    @Transactional(readOnly = true)
    public ApiResponse findById(Long id) {
        Optional<Resguardo> found = resguardoRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Resguardo no encontrado", true, HttpStatus.NOT_FOUND);
        return new ApiResponse("OK", found.get(), HttpStatus.OK);
    }

    /**
     * Recupera todos los resguardos asociados a un activo específico.
     *
     * @param activoId Identificador del activo físico.
     * @return ApiResponse con la lista de resguardos históricos o actuales de ese activo.
     */
    @Transactional(readOnly = true)
    public ApiResponse findByActivo(Long activoId) {
        List<Resguardo> list = resguardoRepository.findByActivoId(activoId);
        return new ApiResponse("OK", list, HttpStatus.OK);
    }

    /**
     * Obtiene los resguardos asignados a un empleado específico.
     *
     * @param userId Identificador del usuario empleado.
     * @return ApiResponse con la lista de resguardos correspondientes al empleado.
     */
    @Transactional(readOnly = true)
    public ApiResponse findByEmpleado(Long userId) {
        List<Resguardo> list = resguardoRepository.findByUsuarioEmpleadoId(userId);
        return new ApiResponse("OK", list, HttpStatus.OK);
    }

    /**
     * Guarda y asigna un nuevo resguardo pendiente a un empleado.
     * Valida que el activo exista y que no esté ya asignado conflictivamente.
     *
     * @param dto Datos del resguardo a generar.
     * @return ApiResponse confirmando la creación e iniciando el estado de custodia.
     */
    @Transactional
    public ApiResponse save(ResguardoDTO dto) {
        Optional<Assets> activo = assetsRepository.findById(dto.getIdActivo());

        if (activo.isEmpty())
            return new ApiResponse("Activo no encontrado", true, HttpStatus.NOT_FOUND);

        log.info("saving resguardo {}", dto);

        Optional<Resguardo> resguardoExistente = resguardoRepository
                .findByActivoAndEstadoResguardo(activo.get(), "Pendiente");

        log.info("resguardo existente {}", resguardoExistente);
        // Evita reasignar el activo a un empleado distinto mientras exista un resguardo pendiente.
        if (resguardoExistente.isPresent()) {
            User empleadoActual = resguardoExistente.get().getUsuarioEmpleado();
            if (!empleadoActual.getId().equals(dto.getIdUsuarioEmpleado()))
                return new ApiResponse(
                        "El activo ya está asignado a otro empleado", true, HttpStatus.CONFLICT
                );

            log.info("Resguardo asignado a otro w {}", dto);
        }

        log.info("Buscando empleado {}", dto.getIdUsuarioEmpleado());
        Optional<User> empleado = userRepository.findById(dto.getIdUsuarioEmpleado());
        if (empleado.isEmpty())
            return new ApiResponse("Empleado no encontrado", true, HttpStatus.NOT_FOUND);

        log.info("Buscando admin {}", dto.getIdUsuarioAdmin());
        Optional<User> admin = userRepository.findById(dto.getIdUsuarioAdmin());
        if (admin.isEmpty())
            return new ApiResponse("Administrador no encontrado", true, HttpStatus.NOT_FOUND);

        log.info("Creando entidad");
        Resguardo entity = new Resguardo();
        entity.setActivo(activo.get());
        entity.setUsuarioEmpleado(empleado.get());
        entity.setUsuarioAdmin(admin.get());
        entity.setFechaAsignacion(LocalDateTime.now());
        entity.setObservacionesAsig(dto.getObservacionesAsig());
        entity.setEstadoResguardo("Pendiente");

        log.info("Resguardo guardao {}", entity);
        resguardoRepository.save(entity);

        // Flujo de estatus: al asignar a empleado -> En Proceso (valor exacto del ENUM)
        Long activoId = activo.get().getId();
        String custAnt = activo.get().getEstadoCustodia();
        log.info("Registrando evento en bitácora para activo {}: {} -> En Proceso", activoId, custAnt);
        bitacoraService.registrarEvento(activoId, dto.getIdUsuarioAdmin(), "Asignacion Resguardo",
                "Asignado a " + empleado.get().getNombreCompleto(),
                custAnt, AssetEstados.CUSTODIA_EN_PROCESO, null, null);
        assetsRepository.updateEstadoCustodia(activoId, AssetEstados.CUSTODIA_EN_PROCESO);
        assetsService.evictAssetCache(activoId);

        return new ApiResponse("Resguardo registrado", entity, HttpStatus.CREATED);
    }

    /**
     * Actualiza el estado de un resguardo y registra sus observaciones correspondientes,
     * afectando el estado de custodia del activo central en caso de confirmación o devolución.
     *
     * @param id Identificador del resguardo a modificar.
     * @param dto Nuevos datos y observaciones a aplicar.
     * @return ApiResponse con el resultado actualizado.
     */
    @Transactional
    public ApiResponse update(Long id, ResguardoDTO dto) {
        Optional<Resguardo> found = resguardoRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Resguardo no encontrado", true, HttpStatus.NOT_FOUND);
        Resguardo entity = found.get();
        if (dto.getObservacionesAsig() != null) entity.setObservacionesAsig(dto.getObservacionesAsig());
        if (dto.getObservacionesConf() != null) entity.setObservacionesConf(dto.getObservacionesConf());
        if (dto.getObservacionesDev() != null) entity.setObservacionesDev(dto.getObservacionesDev());
        if (dto.getEstadoResguardo() != null) {
            entity.setEstadoResguardo(dto.getEstadoResguardo());
            if ("Confirmado".equals(dto.getEstadoResguardo())) {
                entity.setFechaConfirmacion(LocalDateTime.now());
                Long activoId = entity.getActivo().getId();
                String custAnt = entity.getActivo().getEstadoCustodia();
                assetsRepository.updateEstadoCustodia(activoId, AssetEstados.CUSTODIA_RESGUARDADO);
                assetsService.evictAssetCache(activoId);
                String descConf = "Resguardo confirmado por " + entity.getUsuarioEmpleado().getNombreCompleto();
                String obs = entity.getObservacionesConf();
                if (obs != null && !obs.isBlank()) {
                    descConf += " | " + obs;
                }
                bitacoraService.registrarEvento(activoId, null, "Confirmacion Resguardo",
                        descConf, custAnt, AssetEstados.CUSTODIA_RESGUARDADO, null, null);
            }
            if ("Devuelto".equals(dto.getEstadoResguardo())) {
                entity.setFechaDevolucion(LocalDateTime.now());
                Long activoId = entity.getActivo().getId();
                String custAnt = entity.getActivo().getEstadoCustodia();
                assetsRepository.updateEstadoCustodia(activoId, AssetEstados.CUSTODIA_DISPONIBLE);
                assetsService.evictAssetCache(activoId);
                bitacoraService.registrarEvento(activoId, null, "Devolucion Resguardo",
                        "Activo devuelto por " + entity.getUsuarioEmpleado().getNombreCompleto(),
                        custAnt, AssetEstados.CUSTODIA_DISPONIBLE, null, null);
            }
        }
        resguardoRepository.save(entity);
        return new ApiResponse("Resguardo actualizado", entity, HttpStatus.OK);
    }

}
