package mx.edu.utez.controllers;

import lombok.RequiredArgsConstructor;
import mx.edu.utez.dtos.CreateUsuarioDTO;
import mx.edu.utez.services.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static mx.edu.utez.util.HandleStatus.getStatus;

/**
 * Controlador REST para gestionar las operaciones relacionadas con los usuarios.
 * Proporciona endpoints para obtener, crear, actualizar y eliminar usuarios.
 * Utiliza UsuarioService para la lógica de negocio y devuelve respuestas con el estado HTTP adecuado.
 * Permite solicitudes CORS desde cualquier origen.
 * @author Ithera Team
 */
@RestController
@RequestMapping("/api/usuario")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Obtiene todos los usuarios mediante una petición GET
     * Mediante un mapa se devuelve la lista de usuarios por la inyección del servicio
     * @return ResponseEntity con la lista de usuarios y el estado HTTP
     */
    @GetMapping("")
    public ResponseEntity<Object> getAll() {
        Map<String, Object> response = usuarioService.getAll();

        int code = (int) response.get("success");

        return new ResponseEntity<>(response, getStatus(code));
    }

    /**
     * Obtiene un usuario por su ID mediante una petición GET
     * @param id ID del usuario a buscar
     * @return ResponseEntity con el usuario encontrado y el estado HTTP
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Integer id) {
        Map<String, Object> response = usuarioService.getById(id);

        int code = (int) response.get("success");

        return new ResponseEntity<>(response, getStatus(code));
    }

    /**
     * Crea un nuevo usuario mediante una petición POST
     * @param createUsuarioDTO DTO con los datos del nuevo usuario
     * @return ResponseEntity con el resultado de la creación y el estado HTTP
     */
    @PostMapping("")
    public ResponseEntity<Object> create(@RequestBody CreateUsuarioDTO createUsuarioDTO) {
        Map<String, Object> response = usuarioService.create(createUsuarioDTO);

        int code = (int) response.get("success");

        return new ResponseEntity<>(response, getStatus(code));
    }

    /**
     * Actualiza un usuario existente mediante una petición PUT
     * @param id ID del usuario a actualizar
     * @param createUsuarioDTO DTO con los datos actualizados del usuario
     * @return ResponseEntity con el resultado de la actualización y el estado HTTP
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Integer id, @RequestBody CreateUsuarioDTO createUsuarioDTO) {
        Map<String, Object> response = usuarioService.update(id, createUsuarioDTO);

        int code = (int) response.get("success");

        return new ResponseEntity<>(response, getStatus(code));
    }

    /**
     * Elimina un usuario mediante una petición DELETE
     * @param id ID del usuario a eliminar
     * @return ResponseEntity con el resultado de la eliminación y el estado HTTP
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {
        Map<String, Object> response = usuarioService.delete(id);

        int code = (int) response.get("success");

        return new ResponseEntity<>(response, getStatus(code));
    }
}
