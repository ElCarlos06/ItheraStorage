package mx.edu.utez.kernel.sse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Endpoint SSE para notificaciones en tiempo real.
 * Los clientes web se suscriben aquí y reciben eventos cuando
 * cambia el estado de activos, reportes o mantenimientos.
 *
 * Acceso: público (el evento solo indica "algo cambió", no expone datos sensibles).
 * Los clientes con token válido vuelven a llamar al API para obtener los datos.
 */
@RestController
@RequestMapping("/api/eventos")
@RequiredArgsConstructor
public class SseController {

    private final SseEmitterService sseService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter suscribir() {
        return sseService.suscribir();
    }
}
