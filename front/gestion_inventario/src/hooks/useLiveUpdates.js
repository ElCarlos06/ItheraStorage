import { useEffect } from "react";
import { useQueryClient } from "@tanstack/react-query";

/**
 * Hook que abre una conexión SSE con /api/eventos y, al recibir el evento
 * "inventario", invalida el caché de React Query para que las listas de
 * activos, solicitudes y resguardos se recarguen automáticamente.
 *
 * EventSource reconecta solo si la conexión se corta (comportamiento nativo).
 * Un heartbeat desde el backend cada 20 s mantiene la conexión viva.
 */
export function useLiveUpdates() {
  const queryClient = useQueryClient();

  useEffect(() => {
    const es = new EventSource("/api/eventos");

    es.addEventListener("inventario", () => {
      queryClient.invalidateQueries({ queryKey: ["activos"] });
      queryClient.invalidateQueries({ queryKey: ["solicitudes"] });
      queryClient.invalidateQueries({ queryKey: ["resguardos"] });
    });

    // onerror: EventSource reintenta automáticamente — no cerramos ni hacemos nada manual
    es.onerror = (e) => {
      // Solo log en desarrollo; la reconexión la maneja el navegador
      if (import.meta.env.DEV) {
        console.warn("[SSE] Conexión interrumpida, reintentando…", e);
      }
    };

    return () => es.close();
  }, [queryClient]);
}
