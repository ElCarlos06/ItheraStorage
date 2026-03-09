package mx.edu.utez.modules.auth;

import lombok.AllArgsConstructor;
import mx.edu.utez.security.jwt.JwtProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Servicio de autenticación que maneja el proceso de login y generación de tokens JWT.
 */
@Service
@AllArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;
    private final JwtProvider jwtProvider;

    /**
     * Autentica al usuario y retorna un token JWT firmado.
     * @param dto credenciales de acceso
     * @return token JWT válido para futuras peticiones
     */
    public String login(AuthDTO dto) {
        // 1. Spring Security valida contra la BD automáticamente usando UserDetailsServiceImpl
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getCorreo(), dto.getPassword())
        );
        // 2. Si las credenciales son correctas, firmamos el token
        return jwtProvider.generateToken(auth);
    }
}
