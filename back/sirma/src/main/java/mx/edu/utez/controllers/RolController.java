package mx.edu.utez.controllers;

import lombok.RequiredArgsConstructor;
import mx.edu.utez.dtos.CreateRolDTO;
import mx.edu.utez.services.RolService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static mx.edu.utez.util.HandleStatus.getStatus;

/**
 * Controlador REST para gestionar las operaciones relacionadas con los roles.
 * Proporciona endpoints para obtener, crear, actualizar y eliminar roles.
 * Utiliza RolService para la lógica de negocio y devuelve respuestas con el estado HTTP adecuado.
 * Permite solicitudes CORS desde cualquier origen.
 * @author Ithera Team
 */
@RestController
@RequestMapping("/api/rol")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class RolController {

    private final RolService rolService;

    /**
     * Obtiene todos los roles mediante una petición GET
     * Mediante un mapa se devuelve la lista de roles por la inyección del servicio
     * @return ResponseEntity con la lista de roles y el estado HTTP
     */
    @GetMapping("")
    public ResponseEntity<Object> getAll() {
        Map<String, Object> response = rolService.getAll();
        int code = (int) response.get("success");
        return new ResponseEntity<>(response, getStatus(code));
    }

    /**
     * Obtiene un rol por su ID mediante una petición GET
     * @param id ID del rol a buscar
     * @return ResponseEntity con el rol encontrado y el estado HTTP
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Integer id) {
        Map<String, Object> response = rolService.getById(id);
        int code = (int) response.get("success");
        return new ResponseEntity<>(response, getStatus(code));
    }

    /**
     * Crea un nuevo rol mediante una petición POST
     * @param createRolDTO DTO con los datos del nuevo rol
     * @return ResponseEntity con el resultado de la creación y el estado HTTP
     */
    @PostMapping("")
    public ResponseEntity<Object> create(@RequestBody CreateRolDTO createRolDTO) {
        Map<String, Object> response = rolService.create(createRolDTO);
        int code = (int) response.get("success");
        return new ResponseEntity<>(response, getStatus(code));
    }

    /**
     * Actualiza un rol existente mediante una petición PUT
     * @param id ID del rol a actualizar
     * @param createRolDTO DTO con los datos actualizados del rol
     * @return ResponseEntity con el resultado de la actualización y el estado HTTP
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Integer id, @RequestBody CreateRolDTO createRolDTO) {
        Map<String, Object> response = rolService.update(id, createRolDTO);
        int code = (int) response.get("success");
        return new ResponseEntity<>(response, getStatus(code));
    }

    /**
     * Elimina un rol mediante una petición DELETE
     * @param id ID del rol a eliminar
     * @return ResponseEntity con el resultado de la eliminación y el estado HTTP
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {
        Map<String, Object> response = rolService.delete(id);
        int code = (int) response.get("success");
        return new ResponseEntity<>(response, getStatus(code));
    }
}
