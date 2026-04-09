import { useEffect, useState } from "react";
import Modal from "../../../../components/Modal/Modal";
import { solicitudesApi } from "../../../../api/solicitudesApi";
import "./ReporteInfoModal.css";

const PRIORIDAD_STYLES = {
  alta:  { bg: "rgba(64,166,159,0.13)",   color: "#237e79" },
  media: { bg: "rgba(255,179,25,0.13)",   color: "#b8860b" },
  baja:  { bg: "rgba(100,116,139,0.1)",   color: "#475569" },
};
function getPrioridadStyle(p) {
  const s = (p ?? "").toLowerCase();
  return s.includes("alta") ? PRIORIDAD_STYLES.alta
    : s.includes("baja")   ? PRIORIDAD_STYLES.baja
    : PRIORIDAD_STYLES.media;
}

function EvidenciasReporte({ imagenes }) {
  const [preview, setPreview] = useState(null);
  if (!imagenes || imagenes.length === 0) return null;

  return (
    <>
      <section className="d-flex flex-column gap-2">
        <h3 className="rim__section-title m-0">
          Evidencias <span className="rim__count">({imagenes.length})</span>
        </h3>
        <div className="row g-3">
          {imagenes.map((img, i) => {
            const url = img.urlCloudinary ?? img.url ?? img;
            return (
              <div key={img.id ?? i} className="col-6">
                <button
                  type="button"
                  className="rim__foto-tile w-100"
                  onClick={() => setPreview(url)}
                  aria-label={`Ver imagen ${i + 1}`}
                >
                  <img src={url} alt={`Evidencia ${i + 1}`} loading="lazy" />
                </button>
              </div>
            );
          })}
        </div>
      </section>

      {preview && (
        <div className="rim__lightbox" role="dialog" aria-modal="true" onClick={() => setPreview(null)}>
          <button type="button" className="rim__lightbox-close" onClick={() => setPreview(null)} aria-label="Cerrar">✕</button>
          <img src={preview} alt="Vista completa" className="rim__lightbox-img" onClick={(e) => e.stopPropagation()} />
        </div>
      )}
    </>
  );
}

export default function ReporteInfoModal({ open, onClose, reporte }) {
  const [imagenes, setImagenes] = useState([]);
  const reporteId = reporte?.idReporte ?? reporte?.id;

  useEffect(() => {
    if (!open || !reporteId) { setImagenes([]); return; }
    solicitudesApi.reportes.getImagenes(reporteId)
      .then((res) => setImagenes(Array.isArray(res?.data) ? res.data : []))
      .catch(() => setImagenes([]));
  }, [open, reporteId]);

  if (!reporte) return null;

  const fecha = reporte.fechaReporte
    ? new Date(reporte.fechaReporte).toLocaleDateString("es-MX", { year: "numeric", month: "2-digit", day: "2-digit" })
    : "—";

  const prioridadStyle = getPrioridadStyle(reporte.prioridad);
  const tecnico = reporte.tecnicoAsignado ?? reporte.nombreTecnicoAsignado;

  return (
    <Modal open={open} onClose={onClose} className="rim-modal">
      <div className="rim d-flex flex-column">

        {/* Header */}
        <header className="rim__header d-flex align-items-center justify-content-between border-bottom flex-shrink-0">
          <h2 className="rim__title m-0">Detalles del reporte</h2>
          <button type="button" className="rim__close" onClick={onClose} aria-label="Cerrar">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round">
              <line x1="18" y1="6" x2="6" y2="18" /><line x1="6" y1="6" x2="18" y2="18" />
            </svg>
          </button>
        </header>

        {/* Hero strip */}
        <div className="rim__hero d-flex flex-wrap border-bottom flex-shrink-0">
          {[
            { label: "Activo afectado", value: reporte.activoNombre ?? "—", sub: reporte.codigo },
            { label: "Reportado por",   value: reporte.nombreUsuario  ?? "—" },
            { label: "Fecha de reporte",value: fecha },
            ...(tecnico && tecnico !== "—" ? [{ label: "Técnico asignado", value: tecnico }] : []),
          ].map(({ label, value, sub }) => (
            <div key={label} className="rim__hero-col d-flex flex-column gap-1">
              <p className="rim__hero-label m-0">{label}</p>
              <p className="rim__hero-value m-0">{value}</p>
              {sub && <p className="rim__hero-sub m-0">{sub}</p>}
            </div>
          ))}
        </div>

        {/* Body */}
        <div className="rim__body d-flex flex-column gap-4 p-4">

          {/* Tipo de Daño + Prioridad */}
          <div className="row g-3">
            <div className="col-6">
              <div className="rim__info-card d-flex flex-column gap-2">
                <p className="rim__section-title m-0">Tipo de daño</p>
                <p className="rim__card-value m-0">{reporte.tipoFalla ?? "—"}</p>
              </div>
            </div>
            <div className="col-6">
              <div className="rim__info-card d-flex flex-column gap-2">
                <p className="rim__section-title m-0">Prioridad</p>
                <span className="rim__prioridad-badge" style={{ background: prioridadStyle.bg, color: prioridadStyle.color }}>
                  {reporte.prioridad ?? "—"}
                </span>
              </div>
            </div>
          </div>

          {/* Descripción */}
          <section className="d-flex flex-column gap-2">
            <h3 className="rim__section-title m-0">Descripción del problema</h3>
            <div className="rim__desc-box">
              <p className="rim__desc-text m-0">{reporte.descripcion ?? "—"}</p>
            </div>
          </section>

          {open && <EvidenciasReporte imagenes={imagenes} />}
        </div>
      </div>
    </Modal>
  );
}
