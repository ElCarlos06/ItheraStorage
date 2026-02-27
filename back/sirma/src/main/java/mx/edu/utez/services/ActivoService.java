package mx.edu.utez.services;

import lombok.RequiredArgsConstructor;
import mx.edu.utez.dtos.CreateActivoDTO;
import mx.edu.utez.model.Activo;
import mx.edu.utez.repository.ActivoRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio para gestionar las operaciones relacionadas con los activos.
 * Proporciona métodos para obtener, crear y mapear activos utilizando el repositorio de activos.
 * @author Ithera Team
 */
@Service
@RequiredArgsConstructor
public class ActivoService {

    private final ActivoRepository activoRepository;

    /**
     * Metodo para obtener todos los activos, devuelve un mapa con el resultado
     * @return Lista de activos obtenidos del repositorio, junto con un indicador de éxito
    */
    public Map<String, Object> getAll() {
        Map<String, Object> response = new HashMap<>();

        response.put("success", 200);
        response.put("data", activoRepository.findAll());

        return response;
    }

    /**
     * Metodo para obtener un activo por su ID, devuelve un mapa con el resultado
     * @param id ID del activo a buscar
     * @return Mapa con el activo encontrado o un mensaje de error si no se encuentra, junto con un indicador de éxito
     */
    public Map<String, Object> getById(Integer id) {
        Map<String, Object> response = new HashMap<>();

        Optional<Activo> activo = activoRepository.findById(id);

        if( activo.isEmpty() ) {
            response.put("success", 404);
            response.put("message", "Activo no encontrado");
        } else {
            Activo activoActual = activo.get();

            response.put("success", 200);
            response.put("activo", activoActual);
        }

        return response;
    }

    /**
     * Metodo para crear un nuevo activo a partir de un DTO, devuelve un mapa con el resultado
     * @param createActivoDTO DTO con los datos para crear un nuevo activo
     * @return Mapa con el activo creado o un mensaje de error si no se pudo crear, junto con un indicador de éxito.
     */
    public Map<String, Object> create(CreateActivoDTO createActivoDTO) {
        Map<String, Object> response = new HashMap<>();

        Activo nuevoActivo = mapActivo(createActivoDTO);
        Activo activoGuardado = activoRepository.save(nuevoActivo);

        response.put("success", 201);
        response.put("activo", activoGuardado);

        return response;
    }

    /**
     * Metodo para actualizar un activo existente a partir de un DTO, devuelve un mapa con el resultado
     * @param id ID del activo a actualizar
     * @param createActivoDTO DTO con los datos actualizados del activo
     * @return Mapa con el activo actualizado o un mensaje de error si no se pudo actualizar, junto con un indicador de éxito.
     */
    public Map<String, Object> update(Integer id, CreateActivoDTO createActivoDTO) {
        Map<String, Object> response = new HashMap<>();

        Optional<Activo> activo = activoRepository.findById(id);

        if( activo.isEmpty() ) {
            response.put("success", 404);
            response.put("message", "Activo no encontrado");
        } else {
            Activo activoActual = activo.get();

            Activo activoActualizado = mapActivo(createActivoDTO);
            activoActualizado.setIdActivo(activoActual.getIdActivo());

            Activo activoGuardado = activoRepository.save(activoActualizado);

            response.put("success", 200);
            response.put("activo", activoGuardado);
        }

        return response;
    }

    /**
     * Metodo para eliminar un activo por su ID, devuelve un mapa con el resultado
     * @param id ID del activo a eliminar
     * @return Mapa con un mensaje de éxito o error dependiendo del resultado de la eliminación, junto con un indicador de éxito.
     */
    public Map<String, Object> delete(Integer id) {
        Map<String, Object> response = new HashMap<>();

        Optional<Activo> activo = activoRepository.findById(id);

        if( activo.isEmpty() ) {
            response.put("success", 404);
            response.put("message", "Activo no encontrado");
        } else {
            activoRepository.deleteById(id);

            response.put("success", 204);
            response.put("message", "Activo eliminado correctamente");
        }

        return response;
    }

    /**
     * Mapea un CreateActivoDTO a una entidad Activo, asignando solo los campos que no son nulos.
     * @param createActivoDTO DTO con los datos para crear un nuevo activo
     * @return Entidad Activo con los datos mapeados desde el DTO
     */
    private Activo mapActivo(CreateActivoDTO createActivoDTO) {

        Activo activo = new Activo();

        if (createActivoDTO.getTipoActivo() != null)
            activo.setTipoActivo(createActivoDTO.getTipoActivo());

        if (createActivoDTO.getUbicacion() != null)
            activo.setUbicacion(createActivoDTO.getUbicacion());

        if (createActivoDTO.getEtiquetaProducto() != null)
            activo.setEtiquetaProducto(createActivoDTO.getEtiquetaProducto());

        if (createActivoDTO.getEstatus() != null)
            activo.setEstatus(createActivoDTO.getEstatus());

        if (createActivoDTO.getDescripcion() != null)
            activo.setDescripcion(createActivoDTO.getDescripcion());

        if (createActivoDTO.getFechaAlta() != null)
            activo.setFechaAlta(createActivoDTO.getFechaAlta());

        if (createActivoDTO.getCosto() != null)
            activo.setCosto(createActivoDTO.getCosto());

        if (createActivoDTO.getQrCodeUrl() != null)
            activo.setQrCodeUrl(createActivoDTO.getQrCodeUrl());

        return activo;
    }

}
