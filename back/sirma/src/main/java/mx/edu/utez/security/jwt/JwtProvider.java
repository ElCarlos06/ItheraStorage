package mx.edu.utez.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import mx.edu.utez.modules.security.auth.user_details.UserDetailsImp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

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

    private Key secretKey;

    private final HttpServletRequest request;

    public JwtProvider(HttpServletRequest request) {
        this.request = request;
    }

    @PostConstruct
    private void inicializarLLave() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Obtiene la dirección ip del cliente xd
     * @param req
     * @return
     */
    private String getClientIp(HttpServletRequest req) {
        String xfHeader = req.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || "unknown".equalsIgnoreCase(xfHeader))
            return req.getRemoteAddr();

        return xfHeader.split(",")[0].trim();
    }

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

        String ipAddress = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim("id", userPrincipal.getId())
                .claim("nombre", userPrincipal.getNombreCompleto())
                .claim("role", userPrincipal.getRole())
                .claim("area", userPrincipal.getArea())
                .claim("numeroEmpleado", userPrincipal.getNumeroEmpleado())
                .claim("ip", ipAddress)
                .claim("userAgent", userAgent)
                .claim("type", "auth")
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
    public Optional<UserDetailsImp> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImp user)
            return Optional.of(user);

        return Optional.empty();
    }


    public boolean validateToken(String token) {
        try {

            var claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();


            String type = claims.get("type", String.class);
            if (!"auth".equals(type)) return false;

            // Verificar IP (ignorar si es un token que no fue generado con IP, ej. reset token)
            if (claims.containsKey("ip")) {

                String tokenIp = claims.get("ip", String.class);
                String currentIp = getClientIp(request);

                if (tokenIp != null && !tokenIp.equals(currentIp))
                    return false;

            }

            // Verificar User-Agent
            if (claims.containsKey("userAgent")) {
                String tokenUserAgent = claims.get("userAgent", String.class);
                String currentUserAgent = request.getHeader("User-Agent");
                return tokenUserAgent == null || tokenUserAgent.equals(currentUserAgent);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Genera un token JWT para restablecimiento de contraseña.
     * El subject contiene el correo del usuario.
     *
     * @param correo correo del usuario que solicita el restablecimiento
     * @return token JWT con expiración de 1 hora
     */
    public String generateResetToken(String correo) {
        // 1 hora para tokens de restablecimiento
        long RESET_EXPIRATION_MS = 3600000;
        return Jwts.builder()
                .setSubject(correo)
                .claim("type", "reset")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + RESET_EXPIRATION_MS))
                .signWith(getSecretKey())
                .compact();
    }

}
