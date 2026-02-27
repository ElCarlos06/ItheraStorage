package mx.edu.utez.services;

import lombok.RequiredArgsConstructor;
import mx.edu.utez.dtos.CreateTipoActivoDTO;
import mx.edu.utez.model.TipoActivo;
import mx.edu.utez.repository.TipoActivoRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio para gestionar las operaciones relacionadas con los tipos de activo.
 * Proporciona métodos para obtener, crear y mapear tipos de activo utilizando el repositorio de tipos de activo.
 * @author Ithera Team
 */
@Service
@RequiredArgsConstructor
public class TipoActivoService {

    private final TipoActivoRepository tipoActivoRepository;

    /**
     * Metodo para obtener todos los tipos de activo, devuelve un mapa con el resultado
     * @return Lista de tipos de activo obtenidos del repositorio, junto con un indicador de éxito
     */
    public Map<String, Object> getAll() {
        Map<String, Object> response = new HashMap<>();

        response.put("success", 200);
        response.put("data", tipoActivoRepository.findAll());

        return response;
    }

    /**
     * Metodo para obtener un tipo de activo por su ID, devuelve un mapa con el resultado
     * @param id ID del tipo de activo a buscar
     * @return Mapa con el tipo de activo encontrado o un mensaje de error si no se encuentra, junto con un indicador de éxito
     */
    public Map<String, Object> getById(Integer id) {
        Map<String, Object> response = new HashMap<>();

        Optional<TipoActivo> tipoActivo = tipoActivoRepository.findById(id);

        if (tipoActivo.isEmpty()) {
            response.put("success", 404);
            response.put("message", "Tipo de activo no encontrado");
        } else {
            TipoActivo tipoActivoActual = tipoActivo.get();

            response.put("success", 200);
            response.put("tipoActivo", tipoActivoActual);
        }

        return response;
    }

    /**
     * Metodo para crear un nuevo tipo de activo a partir de un DTO, devuelve un mapa con el resultado
     * @param createTipoActivoDTO DTO con los datos para crear un nuevo tipo de activo
     * @return Mapa con el tipo de activo creado o un mensaje de error si no se pudo crear, junto con un indicador de éxito.
     */
    public Map<String, Object> create(CreateTipoActivoDTO createTipoActivoDTO) {
        Map<String, Object> response = new HashMap<>();

        TipoActivo nuevoTipoActivo = mapTipoActivo(createTipoActivoDTO);
        TipoActivo tipoActivoGuardado = tipoActivoRepository.save(nuevoTipoActivo);

        response.put("success", 201);
        response.put("tipoActivo", tipoActivoGuardado);

        return response;
    }

    /**
     * Metodo para actualizar un tipo de activo existente a partir de un DTO, devuelve un mapa con el resultado
     * @param id ID del tipo de activo a actualizar
     * @param createTipoActivoDTO DTO con los datos actualizados del tipo de activo
     * @return Mapa con el tipo de activo actualizado o un mensaje de error si no se pudo actualizar, junto con un indicador de éxito.
     */
    public Map<String, Object> update(Integer id, CreateTipoActivoDTO createTipoActivoDTO) {
        Map<String, Object> response = new HashMap<>();

        Optional<TipoActivo> tipoActivo = tipoActivoRepository.findById(id);

        if (tipoActivo.isEmpty()) {
            response.put("success", 404);
            response.put("message", "Tipo de activo no encontrado");
        } else {
            TipoActivo tipoActivoActual = tipoActivo.get();

            TipoActivo tipoActivoActualizado = mapTipoActivo(createTipoActivoDTO);
            tipoActivoActualizado.setIdTipoActivo(tipoActivoActual.getIdTipoActivo());

            TipoActivo tipoActivoGuardado = tipoActivoRepository.save(tipoActivoActualizado);

            response.put("success", 200);
            response.put("tipoActivo", tipoActivoGuardado);
        }

        return response;
    }

    /**
     * Metodo para eliminar un tipo de activo por su ID, devuelve un mapa con el resultado
     * @param id ID del tipo de activo a eliminar
     * @return Mapa con un mensaje de éxito o error dependiendo del resultado de la eliminación, junto con un indicador de éxito.
     */
    public Map<String, Object> delete(Integer id) {
        Map<String, Object> response = new HashMap<>();

        Optional<TipoActivo> tipoActivo = tipoActivoRepository.findById(id);

        if (tipoActivo.isEmpty()) {
            response.put("success", 404);
            response.put("message", "Tipo de activo no encontrado");
        } else {
            tipoActivoRepository.deleteById(id);

            response.put("success", 204);
            response.put("message", "Tipo de activo eliminado correctamente");
        }

        return response;
    }

    /**
     * Mapea un CreateTipoActivoDTO a una entidad TipoActivo, asignando solo los campos que no son nulos.
     * @param dto DTO con los datos para crear un nuevo tipo de activo
     * @return Entidad TipoActivo con los datos mapeados desde el DTO
     */
    private TipoActivo mapTipoActivo(CreateTipoActivoDTO dto) {
        TipoActivo tipoActivo = new TipoActivo();

        if (dto.getNombre() != null)
            tipoActivo.setNombre(dto.getNombre());

        if (dto.getMarca() != null)
            tipoActivo.setMarca(dto.getMarca());

        if (dto.getModelo() != null)
            tipoActivo.setModelo(dto.getModelo());

        return tipoActivo;
    }
}
