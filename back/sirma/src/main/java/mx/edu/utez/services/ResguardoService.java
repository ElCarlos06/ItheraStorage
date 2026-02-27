package mx.edu.utez.services;

import lombok.RequiredArgsConstructor;
import mx.edu.utez.dtos.CreateResguardoDTO;
import mx.edu.utez.model.Resguardo;
import mx.edu.utez.repository.ResguardoRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio para gestionar las operaciones relacionadas con los resguardos.
 * Proporciona métodos para obtener, crear y mapear resguardos utilizando el repositorio de resguardos.
 * @author Ithera Team
 */
@Service
@RequiredArgsConstructor
public class ResguardoService {

    private final ResguardoRepository resguardoRepository;

    /**
     * Metodo para obtener todos los resguardos, devuelve un mapa con el resultado
     * @return Lista de resguardos obtenidos del repositorio, junto con un indicador de éxito
     */
    public Map<String, Object> getAll() {
        Map<String, Object> response = new HashMap<>();

        response.put("success", 200);
        response.put("data", resguardoRepository.findAll());

        return response;
    }

    /**
     * Metodo para obtener un resguardo por su ID, devuelve un mapa con el resultado
     * @param id ID del resguardo a buscar
     * @return Mapa con el resguardo encontrado o un mensaje de error si no se encuentra, junto con un indicador de éxito
     */
    public Map<String, Object> getById(Integer id) {
        Map<String, Object> response = new HashMap<>();

        Optional<Resguardo> resguardo = resguardoRepository.findById(id);

        if (resguardo.isEmpty()) {
            response.put("success", 404);
            response.put("message", "Resguardo no encontrado");
        } else {
            Resguardo resguardoActual = resguardo.get();

            response.put("success", 200);
            response.put("resguardo", resguardoActual);
        }

        return response;
    }

    /**
     * Metodo para crear un nuevo resguardo a partir de un DTO, devuelve un mapa con el resultado
     * @param createResguardoDTO DTO con los datos para crear un nuevo resguardo
     * @return Mapa con el resguardo creado o un mensaje de error si no se pudo crear, junto con un indicador de éxito.
     */
    public Map<String, Object> create(CreateResguardoDTO createResguardoDTO) {
        Map<String, Object> response = new HashMap<>();

        Resguardo nuevoResguardo = mapResguardo(createResguardoDTO);
        Resguardo resguardoGuardado = resguardoRepository.save(nuevoResguardo);

        response.put("success", 201);
        response.put("resguardo", resguardoGuardado);

        return response;
    }

    /**
     * Metodo para actualizar un resguardo existente a partir de un DTO, devuelve un mapa con el resultado
     * @param id ID del resguardo a actualizar
     * @param createResguardoDTO DTO con los datos actualizados del resguardo
     * @return Mapa con el resguardo actualizado o un mensaje de error si no se pudo actualizar, junto con un indicador de éxito.
     */
    public Map<String, Object> update(Integer id, CreateResguardoDTO createResguardoDTO) {
        Map<String, Object> response = new HashMap<>();

        Optional<Resguardo> resguardo = resguardoRepository.findById(id);

        if (resguardo.isEmpty()) {
            response.put("success", 404);
            response.put("message", "Resguardo no encontrado");
        } else {
            Resguardo resguardoActual = resguardo.get();

            Resguardo resguardoActualizado = mapResguardo(createResguardoDTO);
            resguardoActualizado.setIdResguardo(resguardoActual.getIdResguardo());

            Resguardo resguardoGuardado = resguardoRepository.save(resguardoActualizado);

            response.put("success", 200);
            response.put("resguardo", resguardoGuardado);
        }

        return response;
    }

    /**
     * Metodo para eliminar un resguardo por su ID, devuelve un mapa con el resultado
     * @param id ID del resguardo a eliminar
     * @return Mapa con un mensaje de éxito o error dependiendo del resultado de la eliminación, junto con un indicador de éxito.
     */
    public Map<String, Object> delete(Integer id) {
        Map<String, Object> response = new HashMap<>();

        Optional<Resguardo> resguardo = resguardoRepository.findById(id);

        if (resguardo.isEmpty()) {
            response.put("success", 404);
            response.put("message", "Resguardo no encontrado");
        } else {
            resguardoRepository.deleteById(id);

            response.put("success", 204);
            response.put("message", "Resguardo eliminado correctamente");
        }

        return response;
    }

    /**
     * Mapea un CreateResguardoDTO a una entidad Resguardo, asignando solo los campos que no son nulos.
     * @param dto DTO con los datos para crear un nuevo resguardo
     * @return Entidad Resguardo con los datos mapeados desde el DTO
     */
    private Resguardo mapResguardo(CreateResguardoDTO dto) {
        Resguardo resguardo = new Resguardo();

        if (dto.getActivo() != null)
            resguardo.setActivo(dto.getActivo());

        if (dto.getUsuario() != null)
            resguardo.setUsuario(dto.getUsuario());

        if (dto.getFechaAsignacion() != null)
            resguardo.setFechaAsignacion(dto.getFechaAsignacion());

        if (dto.getFechaDevolucion() != null)
            resguardo.setFechaDevolucion(dto.getFechaDevolucion());

        if (dto.getEstatusDevolucion() != null)
            resguardo.setEstatusDevolucion(dto.getEstatusDevolucion());

        if (dto.getVideoUrl() != null)
            resguardo.setVideoUrl(dto.getVideoUrl());

        return resguardo;
    }
}
