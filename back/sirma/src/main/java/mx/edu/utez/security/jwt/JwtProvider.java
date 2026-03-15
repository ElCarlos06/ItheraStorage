package mx.edu.utez.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import mx.edu.utez.modules.auth.user_details.UserDetailsImp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Proveedor de utilidades para creación y validación de tokens JWT.
 * Encapsula la llave secreta y el tiempo de expiración del token.
 */
@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private int expiration;

    private Key getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Firma un token JWT con el usuario autenticado.
     *
     * @param authentication contexto de autenticación con el principal ya validado
     * @return token JWT firmado
     */
    public String generateToken(Authentication authentication) {

        // Valida el cast de forma segura (no se me ocurrio nunca pq no sabia que existia instanceof XDDD)
        if (!(authentication.getPrincipal() instanceof UserDetailsImp userPrincipal))
            throw new IllegalArgumentException("Usuario inválido o nulo");

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim("id", userPrincipal.getId())
                .claim("nombre", userPrincipal.getNombreCompleto())
                .claim("role", userPrincipal.getRole())
                .claim("area", userPrincipal.getArea())
                .claim("numeroEmpleado", userPrincipal.getNumeroEmpleado())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + expiration))
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * Obtiene el identificador de usuario (correo) desde un token válido.
     *
     * @param token token JWT portador
     * @return correo contenido en el subject
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getSecretKey()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Obtiene el usuario autenticado actual desde el contexto de seguridad.
     * <p>
     * Recupera los detalles del usuario (UserDetailsImp) almacenados en el
     * SecurityContextHolder, evitando así una nueva consulta a la base de datos.
     * </p>
     *
     * @return UserDetailsImp con la información del usuario en sesión, o null si no hay sesión.
     */
    public UserDetailsImp getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImp) {
            return (UserDetailsImp) authentication.getPrincipal();
        }
        return null;
    }


    /**
     * Verifica integridad y vigencia del token.
     *
     * @param token token JWT por validar
     * @return true si el token es válido y no ha expirado
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Expiración para token de restablecimiento: 1 hora */
    private static final int RESET_EXPIRATION_MS = 3600 * 1000;

    /**
     * Genera un token JWT para restablecimiento de contraseña.
     * El subject contiene el correo del usuario.
     *
     * @param correo correo del usuario que solicita el restablecimiento
     * @return token JWT con expiración de 1 hora
     */
    public String generateResetToken(String correo) {
        return Jwts.builder()
                .setSubject(correo)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + RESET_EXPIRATION_MS))
                .signWith(getSecretKey())
                .compact();
    }

}
