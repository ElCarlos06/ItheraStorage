package mx.edu.utez.modules.users;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    /**
     * Obtiene el listado completo de usuarios registrados.
     *
     * @return ResponseEntity con la lista de usuarios.
     */
    @GetMapping("")
    public ResponseEntity<ApiResponse> findAll(@PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
        ApiResponse response = userService.findAll(pageable);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Busca un usuario por su identificador único.
     *
     * @param id ID del usuario.
     * @return ResponseEntity con los detalles del usuario encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id) {
        ApiResponse response = userService.findById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Crea un nuevo usuario en el sistema.
     *
     * @param dto Objeto de transferencia de datos con la información del usuario a crear.
     * @return ResponseEntity con el usuario creado y estado CREATED.
     */
    @PostMapping
    public ResponseEntity<ApiResponse> save(@Valid @RequestBody UserDTO dto) {
        ApiResponse response = userService.save(dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Actualiza la información de un usuario existente.
     *
     * @param id  ID del usuario a modificar.
     * @param dto Datos actualizados del usuario.
     * @return ResponseEntity con el usuario actualizado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody UserDTO dto) {
        ApiResponse response = userService.update(id, dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Habilita o deshabilita el acceso de un usuario (Soft Delete / Toggle).
     *
     * @param id ID del usuario a modificar su estado.
     * @return ResponseEntity con el nuevo estado del usuario.
     */
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
