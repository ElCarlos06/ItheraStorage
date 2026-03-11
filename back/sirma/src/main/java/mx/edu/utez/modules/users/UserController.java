package mx.edu.utez.modules.users;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gestión de usuarios en SIRMA.
 * Proporciona endpoints para operaciones CRUD de usuarios.
 *
 * @author Ithera Team
 */
@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse> findAll() {
        ApiResponse response = userService.findAll();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id) {
        ApiResponse response = userService.findById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping
    public ResponseEntity<ApiResponse> save(@Valid @RequestBody UserDTO dto) {
        ApiResponse response = userService.save(dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody UserDTO dto) {
        ApiResponse response = userService.update(id, dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse> toggleStatus(@PathVariable Long id) {
        ApiResponse response = userService.toggleStatus(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        ApiResponse response = userService.delete(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Busca un usuario por su correo electrónico.
     * <p>
     * Útil para que el front valide si un correo existe antes de continuar
     * con cualquier flujo (recuperación de contraseña, asignaciones, etc.).
     * Retorna 404 con mensaje descriptivo si no se encuentra, para que la UI
     * lo muestre directamente sin parseo adicional.
     * </p>
     * Ejemplo: {@code GET /api/users/by-email?correo=usuario@utez.edu.mx}
     *
     * @param correo correo electrónico a buscar
     * @return usuario encontrado o mensaje de error descriptivo
     */
    @GetMapping("/by-email")
    public ResponseEntity<ApiResponse> findByCorreo(@RequestParam String correo) {
        ApiResponse response = userService.findByCorreo(correo);
        return new ResponseEntity<>(response, response.getStatus());
    }

}
