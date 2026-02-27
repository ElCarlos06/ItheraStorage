package mx.edu.utez.services;

import lombok.RequiredArgsConstructor;
import mx.edu.utez.dtos.CreatePrioridadDTO;
import mx.edu.utez.model.Prioridad;
import mx.edu.utez.repository.PrioridadRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio para gestionar las operaciones relacionadas con las prioridades.
 * Proporciona métodos para obtener, crear y mapear prioridades utilizando el repositorio de prioridades.
 * @author Ithera Team
 */
@Service
@RequiredArgsConstructor
public class PrioridadService {

    private final PrioridadRepository prioridadRepository;

    /**
     * Metodo para obtener todas las prioridades, devuelve un mapa con el resultado
     * @return Lista de prioridades obtenidas del repositorio, junto con un indicador de éxito
     */
    public Map<String, Object> getAll() {
        Map<String, Object> response = new HashMap<>();

        response.put("success", 200);
        response.put("data", prioridadRepository.findAll());

        return response;
    }

    /**
     * Metodo para obtener una prioridad por su ID, devuelve un mapa con el resultado
     * @param id ID de la prioridad a buscar
     * @return Mapa con la prioridad encontrada o un mensaje de error si no se encuentra, junto con un indicador de éxito
     */
    public Map<String, Object> getById(Integer id) {
        Map<String, Object> response = new HashMap<>();

        Optional<Prioridad> prioridad = prioridadRepository.findById(id);

        if (prioridad.isEmpty()) {
            response.put("success", 404);
            response.put("message", "Prioridad no encontrada");
        } else {
            Prioridad prioridadActual = prioridad.get();

            response.put("success", 200);
            response.put("prioridad", prioridadActual);
        }

        return response;
    }

    /**
     * Metodo para crear una nueva prioridad a partir de un DTO, devuelve un mapa con el resultado
     * @param createPrioridadDTO DTO con los datos para crear una nueva prioridad
     * @return Mapa con la prioridad creada o un mensaje de error si no se pudo crear, junto con un indicador de éxito.
     */
    public Map<String, Object> create(CreatePrioridadDTO createPrioridadDTO) {
        Map<String, Object> response = new HashMap<>();

        Prioridad nuevaPrioridad = mapPrioridad(createPrioridadDTO);
        Prioridad prioridadGuardada = prioridadRepository.save(nuevaPrioridad);

        response.put("success", 201);
        response.put("prioridad", prioridadGuardada);

        return response;
    }

    /**
     * Metodo para actualizar una prioridad existente a partir de un DTO, devuelve un mapa con el resultado
     * @param id ID de la prioridad a actualizar
     * @param createPrioridadDTO DTO con los datos actualizados de la prioridad
     * @return Mapa con la prioridad actualizada o un mensaje de error si no se pudo actualizar, junto con un indicador de éxito.
     */
    public Map<String, Object> update(Integer id, CreatePrioridadDTO createPrioridadDTO) {
        Map<String, Object> response = new HashMap<>();

        Optional<Prioridad> prioridad = prioridadRepository.findById(id);

        if (prioridad.isEmpty()) {
            response.put("success", 404);
            response.put("message", "Prioridad no encontrada");
        } else {
            Prioridad prioridadActual = prioridad.get();

            Prioridad prioridadActualizada = mapPrioridad(createPrioridadDTO);
            prioridadActualizada.setIdPrioridad(prioridadActual.getIdPrioridad());

            Prioridad prioridadGuardada = prioridadRepository.save(prioridadActualizada);

            response.put("success", 200);
            response.put("prioridad", prioridadGuardada);
        }

        return response;
    }

    /**
     * Metodo para eliminar una prioridad por su ID, devuelve un mapa con el resultado
     * @param id ID de la prioridad a eliminar
     * @return Mapa con un mensaje de éxito o error dependiendo del resultado de la eliminación, junto con un indicador de éxito.
     */
    public Map<String, Object> delete(Integer id) {
        Map<String, Object> response = new HashMap<>();

        Optional<Prioridad> prioridad = prioridadRepository.findById(id);

        if (prioridad.isEmpty()) {
            response.put("success", 404);
            response.put("message", "Prioridad no encontrada");
        } else {
            prioridadRepository.deleteById(id);

            response.put("success", 204);
            response.put("message", "Prioridad eliminada correctamente");
        }

        return response;
    }

    /**
     * Mapea un CreatePrioridadDTO a una entidad Prioridad, asignando solo los campos que no son nulos.
     * @param dto DTO con los datos para crear una nueva prioridad
     * @return Entidad Prioridad con los datos mapeados desde el DTO
     */
    private Prioridad mapPrioridad(CreatePrioridadDTO dto) {
        Prioridad prioridad = new Prioridad();

        if (dto.getNivel() != null)
            prioridad.setNivel(dto.getNivel());

        if (dto.getDescripcion() != null)
            prioridad.setDescripcion(dto.getDescripcion());

        return prioridad;
    }
}
