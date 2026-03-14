package mx.edu.utez.modules.tipo_fallas;

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
 * Servicio de negocio para gestión de tipos de falla en SIRMA.
 * Maneja operaciones CRUD con validaciones de unicidad.
 *
 * @author Ithera Team
 */
@Service
@AllArgsConstructor
public class TipoFallaService {

    private final TipoFallaRepository tipoFallaRepository;

    @Transactional(readOnly = true)
    public ApiResponse findAll(Pageable pageable) {
        Page<TipoFalla> page = tipoFallaRepository.findAll(pageable);
        return new ApiResponse("OK", page, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ApiResponse findById(Long id) {
        Optional<TipoFalla> found = tipoFallaRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Tipo de falla no encontrado", true, HttpStatus.NOT_FOUND);
        return new ApiResponse("OK", found.get(), HttpStatus.OK);
    }

    @Transactional
    public ApiResponse save(TipoFallaDTO dto) {
        if (tipoFallaRepository.existsByNombre(dto.getNombre()))
            return new ApiResponse("Ya existe un tipo de falla con ese nombre", true, HttpStatus.CONFLICT);
        TipoFalla entity = new TipoFalla();
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        tipoFallaRepository.save(entity);
        return new ApiResponse("Tipo de falla registrado", entity, HttpStatus.CREATED);
    }

    @Transactional
    public ApiResponse update(Long id, TipoFallaDTO dto) {
        Optional<TipoFalla> found = tipoFallaRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Tipo de falla no encontrado", true, HttpStatus.NOT_FOUND);
        TipoFalla entity = found.get();
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        tipoFallaRepository.save(entity);
        return new ApiResponse("Tipo de falla actualizado", entity, HttpStatus.OK);
    }

    @Transactional
    public ApiResponse deleteById(Long id) {
        Optional<TipoFalla> found = tipoFallaRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Tipo de falla no encontrado", true, HttpStatus.NOT_FOUND);
        tipoFallaRepository.deleteById(id);
        return new ApiResponse("Tipo de falla eliminado", HttpStatus.OK);
    }

}
