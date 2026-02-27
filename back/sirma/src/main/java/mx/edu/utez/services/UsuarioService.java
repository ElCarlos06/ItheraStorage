package mx.edu.utez.services;

import lombok.RequiredArgsConstructor;
import mx.edu.utez.dtos.CreateUsuarioDTO;
import mx.edu.utez.model.Usuario;
import mx.edu.utez.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio para gestionar las operaciones relacionadas con los usuarios.
 * Proporciona métodos para obtener, crear y mapear usuarios utilizando el repositorio de usuarios.
 * @author Ithera Team
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Metodo para obtener todos los usuarios, devuelve un mapa con el resultado
     * @return Lista de usuarios obtenidos del repositorio, junto con un indicador de éxito
     */
    public Map<String, Object> getAll() {
        Map<String, Object> response = new HashMap<>();

        response.put("success", 200);
        response.put("data", usuarioRepository.findAll());

        return response;
    }

    /**
     * Metodo para obtener un usuario por su ID, devuelve un mapa con el resultado
     * @param id ID del usuario a buscar
     * @return Mapa con el usuario encontrado o un mensaje de error si no se encuentra, junto con un indicador de éxito
     */
    public Map<String, Object> getById(Integer id) {
        Map<String, Object> response = new HashMap<>();

        Optional<Usuario> usuario = usuarioRepository.findById(id);

        if (usuario.isEmpty()) {
            response.put("success", 404);
            response.put("message", "Usuario no encontrado");
        } else {
            Usuario usuarioActual = usuario.get();

            response.put("success", 200);
            response.put("usuario", usuarioActual);
        }

        return response;
    }

    /**
     * Metodo para crear un nuevo usuario a partir de un DTO, devuelve un mapa con el resultado
     * @param createUsuarioDTO DTO con los datos para crear un nuevo usuario
     * @return Mapa con el usuario creado o un mensaje de error si no se pudo crear, junto con un indicador de éxito.
     */
    public Map<String, Object> create(CreateUsuarioDTO createUsuarioDTO) {
        Map<String, Object> response = new HashMap<>();

        Usuario nuevoUsuario = mapUsuario(createUsuarioDTO);
        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        response.put("success", 201);
        response.put("usuario", usuarioGuardado);

        return response;
    }

    /**
     * Metodo para actualizar un usuario existente a partir de un DTO, devuelve un mapa con el resultado
     * @param id ID del usuario a actualizar
     * @param createUsuarioDTO DTO con los datos actualizados del usuario
     * @return Mapa con el usuario actualizado o un mensaje de error si no se pudo actualizar, junto con un indicador de éxito.
     */
    public Map<String, Object> update(Integer id, CreateUsuarioDTO createUsuarioDTO) {
        Map<String, Object> response = new HashMap<>();

        Optional<Usuario> usuario = usuarioRepository.findById(id);

        if (usuario.isEmpty()) {
            response.put("success", 404);
            response.put("message", "Usuario no encontrado");
        } else {
            Usuario usuarioActual = usuario.get();

            Usuario usuarioActualizado = mapUsuario(createUsuarioDTO);
            usuarioActualizado.setIdUsuario(usuarioActual.getIdUsuario());

            Usuario usuarioGuardado = usuarioRepository.save(usuarioActualizado);

            response.put("success", 200);
            response.put("usuario", usuarioGuardado);
        }

        return response;
    }

    /**
     * Metodo para eliminar un usuario por su ID, devuelve un mapa con el resultado
     * @param id ID del usuario a eliminar
     * @return Mapa con un mensaje de éxito o error dependiendo del resultado de la eliminación, junto con un indicador de éxito.
     */
    public Map<String, Object> delete(Integer id) {
        Map<String, Object> response = new HashMap<>();

        Optional<Usuario> usuario = usuarioRepository.findById(id);

        if (usuario.isEmpty()) {
            response.put("success", 404);
            response.put("message", "Usuario no encontrado");
        } else {
            usuarioRepository.deleteById(id);

            response.put("success", 204);
            response.put("message", "Usuario eliminado correctamente");
        }

        return response;
    }

    /**
     * Mapea un CreateUsuarioDTO a una entidad Usuario, asignando solo los campos que no son nulos.
     * @param dto DTO con los datos para crear un nuevo usuario
     * @return Entidad Usuario con los datos mapeados desde el DTO
     */
    private Usuario mapUsuario(CreateUsuarioDTO dto) {
        Usuario usuario = new Usuario();

        if (dto.getRol() != null)
            usuario.setRol(dto.getRol());

        if (dto.getMatriculaEmpleado() != null)
            usuario.setMatriculaEmpleado(dto.getMatriculaEmpleado());

        if (dto.getNombreCompleto() != null)
            usuario.setNombreCompleto(dto.getNombreCompleto());

        if (dto.getApellidos() != null)
            usuario.setApellidos(dto.getApellidos());

        if (dto.getCurp() != null)
            usuario.setCurp(dto.getCurp());

        if (dto.getEstatus() != null)
            usuario.setEstatus(dto.getEstatus());

        if (dto.getCorreo() != null)
            usuario.setCorreo(dto.getCorreo());

        if (dto.getContrasenia() != null)
            usuario.setContrasenia(dto.getContrasenia());

        if (dto.getArea() != null)
            usuario.setArea(dto.getArea());

        return usuario;
    }
}
