package mx.edu.utez.modules.auth;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.register.MailService;
import mx.edu.utez.modules.users.User;
import mx.edu.utez.modules.users.UserRepository;
import mx.edu.utez.security.jwt.JwtProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Optional;

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
    private final MailService mailService;

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
        Authentication auth;

        try {
             auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getCorreo(), dto.getPassword())
            );
        } catch (BadCredentialsException bcd) {
            return new ApiResponse(
                    "Credenciales incorrectas. Verifica tu correo y contraseña.",
                    true,
                    HttpStatus.UNAUTHORIZED
            );
        }

        // 2. Recuperar entidad para verificar primer_login
        User user = userRepository.findByCorreoIgnoreCase(dto.getCorreo())
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
     * Cambia la contraseña. Unifica dos flujos:
     * <ul>
     *   <li><b>Con token</b>: desde enlace del correo. No requiere contraseña actual.</li>
     *   <li><b>Con correo + passwordActual</b>: primer acceso (primer_login). Requiere contraseña temporal.</li>
     * </ul>
     */
    @Transactional
    public ApiResponse changePassword(ChangePasswordDTO dto) {
        User user;
        boolean requierePasswordActual = false;

        if (dto.getToken() != null && !dto.getToken().isBlank()) {
            // Flujo: enlace del correo (olvidé mi contraseña)
            if (!jwtProvider.validateToken(dto.getToken().trim())) {
                return new ApiResponse("El enlace ha expirado o no es válido. Solicita uno nuevo.", true, HttpStatus.BAD_REQUEST);
            }
            String correo = jwtProvider.getUsernameFromToken(dto.getToken().trim());
            user = userRepository.findByCorreoIgnoreCase(correo).orElse(null);
            if (user == null) {
                return new ApiResponse("Usuario no encontrado.", true, HttpStatus.NOT_FOUND);
            }
        } else if (dto.getCorreo() != null && !dto.getCorreo().isBlank()) {
            // Flujo: primer acceso (contraseña temporal)
            requierePasswordActual = true;
            user = userRepository.findByCorreoIgnoreCase(dto.getCorreo().trim())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            if (dto.getPasswordActual() == null || dto.getPasswordActual().isBlank()) {
                return new ApiResponse("La contraseña temporal es obligatoria", true, HttpStatus.BAD_REQUEST);
            }
            if (!passwordEncoder.matches(dto.getPasswordActual(), user.getPasswordHash())) {
                return new ApiResponse("La contraseña temporal es incorrecta", true, HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ApiResponse("Se requiere token o correo con contraseña actual", true, HttpStatus.BAD_REQUEST);
        }

        String nuevaPassword = dto.getPasswordNueva();
        if (!isPasswordSecure(nuevaPassword)) {
            return new ApiResponse(
                    "La contraseña debe tener al menos 8 caracteres, una mayúscula, un número y un carácter especial (@#$%&*!)",
                    true, HttpStatus.BAD_REQUEST);
        }
        if (requierePasswordActual && passwordEncoder.matches(nuevaPassword, user.getPasswordHash())) {
            return new ApiResponse("La nueva contraseña no puede ser igual a la temporal", true, HttpStatus.BAD_REQUEST);
        }

        user.setPasswordHash(passwordEncoder.encode(nuevaPassword));
        user.setPrimerLogin(false);
        userRepository.save(user);

        return new ApiResponse("Contraseña actualizada correctamente. Ya puedes iniciar sesión.", HttpStatus.OK);
    }

    /**
     * Solicitud de restablecimiento de contraseña (olvidé mi contraseña).
     * Envía un correo con enlace para que el usuario establezca la contraseña que desee.
     *
     * @param dto correo del usuario que solicita el restablecimiento
     * @param resetBaseUrl URL base del frontend para el enlace (ej: http://localhost:5173)
     * @return ApiResponse con mensaje de éxito
     */
    @Transactional
    public ApiResponse requestPasswordReset(RequestPasswordResetDTO dto, String resetBaseUrl) {
        String correo = dto.getCorreo().trim().toLowerCase();
        User user = userRepository.findByCorreoIgnoreCase(correo).orElse(null);
        if (user == null) {
            return new ApiResponse("No se encontró una cuenta con ese correo", true, HttpStatus.NOT_FOUND);
        }

        if (!Boolean.TRUE.equals(user.getEsActivo())) {
            return new ApiResponse("La cuenta está desactivada. Contacta al administrador.", true, HttpStatus.FORBIDDEN);
        }

        String resetToken = jwtProvider.generateResetToken(correo);
        String resetLink = resetBaseUrl.replaceAll("/$", "") + "/reset-password?token="
                + java.net.URLEncoder.encode(resetToken, java.nio.charset.StandardCharsets.UTF_8);

        try {
            mailService.enviarLinkRestablecimiento(correo, user.getNombreCompleto(), resetLink);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo enviar el correo. Intenta más tarde.");
        }

        return new ApiResponse(
                "Revisa tu correo. Te enviamos un enlace para restablecer tu contraseña.",
                HttpStatus.OK);
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
