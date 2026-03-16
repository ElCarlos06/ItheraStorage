package mx.edu.utez.modules.tipo_activos;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.assets.AssetsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TipoActivoService {

    private final TipoActivoRepository tipoActivoRepository;
    private final AssetsRepository assetsRepository;

    @Transactional(readOnly = true)
    public ApiResponse findAll(Pageable pageable) {
        Page<TipoActivo> page = tipoActivoRepository.findByEsActivoTrue(pageable);
        List<Object[]> counts = assetsRepository.countActiveAssetsByTipoActivoId();
        Map<Long, Long> cantidadPorTipo = counts.stream()
                .collect(Collectors.toMap(row -> ((Number) row[0]).longValue(), row -> ((Number) row[1]).longValue()));

        List<TipoActivoDTO> dtos = page.getContent().stream().map(t -> {
            TipoActivoDTO dto = new TipoActivoDTO();
            dto.setId(t.getId());
            dto.setNombre(t.getNombre());
            dto.setTipoBien(t.getTipoBien());
            dto.setDescripcion(t.getDescripcion());
            dto.setMarca(t.getMarca());
            dto.setModelo(t.getModelo());
            dto.setEsActivo(t.getEsActivo());
            dto.setCantidad(cantidadPorTipo.getOrDefault(t.getId(), 0L));
            return dto;
        }).toList();

        Page<TipoActivoDTO> result = new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
        return new ApiResponse("OK", result, HttpStatus.OK);
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
        if (tipoActivoRepository.existsByNombreAndEsActivoTrue(dto.getNombre()))
            return new ApiResponse("Ya existe un tipo de activo activo con ese nombre", true, HttpStatus.CONFLICT);
        var desactivado = tipoActivoRepository.findFirstByNombreAndEsActivoFalse(dto.getNombre());
        TipoActivo entity;
        if (desactivado.isPresent()) {
            entity = desactivado.get();
            entity.setEsActivo(true);
            entity.setTipoBien(dto.getTipoBien());
            entity.setDescripcion(dto.getDescripcion());
            entity.setMarca(dto.getMarca());
            entity.setModelo(dto.getModelo());
        } else {
            entity = new TipoActivo();
            entity.setNombre(dto.getNombre());
            entity.setTipoBien(dto.getTipoBien());
            entity.setDescripcion(dto.getDescripcion());
            entity.setMarca(dto.getMarca());
            entity.setModelo(dto.getModelo());
            entity.setEsActivo(dto.getEsActivo() != null ? dto.getEsActivo() : true);
        }
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
        if (dto.getMarca() != null) entity.setMarca(dto.getMarca());
        if (dto.getModelo() != null) entity.setModelo(dto.getModelo());
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
