import { useEffect, useState } from "react";
import Modal from "../../../../components/Modal/Modal";
import StatusBadge from "../../../../components/StatusBadge/StatusBadge";
import { solicitudesApi } from "../../../../api/solicitudesApi";
import "./MantenimientoInfoModal.css";

function formatDuracion(inicio, fin) {
  if (!inicio || !fin) return "—";
  const a = new Date(inicio);
  const b = new Date(fin);
  const ms = b.getTime() - a.getTime();
  if (Number.isNaN(ms) || ms < 0) return "—";
  const h = Math.round(ms / 3600000);
  return `${h} horas`;
}

function conclusionToStatus(conclusion) {
  if (!conclusion || typeof conclusion !== "string") return "neutral";
  const c = conclusion.trim().toLowerCase();
  if (c.includes("reparado")) return "disponible";
  if (c.includes("irreparable")) return "reportado";
  return "neutral";
}

export default function MantenimientoInfoModal({ open, onClose, mantenimientoId }) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!open || !mantenimientoId) {
      setData(null);
      setError(null);
      return;
    }
    let cancelled = false;
    setLoading(true);
    setError(null);
    solicitudesApi.mantenimientos
      .getMantenimientoById(mantenimientoId)
      .then((res) => {
        if (cancelled) return;
        const payload = res?.data ?? res;
        setData(payload);
      })
      .catch((e) => {
        if (cancelled) return;
        setError(e.message || "Error al cargar");
        setData(null);
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [open, mantenimientoId]);

  if (!open) return null;

  const m = data;
  const activo = m?.activo ?? {};
  const tecnico = m?.usuarioTecnico ?? {};
  const tipoAsignado = m?.tipoEjecutado ?? m?.tipoAsignado ?? "—";

  return (
    <Modal open={open} onClose={onClose} className="modal-content--mantenimiento">
      <div className="mim">
        <header className="mim__header">
          <div className="mim__header-text">
            <h2 className="mim__title">Detalles del mantenimiento</h2>
          </div>
          <button
            type="button"
            className="mim__close"
            onClick={onClose}
            aria-label="Cerrar"
          >
            <svg
              width="20"
              height="20"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2.5"
              strokeLinecap="round"
            >
              <line x1="18" y1="6" x2="6" y2="18" />
              <line x1="6" y1="6" x2="18" y2="18" />
            </svg>
          </button>
        </header>

        <div className="mim__body">
          {loading && (
            <p className="mim__muted">Cargando…</p>
          )}
          {error && !loading && (
            <p className="mim__error">{error}</p>
          )}
          {!loading && !error && m && (
            <>
              <section className="mim__hero">
                <div className="mim__hero-grid">
                  <div className="mim__cell">
                    <p className="mim__label">Activo</p>
                    <p className="mim__value mim__value--strong">
                      {activo.nombre ?? activo.numeroSerie ?? "—"}
                    </p>
                    <p className="mim__sub">
                      {activo.codigoActivo ?? activo.codigo ?? "—"}
                    </p>
                  </div>
                  <div className="mim__cell">
                    <p className="mim__label">Técnico</p>
                    <p className="mim__value mim__value--strong">
                      {tecnico.nombreCompleto ?? tecnico.nombre ?? "—"}
                    </p>
                  </div>
                  <div className="mim__cell">
                    <p className="mim__label">Duración</p>
                    <p className="mim__value mim__value--strong">
                      {formatDuracion(m.fechaInicio, m.fechaFin)}
                    </p>
                  </div>
                  <div className="mim__cell">
                    <p className="mim__label">Tipo de mantenimiento</p>
                    <span
                      className={`mim__tipo-badge mim__tipo-badge--${tipoAsignado === "Preventivo" ? "preventivo" : "correctivo"}`}
                    >
                      {tipoAsignado}
                    </span>
                  </div>
                </div>
              </section>

              <section className="mim__section">
                <h3 className="mim__section-title">Diagnóstico técnico</h3>
                <div className="mim__box">
                  <p className="mim__text">{m.diagnostico ?? "—"}</p>
                </div>
              </section>

              <section className="mim__section">
                <h3 className="mim__section-title">Acciones realizadas</h3>
                <div className="mim__box">
                  <p className="mim__text">{m.accionesRealizadas ?? "—"}</p>
                </div>
              </section>

              <section className="mim__section">
                <h3 className="mim__section-title">Piezas utilizadas</h3>
                <div className="mim__box">
                  <p className="mim__text">{m.piezasUtilizadas ?? "—"}</p>
                </div>
              </section>

              <section className="mim__section">
                <h3 className="mim__section-title">Conclusión</h3>
                <div className="mim__conclusion-box">
                  <StatusBadge
                    status={conclusionToStatus(m.conclusion)}
                    size="small"
                  >
                    {m.conclusion ?? "—"}
                  </StatusBadge>
                  {m.conclusion &&
                    m.conclusion.toLowerCase().includes("reparado") && (
                    <p className="mim__conclusion-note">
                      El activo fue reparado exitosamente y está listo para su uso
                    </p>
                  )}
                </div>
              </section>

              <section className="mim__section">
                <h3 className="mim__section-title">Observaciones</h3>
                <div className="mim__box">
                  <p className="mim__text">{m.observaciones ?? "—"}</p>
                </div>
              </section>
            </>
          )}
        </div>

        <footer className="mim__footer" />
      </div>
    </Modal>
  );
}
