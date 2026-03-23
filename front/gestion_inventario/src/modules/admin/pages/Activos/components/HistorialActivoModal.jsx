import { useEffect, useState } from "react";
import Modal from "../../../../../components/Modal/Modal";
import { bitacoraApi } from "../../../../../api/bitacoraApi";
import { TimeTime } from "@heathmont/moon-icons";
import Icon from "../../../../../components/Icon/Icon";
import "./HistorialActivoModal.css";

const TIPO_EVENTO_LABEL = {
  "Registro Activo": "Registro de activo",
  "Asignacion Resguardo": "Asignación de resguardo",
  "Confirmacion Resguardo": "Confirmación de resguardo",
  "Devolucion Resguardo": "Devolución de resguardo",
  "Reporte Daño": "Reporte de daño",
  "Asignacion Mantenimiento": "Asignación de mantenimiento",
  "Cierre Mantenimiento": "Mantenimiento completado",
  "Solicitud Baja": "Solicitud de baja",
  "Baja Aprobada": "Baja aprobada",
  "Cambio Estado": "Cambio de estado",
  "Actualizacion Activo": "Actualización de activo",
};

// Colores alineados con StatusBadge (estados del sistema)
// Asignación = En Proceso (pendiente de aceptación), Confirmación = Resguardado, Devolución = Disponible
function getCategoriaYColor(tipoEvento) {
  const t = (tipoEvento || "").toLowerCase();
  if (t.includes("baja")) return { categoria: "Baja", variant: "baja" };
  if (t.includes("reporte")) return { categoria: "Reportado", variant: "reportado" };
  if (t.includes("mantenimiento")) return { categoria: "Mantenimiento", variant: "mantenimiento" };
  if (t.includes("asignacion") && t.includes("resguardo")) return { categoria: "En Proceso", variant: "en-proceso" };
  if (t.includes("confirmacion") && t.includes("resguardo")) return { categoria: "Resguardado", variant: "resguardado" };
  if (t.includes("devolucion")) return { categoria: "Disponible", variant: "disponible" };
  if (t.includes("resguardo")) return { categoria: "Resguardado", variant: "resguardado" };
  return { categoria: "Disponible", variant: "disponible" };
}

export default function HistorialActivoModal({ open, onClose, asset }) {
  const [eventos, setEventos] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!open || !asset?.id) {
      setEventos([]);
      setError(null);
      return;
    }
    setLoading(true);
    setError(null);
    bitacoraApi
      .findByActivo(asset.id)
      .then((res) => {
        const data = res?.data;
        const list = Array.isArray(data) ? data : [];
        setEventos(list.sort((a, b) => new Date(b.fechaEvento || 0) - new Date(a.fechaEvento || 0)));
      })
      .catch((err) => {
        setEventos([]);
        setError(err.message ?? "Error al cargar historial");
      })
      .finally(() => setLoading(false));
  }, [open, asset?.id]);

  const nombreActivo = asset?.nombre ?? asset?.codigo ?? asset?.etiqueta ?? "Activo";
  const ultimoEvento = eventos[0];
  const ultimaFecha = ultimoEvento?.fechaEvento
    ? new Date(ultimoEvento.fechaEvento).toLocaleString("es-MX", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
      })
    : "—";

  return (
    <Modal open={open} onClose={onClose} className="historial-modal">
      <div className="historial-modal__inner">
        <header className="historial-modal__header">
          <div className="historial-modal__header-left">
            <div className="historial-modal__icon-wrap">
              <Icon icon={TimeTime} size={24} />
            </div>
            <div className="historial-modal__header-text">
              <h2 className="historial-modal__title">Historial del Activo</h2>
              <p className="historial-modal__subtitle">{nombreActivo}</p>
            </div>
          </div>
          <button type="button" className="historial-modal__close" onClick={onClose} aria-label="Cerrar">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round">
              <path d="M18 6L6 18M6 6l12 12" />
            </svg>
          </button>
        </header>

        <div className="historial-modal__body">
          {loading ? (
            <div className="historial-modal__loading">Cargando historial…</div>
          ) : error ? (
            <div className="historial-modal__error">{error}</div>
          ) : eventos.length === 0 ? (
            <div className="historial-modal__empty">No hay eventos registrados</div>
          ) : (
            <div className="historial-modal__timeline">
              {eventos.map((ev, i) => {
                const { categoria, variant } = getCategoriaYColor(ev.tipoEvento);
                const label = TIPO_EVENTO_LABEL[ev.tipoEvento] ?? ev.tipoEvento ?? "Evento";
                const fecha = ev.fechaEvento
                  ? new Date(ev.fechaEvento).toLocaleString("es-MX", {
                      year: "numeric",
                      month: "2-digit",
                      day: "2-digit",
                      hour: "2-digit",
                      minute: "2-digit",
                    })
                  : "—";
                const autor = ev.usuario?.nombreCompleto ?? ev.usuario?.nombre ?? ev.usuario?.correo ?? "Sistema";

                return (
                  <div key={ev.id ?? i} className={`historial-modal__event historial-modal__event--${variant}`}>
                    <div className="historial-modal__event-node" />
                    <div className="historial-modal__event-card">
                      <div className="historial-modal__event-icon">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                          <circle cx="12" cy="12" r="3" />
                          <path d="M12 1v4M12 19v4M4.22 4.22l2.83 2.83M16.95 16.95l2.83 2.83M1 12h4M19 12h4M4.22 19.78l2.83-2.83M16.95 7.05l2.83-2.83" />
                        </svg>
                      </div>
                      <div className="historial-modal__event-content">
                        <div className="historial-modal__event-row">
                          <span className="historial-modal__event-title">{label}</span>
                          <span className={`historial-modal__event-badge historial-modal__event-badge--${variant}`}>
                            {categoria}
                          </span>
                        </div>
                        <p className="historial-modal__event-date">{fecha}</p>
                        <p className="historial-modal__event-author">Por: {autor}</p>
                        {ev.descripcion && (
                          <p className="historial-modal__event-desc">{ev.descripcion}</p>
                        )}
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>

        {eventos.length > 0 && (
          <footer className="historial-modal__footer">
            <span className="historial-modal__footer-count">Mostrando {eventos.length} eventos</span>
            <span className="historial-modal__footer-date">Último evento: {ultimaFecha}</span>
          </footer>
        )}
      </div>
    </Modal>
  );
}
