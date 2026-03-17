package mx.edu.utez.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import jakarta.servlet.Filter;

@Configuration
public class WebConfig {

    /**
     * Filtro para gestionar ETags.
     * Genera un hash del cuerpo de la respuesta y lo envía como cabecera ETag.
     * Si el cliente (navegador/frontend) envía ese mismo ETag en la siguiente petición (If-None-Match),
     * el servidor responderá con 304 Not Modified sin enviar el cuerpo de nuevo.
     * Esto ahorra ancho de banda y mejora la percepción de velocidad.
     */
    @Bean
    public Filter shallowEtagHeaderFilter() {
        return new ShallowEtagHeaderFilter();
    }
}

