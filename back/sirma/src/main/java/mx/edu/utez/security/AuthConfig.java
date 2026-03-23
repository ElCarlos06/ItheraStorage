package mx.edu.utez.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuración de beans relacionados con la autenticación.
 * Separado de MainSecurity para evitar dependencias circulares y mejorar la organización.
 * @author Ithera Team
 */
@Configuration
@RequiredArgsConstructor
public class AuthConfig {

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

