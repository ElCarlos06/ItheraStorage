package mx.edu.utez.modules.prioridades;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de negocio para gestión de prioridades en SIRMA.
 * Maneja operaciones CRUD con validaciones de unicidad.
 *
 * @author Ithera Team
 */
@Service
@AllArgsConstructor
public class PrioridadService {

    private final PrioridadRepository prioridadRepository;

    @Transactional(readOnly = true)
    public ApiResponse findAll(Pageable pageable) {
        Page<Prioridad> page = prioridadRepository.findAll(pageable);
        return new ApiResponse("OK", page, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ApiResponse findById(Long id) {
        Optional<Prioridad> found = prioridadRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Prioridad no encontrada", true, HttpStatus.NOT_FOUND);
        return new ApiResponse("OK", found.get(), HttpStatus.OK);
    }

    @Transactional
    public ApiResponse save(PrioridadDTO dto) {
        if (prioridadRepository.existsByNivel(dto.getNivel()))
            return new ApiResponse("Ya existe una prioridad con ese nivel", true, HttpStatus.CONFLICT);
        Prioridad entity = new Prioridad();
        entity.setNivel(dto.getNivel());
        entity.setTiempoRespuestaHoras(dto.getTiempoRespuestaHoras());
        prioridadRepository.save(entity);
        return new ApiResponse("Prioridad registrada", entity, HttpStatus.CREATED);
    }

    @Transactional
    public ApiResponse update(Long id, PrioridadDTO dto) {
        Optional<Prioridad> found = prioridadRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Prioridad no encontrada", true, HttpStatus.NOT_FOUND);
        Prioridad entity = found.get();
        entity.setNivel(dto.getNivel());
        entity.setTiempoRespuestaHoras(dto.getTiempoRespuestaHoras());
        prioridadRepository.save(entity);
        return new ApiResponse("Prioridad actualizada", entity, HttpStatus.OK);
    }

    @Transactional
    public ApiResponse deleteById(Long id) {
        Optional<Prioridad> found = prioridadRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Prioridad no encontrada", true, HttpStatus.NOT_FOUND);
        prioridadRepository.deleteById(id);
        return new ApiResponse("Prioridad eliminada", HttpStatus.OK);
    }

}
