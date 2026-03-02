package mx.edu.utez.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
     * Deshabilita CSRF, configura CORS para permitir solicitudes desde cualquier origen y permite todas las solicitudes sin autenticación.
     * @param http Objeto HttpSecurity para configurar las políticas de seguridad
     * @return SecurityFilterChain configurada para la aplicación
     * @throws Exception Si ocurre un error durante la configuración de seguridad
     */
    @Bean
    public SecurityFilterChain filterInternal(HttpSecurity http) throws Exception {

        // Bloquea solicitudes en las que se pueda interceptar la conexión y puedan enviar un suplente
        // de la verdadera solicitud
        http.csrf(AbstractHttpConfigurer::disable).
                cors( c -> c.configurationSource(corsRegistry()) ).
                authorizeHttpRequests(auth -> auth.
                        requestMatchers("/**").permitAll().
                        anyRequest().authenticated()
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

}
