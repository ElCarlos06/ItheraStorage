import { useEffect, useState } from "react";
import Modal from "../../../../components/Modal/Modal";
import { solicitudesApi } from "../../../../api/solicitudesApi";
import "./MantenimientoInfoModal.css";

function formatDuracion(inicio, fin) {
  if (!inicio || !fin) return "—";
  const a = new Date(inicio);
  const b = new Date(fin);
  const ms = b.getTime() - a.getTime();
  if (Number.isNaN(ms) || ms < 0) return "—";
  const h = Math.round(ms / 3600000);
  return `${h} hora${h === 1 ? "" : "s"}`;
}

function ConclusionBox({ conclusion }) {
  if (!conclusion) return null;
  const lower = conclusion.trim().toLowerCase();
  const variant = lower.includes("reparado") ? "reparado"
    : lower.includes("irreparable") ? "irreparable"
    : "neutral";
  const subTexto = variant === "reparado"
    ? "El activo fue reparado exitosamente y está listo para su uso"
    : variant === "irreparable"
    ? "El activo no fue posible repararlo."
    : "";

  return (
    <div className={`mim__conclusion-card mim__conclusion-card--${variant} d-flex flex-column gap-1`}>
      <p className="mim__conclusion-titulo m-0">{conclusion}</p>
      {subTexto && <p className="mim__conclusion-sub m-0">{subTexto}</p>}
    </div>
  );
}

