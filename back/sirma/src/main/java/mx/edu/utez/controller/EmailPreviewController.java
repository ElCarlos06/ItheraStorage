package mx.edu.utez.controller;

import mx.edu.utez.util.EmailTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Controlador para previsualizar las plantillas de correo en desarrollo.
 * Ejemplo: GET http://localhost:8080/preview/email
 */
@RestController
@RequestMapping("/preview")
public class EmailPreviewController {

    @Autowired
    private EmailTemplateService emailTemplateService;

    @GetMapping(value = "/email", produces = "text/html;charset=UTF-8")
    public String previewEmail(HttpServletRequest request) {
        String baseUrl = request.getScheme() + "://" + request.getServerName()
                + ":" + request.getServerPort();
        String logoUrl = baseUrl + "/email/activos360_logo.svg?v=" + System.currentTimeMillis();
        String waveUrl = baseUrl + "/email/onda.svg?v=" + System.currentTimeMillis();
        String loginUrl = baseUrl.replace("8080", "5173") + "/login"; // Front suele correr en 5173

        return emailTemplateService.getWelcomeEmailHtml(
                "Juan Pérez",
                "passwordEjemplo123",
                loginUrl,
                logoUrl,
                waveUrl,
                baseUrl
        );
    }
}
