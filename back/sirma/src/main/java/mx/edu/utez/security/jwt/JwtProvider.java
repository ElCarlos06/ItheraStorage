package mx.edu.utez.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
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

    private final String SECRET = "EstaEsUnaClaveSuperSecretaYDebeSerMuyLargaParaQueSeaSegura1234567890";
    private final int EXPIRATION = 3600 * 1000; // 1 hora en milisegundos

    private Key getSecretKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    /**
     * Firma un token JWT con el usuario autenticado.
     *
     * @param authentication contexto de autenticación con el principal ya validado
     * @return token JWT firmado
     */
    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + EXPIRATION))
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

}
