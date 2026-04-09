package mx.edu.utez.modules.security.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para operaciones de autenticación en SIRMA.
 * <p>
 * Endpoints disponibles:
 * <ul>
 *   <li>{@code POST /api/auth/login} — Autentica credenciales y entrega un token JWT.
 *       Si {@code primer_login = true}, retorna HTTP 403 con la bandera
 *       {@code requiresPasswordChange} para que el front redirija al cambio de contraseña.</li>
 *   <li>{@code POST /api/auth/change-password} — Cambia contraseña. Acepta token (enlace) o
 *       correo+passwordActual (primer acceso). Unifica ambos flujos.</li>
 *   <li>{@code POST /api/auth/request-password-reset} — Olvidé mi contraseña: envía enlace por correo.</li>
 * </ul>
 * </p>
 *
 * @author Ithera Team
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${app.reset-base-url:http://localhost:5173}")
    private String resetBaseUrl;

    /**
     * Autentica credenciales y entrega un token JWT.
     * <p>
     * Si las credenciales son correctas pero el usuario aún no ha cambiado su contraseña
     * temporal ({@code primer_login = true}), retorna HTTP 403 con:
     * <pre>
     * {
     *   "message": "Debes cambiar tu contraseña antes de continuar",
     *   "data": { "requiresPasswordChange": true, "correo": "..." },
     *   "error": false,
     *   "status": "FORBIDDEN"
     * }
     * </pre>
     * El frontend debe detectar {@code data.requiresPasswordChange == true} y
     * redirigir al usuario al formulario de cambio de contraseña.
     * </p>
     *
     * @param dto credenciales de acceso (correo y password)
     * @return {@link ApiResponse} con token en {@code data} o bandera de cambio requerido
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody AuthDTO dto) {
        ApiResponse response = authService.login(dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Cambia la contraseña temporal del usuario por una permanente.
     * <p>
     * Solo aplica cuando {@code primer_login = true}. Después del cambio exitoso,
     * {@code primer_login} queda en {@code false} y el usuario puede iniciar sesión
     * normalmente. La nueva contraseña debe cumplir:
     * <ul>
     *   <li>Mínimo 8 caracteres</li>
     *   <li>Al menos una letra mayúscula</li>
     *   <li>Al menos un dígito</li>
     *   <li>Al menos un carácter especial (@#$%&amp;*!)</li>
     * </ul>
     * </p>
     *
     * @param dto correo, contraseña actual (temporal) y nueva contraseña
     * @return {@link ApiResponse} con resultado de la operación
     */
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(@Valid @RequestBody ChangePasswordDTO dto) {
        ApiResponse response = authService.changePassword(dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Solicitud de restablecimiento de contraseña (olvidé mi contraseña).
     * Genera una nueva contraseña temporal, la envía por correo y marca primer_login = true.
     *
     * @param dto correo del usuario
     * @return ApiResponse con mensaje de éxito
     */
    @PostMapping("/request-password-reset")
    public ResponseEntity<ApiResponse> requestPasswordReset(@Valid @RequestBody RequestPasswordResetDTO dto) {
        ApiResponse response = authService.requestPasswordReset(dto, resetBaseUrl);
        return new ResponseEntity<>(response, response.getStatus());
    }




}

