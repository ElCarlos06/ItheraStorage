package mx.edu.utez.modules.areas;

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
 * Servicio de negocio para gestión de áreas en SIRMA.
 * Maneja operaciones CRUD con validaciones de unicidad.
 *
 * @author Ithera Team
 */
@Service
@AllArgsConstructor
public class AreaService {

    private final AreaRepository areaRepository;

    @Transactional(readOnly = true)
    public ApiResponse findAll(Pageable pageable) {
        Page<Area> page = areaRepository.findAll(pageable);
        return new ApiResponse("OK", page, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ApiResponse findById(Long id) {
        Optional<Area> found = areaRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Área no encontrada", true, HttpStatus.NOT_FOUND);
        return new ApiResponse("OK", found.get(), HttpStatus.OK);
    }

    @Transactional
    public ApiResponse save(AreaDTO dto) {
        if (areaRepository.existsByNombre(dto.getNombre()))
            return new ApiResponse("Ya existe un área con ese nombre", true, HttpStatus.CONFLICT);
        Area entity = new Area();
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        areaRepository.save(entity);
        return new ApiResponse("Área registrada", entity, HttpStatus.CREATED);
    }

    @Transactional
    public ApiResponse update(Long id, AreaDTO dto) {
        Optional<Area> found = areaRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Área no encontrada", true, HttpStatus.NOT_FOUND);
        Area entity = found.get();
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        areaRepository.save(entity);
        return new ApiResponse("Área actualizada", entity, HttpStatus.OK);
    }

    @Transactional
    public ApiResponse deleteById(Long id) {
        Optional<Area> found = areaRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Área no encontrada", true, HttpStatus.NOT_FOUND);
        areaRepository.deleteById(id);
        return new ApiResponse("Área eliminada", HttpStatus.OK);
    }

}
