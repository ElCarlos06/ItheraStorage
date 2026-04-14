package mx.edu.utez.kernel.sse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Servicio SSE: mantiene una lista de clientes suscritos y les envía
 * notificaciones cuando cambia el estado de activos, reportes o mantenimientos.
 */
@Service
public class SseEmitterService {

    private static final Logger log = LoggerFactory.getLogger(SseEmitterService.class);
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter suscribir() {
        // Long.MAX_VALUE = sin timeout (evita que Tomcat cierre la conexión a los 30 s)
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> {
            emitter.complete();
            emitters.remove(emitter);
        });
        emitter.onError(e -> emitters.remove(emitter));

        try {
            // Evento inicial de confirmación de conexión
            emitter.send(SseEmitter.event().name("conectado").data("ok"));
        } catch (IOException e) {
            emitters.remove(emitter);
        }
        return emitter;
    }

    /**
     * Envía un evento a todos los clientes conectados.
     * @param evento Nombre del evento SSE (ej. "inventario")
     */
    public void notificar(String evento) {
        if (emitters.isEmpty()) return;
        List<SseEmitter> muertos = new ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name(evento)
                        .data(System.currentTimeMillis()));
            } catch (Exception e) {
                muertos.add(emitter);
            }
        }
        emitters.removeAll(muertos);
        log.debug("SSE '{}' enviado a {} cliente(s)", evento, emitters.size() - muertos.size());
    }

    /**
     * Heartbeat cada 20 segundos para mantener la conexión viva
     * a través de proxies y firewalls que cierran conexiones inactivas.
     */
    @Scheduled(fixedDelay = 20_000)
    public void heartbeat() {
        if (emitters.isEmpty()) return;
        List<SseEmitter> muertos = new ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().comment("heartbeat"));
            } catch (Exception e) {
                muertos.add(emitter);
            }
        }
        emitters.removeAll(muertos);
    }
}
