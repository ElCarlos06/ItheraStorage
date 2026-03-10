package mx.edu.utez.modules.reportes;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.assets.Assets;
import mx.edu.utez.modules.assets.AssetsRepository;
import mx.edu.utez.modules.prioridades.Prioridad;
import mx.edu.utez.modules.prioridades.PrioridadRepository;
import mx.edu.utez.modules.tipo_fallas.TipoFalla;
import mx.edu.utez.modules.tipo_fallas.TipoFallaRepository;
import mx.edu.utez.modules.users.User;
import mx.edu.utez.modules.users.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final AssetsRepository assetsRepository;
    private final UserRepository userRepository;
    private final TipoFallaRepository tipoFallaRepository;
    private final PrioridadRepository prioridadRepository;

    @Transactional(readOnly = true)
    public ApiResponse findAll() {
        List<Reporte> list = reporteRepository.findAll();
        return new ApiResponse("OK", list, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ApiResponse findById(Long id) {
        Optional<Reporte> found = reporteRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Reporte no encontrado", true, HttpStatus.NOT_FOUND);
        return new ApiResponse("OK", found.get(), HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ApiResponse findByActivo(Long activoId) {
        List<Reporte> list = reporteRepository.findByActivoId(activoId);
        return new ApiResponse("OK", list, HttpStatus.OK);
    }

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
        return new ApiResponse("Reporte registrado", entity, HttpStatus.CREATED);
    }

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

