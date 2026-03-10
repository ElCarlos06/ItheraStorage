package mx.edu.utez.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Clase de configuración de seguridad para la aplicación.
 * Configura las políticas de seguridad, incluyendo CORS y CSRF.
 * Permite solicitudes desde cualquier origen y deshabilita la protección CSRF para facilitar el desarrollo.
 * @author Ithera Team
*/

@Configuration
@EnableWebSecurity
public class MainSecurity {

    /**
     * Configura la cadena de filtros de seguridad para la aplicación.
     * Deshabilita CSRF, configura CORS y define rutas públicas/privadas.
     * @param http Objeto HttpSecurity para configurar las políticas de seguridad
     * @return SecurityFilterChain configurada para la aplicación
     * @throws Exception Si ocurre un error durante la configuración de seguridad
     */
    @Bean
    public SecurityFilterChain filterInternal(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .cors(c -> c.configurationSource(corsRegistry()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/register").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    /**
     * Configura las políticas de CORS para la aplicación.
     * Permite solicitudes desde cualquier origen, con cualquier encabezado y métodos GET, POST, PUT, DELETE y OPTIONS.
     * No permite el envío de credenciales en las solicitudes CORS.
     * @return CorsConfigurationSource configurada para la aplicación
     */
    private CorsConfigurationSource corsRegistry() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET",  "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowCredentials(false); // si está en 1 allowedOrigins no puede ser asterisco, ya que debe de especificar desde que ip's se puede entrar

        // Configura la fuente de configuración de CORS para aplicar las políticas a todas las rutas
        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", config);

        return src;
    }

    /**
     * Proveedor de codificador de contraseñas usando BCrypt.
     * @return instancia de PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Expone el AuthenticationManager configurado por Spring Security.
     * @param authenticationConfiguration configuración auto-provista
     * @return AuthenticationManager para inyectar en servicios
     * @throws Exception si falla la carga de la configuración
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
