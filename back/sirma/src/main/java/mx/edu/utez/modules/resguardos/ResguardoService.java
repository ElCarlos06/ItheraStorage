package mx.edu.utez.modules.resguardos;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.assets.Assets;
import mx.edu.utez.modules.assets.AssetsRepository;
import mx.edu.utez.modules.assets.AssetsService;
import mx.edu.utez.modules.bitacora.BitacoraService;
import mx.edu.utez.modules.users.User;
import mx.edu.utez.modules.users.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ResguardoService {

    private final ResguardoRepository resguardoRepository;
    private final AssetsRepository assetsRepository;
    private final AssetsService assetsService;
    private final BitacoraService bitacoraService;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public ApiResponse findAll(Pageable pageable) {
        Page<Resguardo> page = resguardoRepository.findAll(pageable);
        return new ApiResponse("OK", page, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ApiResponse findById(Long id) {
        Optional<Resguardo> found = resguardoRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Resguardo no encontrado", true, HttpStatus.NOT_FOUND);
        return new ApiResponse("OK", found.get(), HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ApiResponse findByActivo(Long activoId) {
        List<Resguardo> list = resguardoRepository.findByActivoId(activoId);
        return new ApiResponse("OK", list, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ApiResponse findByEmpleado(Long userId) {
        List<Resguardo> list = resguardoRepository.findByUsuarioEmpleadoId(userId);
        return new ApiResponse("OK", list, HttpStatus.OK);
    }

    @Transactional
    public ApiResponse save(ResguardoDTO dto) {
        Optional<Assets> activo = assetsRepository.findById(dto.getIdActivo());

        if (activo.isEmpty())
            return new ApiResponse("Activo no encontrado", true, HttpStatus.NOT_FOUND);

        Optional<Resguardo> resguardoExistente = resguardoRepository
                .findByActivoAndEstadoResguardo(activo.get(), "Pendiente");

        // Evita reasignar el activo a un empleado distinto mientras exista un resguardo pendiente.
        if (resguardoExistente.isPresent()) {
            User empleadoActual = resguardoExistente.get().getUsuarioEmpleado();
            if (!empleadoActual.getId().equals(dto.getIdUsuarioEmpleado()))
                return new ApiResponse(
                        "El activo ya está asignado a otro empleado", true, HttpStatus.CONFLICT
                );
        }

        Optional<User> empleado = userRepository.findById(dto.getIdUsuarioEmpleado());
        if (empleado.isEmpty())
            return new ApiResponse("Empleado no encontrado", true, HttpStatus.NOT_FOUND);
        Optional<User> admin = userRepository.findById(dto.getIdUsuarioAdmin());
        if (admin.isEmpty())
            return new ApiResponse("Administrador no encontrado", true, HttpStatus.NOT_FOUND);

        Resguardo entity = new Resguardo();
        entity.setActivo(activo.get());
        entity.setUsuarioEmpleado(empleado.get());
        entity.setUsuarioAdmin(admin.get());
        entity.setFechaAsignacion(LocalDateTime.now());
        entity.setObservacionesAsig(dto.getObservacionesAsig());
        entity.setEstadoResguardo("Pendiente");
        resguardoRepository.save(entity);

        // Flujo de estatus: al asignar a empleado → En Proceso (valor exacto del ENUM)
        Long activoId = activo.get().getId();
        String custAnt = activo.get().getEstadoCustodia();
        assetsRepository.updateEstadoCustodia(activoId, "En Proceso");
        assetsService.evictAssetCache(activoId);
        bitacoraService.registrarEvento(activoId, dto.getIdUsuarioAdmin(), "Asignacion Resguardo",
                "Asignado a " + empleado.get().getNombreCompleto(), custAnt, "En Proceso", null, null);

        return new ApiResponse("Resguardo registrado", entity, HttpStatus.CREATED);
    }

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
                assetsRepository.updateEstadoCustodia(activoId, "Resguardado");
                assetsService.evictAssetCache(activoId);
                bitacoraService.registrarEvento(activoId, null, "Confirmacion Resguardo",
                        "Resguardo confirmado por " + entity.getUsuarioEmpleado().getNombreCompleto(),
                        custAnt, "Resguardado", null, null);
            }
            if ("Devuelto".equals(dto.getEstadoResguardo())) {
                entity.setFechaDevolucion(LocalDateTime.now());
                Long activoId = entity.getActivo().getId();
                String custAnt = entity.getActivo().getEstadoCustodia();
                assetsRepository.updateEstadoCustodia(activoId, "Disponible");
                assetsService.evictAssetCache(activoId);
                bitacoraService.registrarEvento(activoId, null, "Devolucion Resguardo",
                        "Activo devuelto por " + entity.getUsuarioEmpleado().getNombreCompleto(),
                        custAnt, "Disponible", null, null);
            }
        }
        resguardoRepository.save(entity);
        return new ApiResponse("Resguardo actualizado", entity, HttpStatus.OK);
    }

}
