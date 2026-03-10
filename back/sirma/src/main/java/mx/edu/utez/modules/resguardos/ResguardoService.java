package mx.edu.utez.modules.resguardos;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.assets.Assets;
import mx.edu.utez.modules.assets.AssetsRepository;
import mx.edu.utez.modules.users.User;
import mx.edu.utez.modules.users.UserRepository;
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
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public ApiResponse findAll() {
        List<Resguardo> list = resguardoRepository.findAll();
        return new ApiResponse("OK", list, HttpStatus.OK);
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
            if ("Confirmado".equals(dto.getEstadoResguardo()))
                entity.setFechaConfirmacion(LocalDateTime.now());
            if ("Devuelto".equals(dto.getEstadoResguardo()))
                entity.setFechaDevolucion(LocalDateTime.now());
        }
        resguardoRepository.save(entity);
        return new ApiResponse("Resguardo actualizado", entity, HttpStatus.OK);
    }

}

