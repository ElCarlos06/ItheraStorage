import Modal from "../../../../components/Modal/Modal";
import StatusBadge from "../../../../components/StatusBadge/StatusBadge";
import "./ReporteInfoModal.css";

function prioridadToBadgeStatus(prioridad) {
  const p = (prioridad ?? "").toString().toLowerCase();
  if (p.includes("alta")) return "disponible";
  if (p.includes("media")) return "en-proceso";
  if (p.includes("baja")) return "neutral";
  return "neutral";
}

export default function ReporteInfoModal({ open, onClose, reporte }) {
  if (!reporte) return null;

  const fecha = reporte.fechaReporte
    ? new Date(reporte.fechaReporte).toLocaleDateString("es-MX", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
      })
    : "—";

  return (
    <Modal open={open} onClose={onClose} className="reporte-info-modal">
      <div className="reporte-info-modal__inner">
        <header className="reporte-info-modal__header">
          <div>
            <h2 className="reporte-info-modal__title">Detalles del reporte</h2>
            {(reporte.codigo || reporte.estatus) && (
              <p className="reporte-info-modal__subtitle">
                {[reporte.codigo, reporte.estatus].filter(Boolean).join(" · ")}
              </p>
            )}
          </div>
          <button
            type="button"
            className="reporte-info-modal__close"
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

        <div className="reporte-info-modal__body">
          <div className="reporte-info-modal__row">
            <span className="reporte-info-modal__label">Activo afectado</span>
            <span className="reporte-info-modal__value">
              {reporte.activoNombre ?? "—"}
            </span>
          </div>
          <div className="reporte-info-modal__row">
            <span className="reporte-info-modal__label">Reportado por</span>
            <span className="reporte-info-modal__value">
              {reporte.nombreUsuario ?? "—"}
            </span>
          </div>
          <div className="reporte-info-modal__row">
            <span className="reporte-info-modal__label">Técnico asignado</span>
            <span className="reporte-info-modal__value">
              {reporte.tecnicoAsignado ??
                reporte.nombreTecnicoAsignado ??
                "—"}
            </span>
          </div>
          <div className="reporte-info-modal__row">
            <span className="reporte-info-modal__label">Fecha de reporte</span>
            <span className="reporte-info-modal__value">{fecha}</span>
          </div>
          <div className="reporte-info-modal__row">
            <span className="reporte-info-modal__label">Tipo de daño</span>
            <span className="reporte-info-modal__value">
              {reporte.tipoFalla ?? "—"}
            </span>
          </div>
          <div className="reporte-info-modal__row reporte-info-modal__row--badge">
            <span className="reporte-info-modal__label">Prioridad</span>
            <span className="reporte-info-modal__value reporte-info-modal__value--inline">
              <StatusBadge
                status={prioridadToBadgeStatus(reporte.prioridad)}
                size="small"
              >
                {reporte.prioridad ?? "—"}
              </StatusBadge>
            </span>
          </div>
          <div className="reporte-info-modal__row reporte-info-modal__row--desc">
            <span className="reporte-info-modal__label">
              Descripción del problema
            </span>
            <div className="reporte-info-modal__desc">
              {reporte.descripcion ?? "—"}
            </div>
          </div>
        </div>
      </div>
    </Modal>
  );
}
