package mx.edu.utez.modules.tipo_activos;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class TipoActivoService {

    private final TipoActivoRepository tipoActivoRepository;

    @Transactional(readOnly = true)
    public ApiResponse findAll(Pageable pageable) {
        Page<TipoActivo> page = tipoActivoRepository.findAll(pageable);
        return new ApiResponse("OK", page, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ApiResponse findById(Long id) {
        Optional<TipoActivo> found = tipoActivoRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Tipo de activo no encontrado", true, HttpStatus.NOT_FOUND);
        return new ApiResponse("OK", found.get(), HttpStatus.OK);
    }

    @Transactional
    public ApiResponse save(TipoActivoDTO dto) {
        if (tipoActivoRepository.existsByNombre(dto.getNombre()))
            return new ApiResponse("Ya existe un tipo de activo con ese nombre", true, HttpStatus.CONFLICT);
        TipoActivo entity = new TipoActivo();
        entity.setNombre(dto.getNombre());
        entity.setTipoBien(dto.getTipoBien());
        entity.setDescripcion(dto.getDescripcion());
        entity.setEsActivo(dto.getEsActivo() != null ? dto.getEsActivo() : true);
        tipoActivoRepository.save(entity);
        return new ApiResponse("Tipo de activo registrado", entity, HttpStatus.CREATED);
    }

    @Transactional
    public ApiResponse update(Long id, TipoActivoDTO dto) {
        Optional<TipoActivo> found = tipoActivoRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Tipo de activo no encontrado", true, HttpStatus.NOT_FOUND);
        TipoActivo entity = found.get();
        entity.setNombre(dto.getNombre());
        entity.setTipoBien(dto.getTipoBien());
        entity.setDescripcion(dto.getDescripcion());
        if (dto.getEsActivo() != null) entity.setEsActivo(dto.getEsActivo());
        tipoActivoRepository.save(entity);
        return new ApiResponse("Tipo de activo actualizado", entity, HttpStatus.OK);
    }

    @Transactional
    public ApiResponse toggleStatus(Long id) {
        Optional<TipoActivo> found = tipoActivoRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Tipo de activo no encontrado", true, HttpStatus.NOT_FOUND);
        TipoActivo entity = found.get();
        entity.setEsActivo(!entity.getEsActivo());
        tipoActivoRepository.save(entity);
        return new ApiResponse("Estado actualizado", entity, HttpStatus.OK);
    }

}
