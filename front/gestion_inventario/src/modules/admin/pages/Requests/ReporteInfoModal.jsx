import Modal from "../../../../components/Modal/Modal";
import "./ReporteInfoModal.css";

const PRIORIDAD_STYLES = {
  Alta:  { background: "#99e6da", color: "#065f55" },
  Media: { background: "#fef08a", color: "#854d0e" },
  Baja:  { background: "#e5e7eb", color: "#374151" },
};

function Field({ label, children, wide }) {
  return (
    <div className={`rim__field${wide ? " rim__field--wide" : ""}`}>
      <p className="rim__field-label">{label}</p>
      <div className="rim__field-value">{children}</div>
    </div>
  );
}

export default function ReporteInfoModal({ open, onClose, reporte }) {
  if (!reporte) return null;

  const prioridadStyle = PRIORIDAD_STYLES[reporte.prioridad] ?? PRIORIDAD_STYLES.Baja;

  const fecha = reporte.fechaReporte
    ? new Date(reporte.fechaReporte).toLocaleDateString("es-MX", {
        year: "numeric", month: "2-digit", day: "2-digit",
      })
    : "—";

  return (
    <Modal open={open} onClose={onClose}>
      <div className="rim__inner">

        {/* Header */}
        <div className="rim__header">
          <div>
            <p className="rim__title">Detalles del reporte</p>
            <p className="rim__subtitle">{reporte.codigo ?? "—"} · {reporte.estatus ?? "Pendiente"}</p>
          </div>
          <button type="button" className="rim__close" onClick={onClose} aria-label="Cerrar">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round">
              <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
        </div>

        {/* Body */}
        <div className="rim__body">

          {/* Fila 1: activo + usuario + fecha */}
          <div className="rim__row">
            <Field label="Activo afectado">
              <p className="rim__text rim__text--bold">{reporte.activoNombre ?? "—"}</p>
              <p className="rim__text rim__text--sub">{reporte.codigo ?? "—"}</p>
            </Field>
            <Field label="Reportado por">
              <p className="rim__text rim__text--bold">{reporte.nombreUsuario ?? "—"}</p>
            </Field>
            <Field label="Fecha de reporte">
              <p className="rim__text">{fecha}</p>
            </Field>
          </div>

          {/* Fila 2: tipo de daño + prioridad */}
          <div className="rim__row">
            <Field label="Tipo de daño">
              <p className="rim__text rim__text--bold">{reporte.tipoFalla ?? "—"}</p>
            </Field>
            <Field label="Prioridad">
              <span className="rim__badge" style={prioridadStyle}>
                {reporte.prioridad ?? "—"}
              </span>
            </Field>
          </div>

          {/* Descripción */}
          <Field label="Descripción del problema" wide>
            <div className="rim__desc-box">
              <p className="rim__text">{reporte.descripcion ?? "—"}</p>
            </div>
          </Field>

        </div>
      </div>
    </Modal>
  );
}