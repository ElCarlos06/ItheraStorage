package mx.edu.utez.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Servicio para cargar y procesar plantillas de correo HTML.
 * Las plantillas usan placeholders: {{nombre}}, {{password}}, {{loginUrl}}, {{logoUrl}}, {{waveUrl}}
 */
@Component
public class EmailTemplateService {

    private static final String TEMPLATE_PATH = "templates/welcome-email.html";

    /**
     * Carga la plantilla de bienvenida y reemplaza los placeholders.
     *
     * @param nombre    Nombre del usuario (ej: "Juan" o "Juan Pérez")
     * @param password  Contraseña temporal asignada
     * @param loginUrl  URL para iniciar sesión (ej: https://app.com/login)
     * @param logoUrl   URL absoluta del logo (ej: https://app.com/email/activos360_logo.svg)
     * @param waveUrl   URL absoluta de la onda del header (ej: https://app.com/email/onda.svg)
     * @param baseUrl   URL base para iconos (ej: https://app.com)
     * @return HTML del correo listo para enviar
     */
    public String getWelcomeEmailHtml(String nombre, String password, String loginUrl, String logoUrl, String waveUrl, String baseUrl) {
        try {
            var resource = new ClassPathResource(TEMPLATE_PATH);
            String html = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            return html
                    .replace("{{nombre}}", escapeHtml(nombre))
                    .replace("{{password}}", escapeHtml(password))
                    .replace("{{loginUrl}}", loginUrl)
                    .replace("{{logoUrl}}", logoUrl)
                    .replace("{{waveUrl}}", waveUrl)
                    .replace("{{baseUrl}}", baseUrl);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar la plantilla de correo: " + TEMPLATE_PATH, e);
        }
    }

    private String escapeHtml(String value) {
        if (value == null) return "";
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
