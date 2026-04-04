package mx.edu.utez.security;

import lombok.RequiredArgsConstructor;
import mx.edu.utez.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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
@EnableMethodSecurity
@RequiredArgsConstructor
public class MainSecurity {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configura la cadena de filtros de seguridad para la aplicación.
     * Deshabilita CSRF, configura CORS y define rutas públicas/privadas.
     * @param http Objeto HttpSecurity para configurar las políticas de seguridad
     * @return SecurityFilterChain configurada para la aplicación
     */
    @Bean
    public SecurityFilterChain filterInternal(HttpSecurity http) {

        http.csrf(AbstractHttpConfigurer::disable)
                .cors(c -> c.configurationSource(corsRegistry()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/auth/**").permitAll() // Permite el login y la doc
                        .requestMatchers("/api/prioridades", "/api/prioridades/**").permitAll()
                        .requestMatchers("/", "/error", "/email/**", "/preview/**").permitAll()
                        .requestMatchers("/api/auth/login", "/api/auth/request-password-reset", "/api/auth/change-password").permitAll()
                        .requestMatchers("/api/register", "/api/register/**").hasAnyAuthority("ROLE_Administrador") // Solo el admin puede crear usuarios
                        .requestMatchers("/api/roles/**", "/api/areas/**", "/api/users/**", "/api/imports/").hasAnyAuthority("ROLE_Administrador")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/activos/**").hasAnyAuthority("ROLE_Administrador", "ROLE_Tecnico", "ROLE_Empleado")
                        .requestMatchers("/api/qr/**", "/api/campus/**", "/api/edificios/**", "/api/espacios/**", "/api/tipo-activos/**", "/api/marcas/**", "/api/modelos/**", "/api/activos/**")
                        .hasAnyAuthority("ROLE_Administrador", "ROLE_Tecnico")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

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
        config.setAllowedMethods(List.of("GET",  "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowCredentials(false); // si está en 1 allowedOrigins no puede ser asterisco, ya que debe de especificar desde que ip's se puede entrar

        // Configura la fuente de configuración de CORS para aplicar las políticas a todas las rutas
        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", config);

        return src;
    }
}
