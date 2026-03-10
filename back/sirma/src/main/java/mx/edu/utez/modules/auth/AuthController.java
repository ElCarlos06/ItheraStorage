package mx.edu.utez.modules.auth;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para operaciones de autenticación en SIRMA.
 * Maneja el login de usuarios y generación de tokens JWT.
 *
 * @author Ithera Team
 */
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Autentica credenciales y entrega un token JWT.
     * @param dto credenciales de acceso (correo y password)
     * @return token en el cuerpo de la respuesta
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody AuthDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}