function EvidenciasGrid({ imagenes }) {
  const [preview, setPreview] = useState(null);
  if (!imagenes || imagenes.length === 0) return null;

  return (
    <>
      <section className="d-flex flex-column gap-2">
        <h3 className="mim__section-title m-0">
          Fotos de evidencia
          <span className="mim__img-count">({imagenes.length})</span>
        </h3>
        <div className="row g-3">
          {imagenes.map((img, i) => {
            const url = img.urlCloudinary ?? img.url ?? img;
            return (
              <div key={img.id ?? i} className="col-4">
                <button
                  type="button"
                  className="mim__img-tile w-100"
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
        <div className="mim__lightbox" role="dialog" aria-modal="true" onClick={() => setPreview(null)}>
          <button type="button" className="mim__lightbox-close" onClick={() => setPreview(null)} aria-label="Cerrar imagen">✕</button>
          <img src={preview} alt="Vista completa" className="mim__lightbox-img" onClick={(e) => e.stopPropagation()} />
        </div>
      )}
    </>
  );
}

export default function MantenimientoInfoModal({ open, onClose, mantenimientoId, onBaja }) {
  const [data, setData] = useState(null);
  const [imagenes, setImagenes] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!open || !mantenimientoId) { setData(null); setImagenes([]); setError(null); return; }
    let cancelled = false;
    setLoading(true);
    setError(null);

    solicitudesApi.mantenimientos.getMantenimientoById(mantenimientoId)
      .then(async (resData) => {
        if (cancelled) return;
        const mtn = resData?.data ?? resData;
        setData(mtn);
        const reporteId = mtn?.reporte?.id ?? mtn?.idReporte ?? null;
        const [resMtnImgs, resRptImgs] = await Promise.all([
          solicitudesApi.mantenimientos.getImagenes(mantenimientoId).catch(() => null),
          reporteId ? solicitudesApi.reportes.getImagenes(reporteId).catch(() => null) : Promise.resolve(null),
        ]);
        if (cancelled) return;
        setImagenes([
          ...(Array.isArray(resRptImgs?.data) ? resRptImgs.data : []),
          ...(Array.isArray(resMtnImgs?.data) ? resMtnImgs.data : []),
        ]);
      })
      .catch((e) => { if (!cancelled) { setError(e.message || "Error al cargar"); setData(null); } })
      .finally(() => { if (!cancelled) setLoading(false); });

    return () => { cancelled = true; };
  }, [open, mantenimientoId]);

  if (!open) return null;

  const m = data;
  const activo = m?.activo ?? {};
  const tecnico = m?.usuarioTecnico ?? {};
  const tipoAsignado = m?.tipoEjecutado ?? m?.tipoAsignado ?? "—";
  const esIrreparable = m?.conclusion?.toLowerCase().includes("irreparable");

  return (
    <Modal open={open} onClose={onClose} className="modal-content--mantenimiento">
      <div className="mim d-flex flex-column">

        {/* Header */}
        <header className="d-flex align-items-center justify-content-between mim__header flex-shrink-0">
          <h2 className="mim__title m-0">Detalles del mantenimiento</h2>
          <button type="button" className="mim__close" onClick={onClose} aria-label="Cerrar">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round">
              <line x1="18" y1="6" x2="6" y2="18" /><line x1="6" y1="6" x2="18" y2="18" />
            </svg>
          </button>
        </header>

        {/* Scrollable body */}
        <div className="mim__body">
          {loading && <p className="p-4 m-0 mim__muted">Cargando…</p>}
          {error && !loading && <p className="p-4 m-0 mim__error">{error}</p>}

          {!loading && !error && m && (
            <>
              {/* Hero — 4 cols */}
              <section className="mim__hero border-bottom">
                <div className="row g-3 row-cols-2 row-cols-md-4">
                  <div className="col d-flex flex-column gap-1">
                    <p className="mim__label m-0">Activo</p>
                    <p className="mim__value mim__value--strong m-0">{activo.nombre ?? activo.numeroSerie ?? "—"}</p>
                    <p className="mim__sub m-0">{activo.codigoActivo ?? activo.codigo ?? "—"}</p>
                  </div>
                  <div className="col d-flex flex-column gap-1">
                    <p className="mim__label m-0">Técnico</p>
                    <p className="mim__value mim__value--strong m-0">{tecnico.nombreCompleto ?? tecnico.nombre ?? "—"}</p>
                  </div>
                  <div className="col d-flex flex-column gap-1">
                    <p className="mim__label m-0">Duración</p>
                    <p className="mim__value mim__value--strong m-0">{formatDuracion(m.fechaInicio, m.fechaFin)}</p>
                  </div>
                  <div className="col d-flex flex-column gap-1">
                    <p className="mim__label m-0">Tipo de mantenimiento</p>
                    <span className={`mim__tipo-badge mim__tipo-badge--${tipoAsignado === "Preventivo" ? "preventivo" : "correctivo"}`}>
                      {tipoAsignado}
                    </span>
                  </div>
                </div>
              </section>

              {/* Sections */}
              <div className="d-flex flex-column gap-4 p-4">
                {[
                  { title: "Diagnóstico técnico", text: m.diagnostico },
                  { title: "Acciones realizadas", text: m.accionesRealizadas },
                  { title: "Piezas utilizadas",   text: m.piezasUtilizadas },
                ].map(({ title, text }) => (
                  <section key={title} className="d-flex flex-column gap-2">
                    <h3 className="mim__section-title m-0">{title}</h3>
                    <div className="mim__box"><p className="mim__text m-0">{text ?? "—"}</p></div>
                  </section>
                ))}

                <EvidenciasGrid imagenes={imagenes} />

                <section className="d-flex flex-column gap-2">
                  <h3 className="mim__section-title m-0">Conclusión</h3>
                  <ConclusionBox conclusion={m.conclusion} />
                </section>

                <section className="d-flex flex-column gap-2">
                  <h3 className="mim__section-title m-0">Observaciones</h3>
                  <div className="mim__box"><p className="mim__text m-0">{m.observaciones ?? "—"}</p></div>
                </section>
              </div>
            </>
          )}
        </div>

        {/* Footer */}
        <footer className="d-flex align-items-center justify-content-end mim__footer border-top flex-shrink-0">
          {esIrreparable && (
            <button type="button" className="mim__baja-btn" onClick={() => onBaja?.(m)}>
              Dar de baja
            </button>
          )}
        </footer>
      </div>
    </Modal>
  );
}
