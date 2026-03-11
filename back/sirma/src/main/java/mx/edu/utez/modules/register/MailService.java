package mx.edu.utez.modules.register;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Servicio especializado de Mail Sender para envío de contraseñas temporales.
 * Lee el template HTML desde resources/templates/welcome-email.html,
 * reemplaza los placeholders de texto y adjunta las imágenes como inline (CID).
 *
 * @author Ithera Team
 */
@Service
public class MailService {

    private final JavaMailSender mailSender;

    /** URL del frontend para el botón "Acceder al Sistema". */
    @Value("${app.login-url:http://localhost:4200/login}")
    private String loginUrl;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envía la contraseña temporal al correo del nuevo usuario registrado.
     * Las imágenes se adjuntan como inline (CID) en el mensaje multipart.
     *
     * @param destinatario   correo electrónico del usuario
     * @param nombreCompleto nombre del usuario para personalizar el mensaje
     * @param password contraseña temporal generada para el usuario
     * @throws MessagingException si falla el envío del correo
     * @throws IOException        si falla la lectura del template o imágenes
     */
    public void enviarCredenciales(String destinatario, String nombreCompleto,
                                   String password)
            throws MessagingException, IOException {

        // ── 1. Leer el template y reemplazar placeholders de texto ─────────
        ClassPathResource tpl = new ClassPathResource("templates/welcome-email.html");
        String html = StreamUtils.copyToString(tpl.getInputStream(), StandardCharsets.UTF_8)
                .replace("{{nombre}}",   nombreCompleto)
                .replace("{{password}}", password)
                .replace("{{loginUrl}}", loginUrl);

        // ── 2. Construir el mensaje multipart con imágenes inline (CID) ────
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(destinatario);
        helper.setSubject("SIRMA — Credenciales de acceso");
        helper.setText(html, true);

        // Adjuntar imágenes inline referenciadas en el HTML como src="cid:xxx"
        helper.addInline("logo",       new ClassPathResource("static/email/activos360_logo.png"));
        helper.addInline("onda",       new ClassPathResource("static/email/onda.png"));
        helper.addInline("caja",       new ClassPathResource("static/email/caja.png"));
        helper.addInline("credencial", new ClassPathResource("static/email/credencial.png"));

        // ── 3. Enviar ──────────────────────────────────────────────────────
        mailSender.send(message);
    }

    /**
     * Envía el correo con enlace para restablecer contraseña.
     *
     * @param destinatario correo del usuario
     * @param nombreCompleto nombre para personalizar
     * @param resetLink URL completa con token (ej: http://localhost:5173/reset-password?token=xxx)
     */
    public void enviarLinkRestablecimiento(String destinatario, String nombreCompleto, String resetLink)
            throws MessagingException, IOException {

        ClassPathResource tpl = new ClassPathResource("templates/reset-password-email.html");
        String html = StreamUtils.copyToString(tpl.getInputStream(), StandardCharsets.UTF_8)
                .replace("{{nombre}}", nombreCompleto)
                .replace("{{resetLink}}", resetLink);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(destinatario);
        helper.setSubject("SIRMA — Restablecer contraseña");
        helper.setText(html, true);

        helper.addInline("logo", new ClassPathResource("static/email/activos360_logo.png"));
        helper.addInline("onda", new ClassPathResource("static/email/onda.png"));

        mailSender.send(message);
    }

}
