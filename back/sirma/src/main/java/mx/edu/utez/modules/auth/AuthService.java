package mx.edu.utez.modules.auth;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.users.User;
import mx.edu.utez.modules.users.UserRepository;
import mx.edu.utez.security.jwt.JwtProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de autenticación que maneja el proceso de login, generación de tokens JWT
 * y el cambio de contraseña en el primer acceso del usuario.
 * <p>
 * Si {@code primer_login = true}, el login retorna un error 403 con bandera
 * {@code requiresPasswordChange = true} para que el front redirija al flujo de cambio.
 * </p>
 *
 * @author Ithera Team
 */
@Service
@AllArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Autentica al usuario y retorna un {@link ApiResponse} con el token JWT.
     * <p>
     * Si las credenciales son válidas pero {@code primer_login = true}, retorna
     * HTTP 403 con {@code requiresPasswordChange = true} para que el frontend
     * redirija al flujo de cambio de contraseña antes de permitir el acceso.
     * </p>
     *
     * @param dto credenciales de acceso (correo + password)
     * @return {@link ApiResponse} con token en {@code data} o mensaje de cambio requerido
     */
    public ApiResponse login(AuthDTO dto) {
        // 1. Spring Security valida credenciales contra la BD (lanza excepción si son incorrectas)
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getCorreo(), dto.getPassword())
        );

        // 2. Recuperar entidad para verificar primer_login
        User user = userRepository.findByCorreo(dto.getCorreo())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 3. Si es primer acceso, bloquear el login y avisar al front
        if (Boolean.TRUE.equals(user.getPrimerLogin())) {
            return new ApiResponse(
                    "Debes cambiar tu contraseña antes de continuar",
                    new PrimerLoginResponse(true, dto.getCorreo()),
                    HttpStatus.FORBIDDEN
            );
        }

        // 4. Credenciales correctas y contraseña ya cambiada → generar token
        String token = jwtProvider.generateToken(auth);
        return new ApiResponse("Login exitoso", new TokenResponse(token), HttpStatus.OK);
    }

    /**
     * Cambia la contraseña de un usuario en su primer acceso y desactiva la bandera
     * {@code primer_login}, habilitando el acceso completo al sistema.
     * <p>
     * Reglas de la nueva contraseña:
     * <ul>
     *   <li>Mínimo 8 caracteres</li>
     *   <li>Al menos una letra mayúscula</li>
     *   <li>Al menos un dígito</li>
     *   <li>Al menos un carácter especial (@#$%&amp;*!)</li>
     * </ul>
     * </p>
     *
     * @param dto datos de cambio: correo, contraseña temporal (actual) y nueva contraseña
     * @return {@link ApiResponse} con el resultado de la operación
     */
    @Transactional
    public ApiResponse changePassword(ChangePasswordDTO dto) {

        // 1. Verificar que el usuario exista
        User user = userRepository.findByCorreo(dto.getCorreo().trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Verificar que la contraseña actual (temporal) sea correcta
        if (!passwordEncoder.matches(dto.getPasswordActual(), user.getPasswordHash())) {
            return new ApiResponse("La contraseña actual es incorrecta", true, HttpStatus.UNAUTHORIZED);
        }

        // 3. Validar que la nueva contraseña cumpla con el nivel mínimo de seguridad
        String nuevaPassword = dto.getPasswordNueva();
        if (!isPasswordSecure(nuevaPassword)) {
            return new ApiResponse(
                    "La nueva contraseña debe tener al menos 8 caracteres, una mayúscula, un número y un carácter especial (@#$%&*!)",
                    true, HttpStatus.BAD_REQUEST);
        }

        // 4. Verificar que la nueva contraseña sea diferente a la temporal
        if (passwordEncoder.matches(nuevaPassword, user.getPasswordHash())) {
            return new ApiResponse(
                    "La nueva contraseña no puede ser igual a la contraseña temporal",
                    true, HttpStatus.BAD_REQUEST);
        }

        // 5. Aplicar cambios: cifrar nueva contraseña y marcar primer_login = false
        user.setPasswordHash(passwordEncoder.encode(nuevaPassword));
        user.setPrimerLogin(false);
        userRepository.save(user);

        return new ApiResponse("Contraseña actualizada correctamente. Ya puedes iniciar sesión.", HttpStatus.OK);
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    /**
     * Valida que la contraseña tenga al menos 8 caracteres, una mayúscula,
     * un dígito y un carácter especial permitido.
     */
    private boolean isPasswordSecure(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasUpper   = password.chars().anyMatch(Character::isUpperCase);
        boolean hasDigit   = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(c -> "@#$%&*!".indexOf(c) >= 0);
        return hasUpper && hasDigit && hasSpecial;
    }

    // ── Clases internas de respuesta ───────────────────────────────────────

    /**
     * Payload retornado cuando el login bloquea al usuario por primer_login.
     * El front usa {@code requiresPasswordChange} para redirigir al flujo correcto.
     */
    public record PrimerLoginResponse(boolean requiresPasswordChange, String correo) {}

    /**
     * Payload retornado en un login exitoso.
     */
    public record TokenResponse(String token) {}
}
