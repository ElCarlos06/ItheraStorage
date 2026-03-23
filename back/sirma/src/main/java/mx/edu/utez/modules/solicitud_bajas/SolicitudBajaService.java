package mx.edu.utez.modules.solicitud_bajas;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.assets.Assets;
import mx.edu.utez.modules.assets.AssetsRepository;
import mx.edu.utez.modules.bitacora.BitacoraService;
import mx.edu.utez.modules.mantenimientos.Mantenimiento;
import mx.edu.utez.modules.mantenimientos.MantenimientoRepository;
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
public class SolicitudBajaService {

    private final SolicitudBajaRepository solicitudBajaRepository;
    private final AssetsRepository assetsRepository;
    private final MantenimientoRepository mantenimientoRepository;
    private final UserRepository userRepository;
    private final BitacoraService bitacoraService;

    @Transactional(readOnly = true)
    public ApiResponse findAll(Pageable pageable) {
        Page<SolicitudBaja> page = solicitudBajaRepository.findAll(pageable);
        return new ApiResponse("OK", page, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ApiResponse findById(Long id) {
        Optional<SolicitudBaja> found = solicitudBajaRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Solicitud de baja no encontrada", true, HttpStatus.NOT_FOUND);
        return new ApiResponse("OK", found.get(), HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ApiResponse findByEstado(String estado) {
        List<SolicitudBaja> list = solicitudBajaRepository.findByEstado(estado);
        return new ApiResponse("OK", list, HttpStatus.OK);
    }

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
