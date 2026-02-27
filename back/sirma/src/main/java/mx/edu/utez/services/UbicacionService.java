package mx.edu.utez.services;

import lombok.RequiredArgsConstructor;
import mx.edu.utez.dtos.CreateUbicacionDTO;
import mx.edu.utez.model.Ubicacion;
import mx.edu.utez.repository.UbicacionRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio para gestionar las operaciones relacionadas con las ubicaciones.
 * Proporciona métodos para obtener, crear y mapear ubicaciones utilizando el repositorio de ubicaciones.
 * @author Ithera Team
 */
@Service
@RequiredArgsConstructor
public class UbicacionService {

    private final UbicacionRepository ubicacionRepository;

    /**
     * Metodo para obtener todas las ubicaciones, devuelve un mapa con el resultado
     * @return Lista de ubicaciones obtenidas del repositorio, junto con un indicador de éxito
     */
    public Map<String, Object> getAll() {
        Map<String, Object> response = new HashMap<>();

        response.put("success", 200);
        response.put("data", ubicacionRepository.findAll());

        return response;
    }

    /**
     * Metodo para obtener una ubicación por su ID, devuelve un mapa con el resultado
     * @param id ID de la ubicación a buscar
     * @return Mapa con la ubicación encontrada o un mensaje de error si no se encuentra, junto con un indicador de éxito
     */
    public Map<String, Object> getById(Integer id) {
        Map<String, Object> response = new HashMap<>();

        Optional<Ubicacion> ubicacion = ubicacionRepository.findById(id);

        if (ubicacion.isEmpty()) {
            response.put("success", 404);
            response.put("message", "Ubicación no encontrada");
        } else {
            Ubicacion ubicacionActual = ubicacion.get();

            response.put("success", 200);
            response.put("ubicacion", ubicacionActual);
        }

        return response;
    }

    /**
     * Metodo para crear una nueva ubicación a partir de un DTO, devuelve un mapa con el resultado
     * @param createUbicacionDTO DTO con los datos para crear una nueva ubicación
     * @return Mapa con la ubicación creada o un mensaje de error si no se pudo crear, junto con un indicador de éxito.
     */
    public Map<String, Object> create(CreateUbicacionDTO createUbicacionDTO) {
        Map<String, Object> response = new HashMap<>();

        Ubicacion nuevaUbicacion = mapUbicacion(createUbicacionDTO);
        Ubicacion ubicacionGuardada = ubicacionRepository.save(nuevaUbicacion);

        response.put("success", 201);
        response.put("ubicacion", ubicacionGuardada);

        return response;
    }

    /**
     * Metodo para actualizar una ubicación existente a partir de un DTO, devuelve un mapa con el resultado
     * @param id ID de la ubicación a actualizar
     * @param createUbicacionDTO DTO con los datos actualizados de la ubicación
     * @return Mapa con la ubicación actualizada o un mensaje de error si no se pudo actualizar, junto con un indicador de éxito.
     */
    public Map<String, Object> update(Integer id, CreateUbicacionDTO createUbicacionDTO) {
        Map<String, Object> response = new HashMap<>();

        Optional<Ubicacion> ubicacion = ubicacionRepository.findById(id);

        if (ubicacion.isEmpty()) {
            response.put("success", 404);
            response.put("message", "Ubicación no encontrada");
        } else {
            Ubicacion ubicacionActual = ubicacion.get();

            Ubicacion ubicacionActualizada = mapUbicacion(createUbicacionDTO);
            ubicacionActualizada.setIdUbicacion(ubicacionActual.getIdUbicacion());

            Ubicacion ubicacionGuardada = ubicacionRepository.save(ubicacionActualizada);

            response.put("success", 200);
            response.put("ubicacion", ubicacionGuardada);
        }

        return response;
    }

    /**
     * Metodo para eliminar una ubicación por su ID, devuelve un mapa con el resultado
     * @param id ID de la ubicación a eliminar
     * @return Mapa con un mensaje de éxito o error dependiendo del resultado de la eliminación, junto con un indicador de éxito.
     */
    public Map<String, Object> delete(Integer id) {
        Map<String, Object> response = new HashMap<>();

        Optional<Ubicacion> ubicacion = ubicacionRepository.findById(id);

        if (ubicacion.isEmpty()) {
            response.put("success", 404);
            response.put("message", "Ubicación no encontrada");
        } else {
            ubicacionRepository.deleteById(id);

            response.put("success", 204);
            response.put("message", "Ubicación eliminada correctamente");
        }

        return response;
    }

    /**
     * Mapea un CreateUbicacionDTO a una entidad Ubicacion, asignando solo los campos que no son nulos.
     * @param dto DTO con los datos para crear una nueva ubicación
     * @return Entidad Ubicacion con los datos mapeados desde el DTO
     */
    private Ubicacion mapUbicacion(CreateUbicacionDTO dto) {
        Ubicacion ubicacion = new Ubicacion();

        if (dto.getCampus() != null)
            ubicacion.setCampus(dto.getCampus());

        if (dto.getEdificio() != null)
            ubicacion.setEdificio(dto.getEdificio());

        if (dto.getAula() != null)
            ubicacion.setAula(dto.getAula());

        if (dto.getLaboratorio() != null)
            ubicacion.setLaboratorio(dto.getLaboratorio());

        if (dto.getDescripcion() != null)
            ubicacion.setDescripcion(dto.getDescripcion());

        return ubicacion;
    }
}
