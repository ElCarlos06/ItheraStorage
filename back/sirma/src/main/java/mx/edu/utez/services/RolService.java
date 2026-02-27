package mx.edu.utez.services;

import lombok.RequiredArgsConstructor;
import mx.edu.utez.dtos.CreateRolDTO;
import mx.edu.utez.model.Rol;
import mx.edu.utez.repository.RolRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio para gestionar las operaciones relacionadas con los roles.
 * Proporciona métodos para obtener, crear y mapear roles utilizando el repositorio de roles.
 * @author Ithera Team
 */
@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepository rolRepository;

    /**
     * Metodo para obtener todos los roles, devuelve un mapa con el resultado
     * @return Lista de roles obtenidos del repositorio, junto con un indicador de éxito
     */
    public Map<String, Object> getAll() {
        Map<String, Object> response = new HashMap<>();

        response.put("success", 200);
        response.put("data", rolRepository.findAll());

        return response;
    }

    /**
     * Metodo para obtener un rol por su ID, devuelve un mapa con el resultado
     * @param id ID del rol a buscar
     * @return Mapa con el rol encontrado o un mensaje de error si no se encuentra, junto con un indicador de éxito
     */
    public Map<String, Object> getById(Integer id) {
        Map<String, Object> response = new HashMap<>();

        Optional<Rol> rol = rolRepository.findById(id);

        if (rol.isEmpty()) {
            response.put("success", 404);
            response.put("message", "Rol no encontrado");
        } else {
            Rol rolActual = rol.get();

            response.put("success", 200);
            response.put("rol", rolActual);
        }

        return response;
    }

    /**
     * Metodo para crear un nuevo rol a partir de un DTO, devuelve un mapa con el resultado
     * @param createRolDTO DTO con los datos para crear un nuevo rol
     * @return Mapa con el rol creado o un mensaje de error si no se pudo crear, junto con un indicador de éxito.
     */
    public Map<String, Object> create(CreateRolDTO createRolDTO) {
        Map<String, Object> response = new HashMap<>();

        Rol nuevoRol = mapRol(createRolDTO);
        Rol rolGuardado = rolRepository.save(nuevoRol);

        response.put("success", 201);
        response.put("rol", rolGuardado);

        return response;
    }

    /**
     * Metodo para actualizar un rol existente a partir de un DTO, devuelve un mapa con el resultado
     * @param id ID del rol a actualizar
     * @param createRolDTO DTO con los datos actualizados del rol
     * @return Mapa con el rol actualizado o un mensaje de error si no se pudo actualizar, junto con un indicador de éxito.
     */
    public Map<String, Object> update(Integer id, CreateRolDTO createRolDTO) {
        Map<String, Object> response = new HashMap<>();

        Optional<Rol> rol = rolRepository.findById(id);

        if (rol.isEmpty()) {
            response.put("success", 404);
            response.put("message", "Rol no encontrado");
        } else {
            Rol rolActual = rol.get();

            Rol rolActualizado = mapRol(createRolDTO);
            rolActualizado.setIdRol(rolActual.getIdRol());

            Rol rolGuardado = rolRepository.save(rolActualizado);

            response.put("success", 200);
            response.put("rol", rolGuardado);
        }

        return response;
    }

    /**
     * Metodo para eliminar un rol por su ID, devuelve un mapa con el resultado
     * @param id ID del rol a eliminar
     * @return Mapa con un mensaje de éxito o error dependiendo del resultado de la eliminación, junto con un indicador de éxito.
     */
    public Map<String, Object> delete(Integer id) {
        Map<String, Object> response = new HashMap<>();

        Optional<Rol> rol = rolRepository.findById(id);

        if (rol.isEmpty()) {
            response.put("success", 404);
            response.put("message", "Rol no encontrado");
        } else {
            rolRepository.deleteById(id);

            response.put("success", 204);
            response.put("message", "Rol eliminado correctamente");
        }

        return response;
    }

    /**
     * Mapea un CreateRolDTO a una entidad Rol, asignando solo los campos que no son nulos.
     * @param dto DTO con los datos para crear un nuevo rol
     * @return Entidad Rol con los datos mapeados desde el DTO
     */
    private Rol mapRol(CreateRolDTO dto) {
        Rol rol = new Rol();

        if (dto.getNombreRol() != null)
            rol.setNombreRol(dto.getNombreRol());

        return rol;
    }
}
