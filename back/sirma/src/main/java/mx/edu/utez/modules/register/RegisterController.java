package mx.edu.utez.modules.register;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para el registro de nuevos usuarios en SIRMA.
 * Endpoint público (no requiere autenticación) — configurado en MainSecurity.
 *
 * @author Ithera Team
 */
@RestController
@RequestMapping("/api/register")
@AllArgsConstructor
public class RegisterController {

    private final RegisterService registerService;

    /**
     * Registra un nuevo usuario, genera su número de empleado y envía
     * la contraseña temporal al correo proporcionado.
     *
     * @param dto datos de registro con validaciones básicas de Bean Validation
     * @return respuesta con el resultado del registro
     */
    @PostMapping
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterDTO dto) {
        ApiResponse response = registerService.register(dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

}
