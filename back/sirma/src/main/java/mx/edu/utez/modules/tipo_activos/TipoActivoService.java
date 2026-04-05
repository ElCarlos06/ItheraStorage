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

/**
 * Servicio que contiene la lógica de negocio para la gestión de tipos de activo.
 *
 * @author Ithera Team
 */
@Service
@AllArgsConstructor
public class TipoActivoService {

    private final TipoActivoRepository tipoActivoRepository;
    private final AssetsRepository assetsRepository;

    /**
     * Obtiene una página de tipos de activo que se encuentran activos.
     * También calcula y asigna la cantidad de activos asociados a cada tipo.
     *
     * @param pageable Información de paginación y ordenamiento.
     * @return ApiResponse con la página de tipos de activo y la cantidad asociada.
     */
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

    /**
     * Busca un tipo de activo específico por su identificador.
     *
     * @param id Identificador único del tipo de activo.
     * @return ApiResponse con el tipo de activo si se encuentra, o un mensaje de error si no existe.
     */
    @Transactional(readOnly = true)
    public ApiResponse findById(Long id) {
        Optional<TipoActivo> found = tipoActivoRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Tipo de activo no encontrado", true, HttpStatus.NOT_FOUND);
        return new ApiResponse("OK", found.get(), HttpStatus.OK);
    }

    /**
     * Registra un nuevo tipo de activo o reactiva uno existente con el mismo nombre.
     *
     * @param dto Objeto con los datos del tipo de activo a guardar.
     * @return ApiResponse con el resultado de la operación de registro.
     */
    @Transactional
    public ApiResponse save(TipoActivoDTO dto) {

        Optional<TipoActivo> existenteOpt = tipoActivoRepository.findByNombre(dto.getNombre());

        TipoActivo nuevaEntity = null;
        if (existenteOpt.isPresent()) {
            TipoActivo entity = existenteOpt.get();

            // Si ya está activo, lanzamos el error de conflicto
            if (entity.getEsActivo())
                return new ApiResponse("Ya existe un tipo de activo, activo con ese nombre", true, HttpStatus.CONFLICT);


            // Si existía pero estaba desactivado se actualiza y persiste
            mapEntity(dto, entity);
            entity.setEsActivo(true);
            nuevaEntity = tipoActivoRepository.save(entity);
        } else {
            // Si no existía nada, creamos uno nuevo
            nuevaEntity = new TipoActivo();
            nuevaEntity.setNombre(dto.getNombre());
            mapEntity(dto, nuevaEntity);
            tipoActivoRepository.save(nuevaEntity);
        }
        return new ApiResponse("Tipo de activo registrado", nuevaEntity, HttpStatus.CREATED);
    }

    /**
     * Actualiza la información de un tipo de activo existente.
     *
     * @param id Identificador único del tipo de activo a actualizar.
     * @param dto Objeto con los nuevos datos a actualizar en el tipo de activo.
     * @return ApiResponse con el resultado de la actualización.
     */
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

    /**
     * Alterna el estado (activo/inactivo) de un tipo de activo.
     *
     * @param id Identificador único del tipo de activo cuyo estado se cambiará.
     * @return ApiResponse indicando el éxito del cambio de estado.
     */
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

    /**
     * Mapea los datos de un DTO a una entidad TipoActivo.
     *
     * @param dto Objeto de transferencia de datos con la información de origen.
     * @param entity Entidad JPA que será actualizada con los datos del DTO.
     */
    private void mapEntity(TipoActivoDTO dto, TipoActivo entity) {
        entity.setTipoBien(dto.getTipoBien());
        entity.setDescripcion(dto.getDescripcion());
        entity.setMarca(dto.getMarca());
        entity.setModelo(dto.getModelo());
        entity.setEsActivo(true);
    }

}
