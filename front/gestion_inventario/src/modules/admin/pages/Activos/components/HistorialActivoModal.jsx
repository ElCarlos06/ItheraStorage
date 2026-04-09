import { useEffect, useState } from "react";
import Modal from "../../../../../components/Modal/Modal";
import { bitacoraApi } from "../../../../../api/bitacoraApi";
import { solicitudesApi } from "../../../../../api/solicitudesApi";
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

function parseUtcDate(dateStr) {
  if (!dateStr) return null;
  const s = String(dateStr);
  if (s.endsWith("Z") || s.includes("+") || /[0-9]-[0-9]{2}:[0-9]{2}$/.test(s)) {
    return new Date(s);
  }
  return new Date(s + "Z");
}

function descripcionSinSufijoAuditoria(text) {
  if (typeof text !== "string" || !text) return text;
  const idx = text.indexOf(" (custodia=");
  if (idx === -1) return text;
  return text.slice(0, idx).trim();
}

/**
 * Parsea el string de checklist que viene en la descripción de "Confirmacion Resguardo".
 * Formato esperado: "...| Checklist: item1=OK; item2=NO; ... | Fotos: N"
 * Devuelve array de { nombre, ok } o null si no hay checklist.
 */
function parseChecklist(desc) {
  if (!desc || typeof desc !== "string") return null;
  const idx = desc.indexOf("Checklist:");
  if (idx === -1) return null;
  const raw = desc.slice(idx + "Checklist:".length).split("|")[0].trim();
  const items = raw.split(";").map((s) => s.trim()).filter(Boolean);
  if (items.length === 0) return null;
  return items.map((item) => {
    const sepIdx = item.lastIndexOf("=");
    if (sepIdx === -1) return { nombre: item, ok: null };
    const nombre = item.slice(0, sepIdx).trim();
    const resultado = item.slice(sepIdx + 1).trim().toUpperCase();
    return { nombre, ok: resultado === "OK" };
  });
}

/** Extrae el número de fotos del string de descripción. */
function parseFotos(desc) {
  if (!desc || typeof desc !== "string") return 0;
  const match = desc.match(/Fotos:\s*(\d+)/i);
  return match ? parseInt(match[1], 10) : 0;
}

/** Extrae solo la parte del nombre del confirmador, antes del checklist. */
function descPrincipal(desc) {
  if (!desc) return desc;
  const pipe = desc.indexOf(" | Checklist:");
  return pipe !== -1 ? desc.slice(0, pipe).trim() : descripcionSinSufijoAuditoria(desc);
}

function EvidenciasMantenimiento({ imagenes, variant }) {
  const [preview, setPreview] = useState(null);
  if (!imagenes || imagenes.length === 0) return null;
  return (
    <div className={`historial-modal__resguardo-detail historial-modal__resguardo-detail--${variant}`}>
      <p className="historial-modal__checklist-title">
        Evidencias fotográficas ({imagenes.length})
      </p>
      <div className="historial-modal__fotos-grid">
        {imagenes.map((img, i) => (
          <button
            key={img.id ?? i}
            type="button"
            className="historial-modal__foto-thumb"
            onClick={() => setPreview(img.urlCloudinary ?? img.url)}
            aria-label={`Ver evidencia ${i + 1}`}
          >
            <img src={img.urlCloudinary ?? img.url} alt={`Evidencia ${i + 1}`} loading="lazy" />
          </button>
        ))}
      </div>
      {preview && (
        <div className="historial-modal__lightbox" onClick={() => setPreview(null)} role="dialog" aria-modal="true">
          <button type="button" className="historial-modal__lightbox-close" onClick={() => setPreview(null)}>✕</button>
          <img src={preview} alt="Evidencia" className="historial-modal__lightbox-img" onClick={(e) => e.stopPropagation()} />
        </div>
      )}
    </div>
  );
}

function ChecklistResguardo({ desc, fotosConf, variant }) {
  const [preview, setPreview] = useState(null);
  const items = parseChecklist(desc);
  const numFotos = parseFotos(desc);
  if (!items && fotosConf.length === 0) return null;

  return (
    <div className={`historial-modal__resguardo-detail historial-modal__resguardo-detail--${variant}`}>
      {items && items.length > 0 && (
        <div className="historial-modal__checklist">
          <p className="historial-modal__checklist-title">Checklist recepción</p>
          {items.map((item, i) => (
            <div key={i} className="historial-modal__checklist-row">
              <span
                className={`historial-modal__checklist-icon historial-modal__checklist-icon--${item.ok ? "ok" : item.ok === false ? "no" : "na"}`}
                aria-label={item.ok ? "OK" : item.ok === false ? "No" : "N/A"}
              >
                {item.ok ? "✓" : item.ok === false ? "✗" : "—"}
              </span>
              <span className="historial-modal__checklist-label">{item.nombre}</span>
            </div>
          ))}
        </div>
      )}

      {fotosConf.length > 0 && (
        <div className="historial-modal__fotos-conf">
          <p className="historial-modal__checklist-title">
            Evidencias fotográficas ({fotosConf.length})
          </p>
          <div className="historial-modal__fotos-grid">
            {fotosConf.map((img, i) => (
              <button
                key={img.id ?? i}
                type="button"
                className="historial-modal__foto-thumb"
                onClick={() => setPreview(img.urlCloudinary ?? img.url)}
                aria-label={`Ver foto ${i + 1}`}
              >
                <img src={img.urlCloudinary ?? img.url} alt={`Evidencia ${i + 1}`} loading="lazy" />
              </button>
            ))}
          </div>
        </div>
      )}

      {numFotos > 0 && fotosConf.length === 0 && (
        <p className="historial-modal__fotos-pendiente">
          {numFotos} foto{numFotos !== 1 ? "s" : ""} registrada{numFotos !== 1 ? "s" : ""} (se muestran en la galería del activo)
        </p>
      )}

      {preview && (
        <div
          className="historial-modal__lightbox"
          onClick={() => setPreview(null)}
          role="dialog"
          aria-modal="true"
        >
          <button
            type="button"
            className="historial-modal__lightbox-close"
            onClick={() => setPreview(null)}
          >✕</button>
          <img
            src={preview}
            alt="Evidencia"
            className="historial-modal__lightbox-img"
            onClick={(e) => e.stopPropagation()}
          />
        </div>
      )}
    </div>
  );
}

export default function HistorialActivoModal({ open, onClose, asset }) {
  const [eventos, setEventos] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [fotosConf, setFotosConf] = useState([]);
  // mapa mantenimientoId → [imagenes] para eventos de mantenimiento
  const [imgsPorMantenimiento, setImgsPorMantenimiento] = useState({});

  useEffect(() => {
    if (!open || !asset?.id) {
      setEventos([]);
      setError(null);
      setFotosConf([]);
      setImgsPorMantenimiento({});
      return;
    }
    setLoading(true);
    setError(null);

    Promise.all([
      bitacoraApi.findByActivo(asset.id),
      bitacoraApi.getImagenesActivo(asset.id).catch(() => null),
      solicitudesApi.mantenimientos.getMantenimientosByActivo(asset.id).catch(() => null),
    ])
      .then(async ([resBitacora, resImagenes, resMantenimientos]) => {
        const data = resBitacora?.data;
        const list = Array.isArray(data) ? data : [];
        setEventos(list.sort((a, b) => (parseUtcDate(b.fechaEvento) ?? 0) - (parseUtcDate(a.fechaEvento) ?? 0)));

        const imgs = Array.isArray(resImagenes?.data) ? resImagenes.data : [];
        setFotosConf(imgs.filter((img) =>
          (img.nombreArchivo ?? "").toUpperCase().startsWith("RESGUARDO_CONF")
        ));

        // Cargar imágenes de cada mantenimiento
        const mantenimientos = Array.isArray(resMantenimientos?.data) ? resMantenimientos.data : [];
        if (mantenimientos.length > 0) {
          const entradas = await Promise.all(
            mantenimientos.map(async (m) => {
              const mId = m.id ?? m.idMantenimiento;
              if (!mId) return null;
              try {
                const [resMtn, resRpt] = await Promise.all([
                  solicitudesApi.mantenimientos.getImagenes(mId).catch(() => null),
                  m.reporte?.id
                    ? solicitudesApi.reportes.getImagenes(m.reporte.id).catch(() => null)
                    : Promise.resolve(null),
                ]);
                const mtnImgs = Array.isArray(resMtn?.data) ? resMtn.data : [];
                const rptImgs = Array.isArray(resRpt?.data) ? resRpt.data : [];
                return [mId, [...rptImgs, ...mtnImgs]];
              } catch { return null; }
            })
          );
          setImgsPorMantenimiento(
            Object.fromEntries(entradas.filter(Boolean))
          );
        }
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
    ? parseUtcDate(ultimoEvento.fechaEvento).toLocaleString("es-MX", {
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
                  ? parseUtcDate(ev.fechaEvento).toLocaleString("es-MX", {
                      year: "numeric",
                      month: "2-digit",
                      day: "2-digit",
                      hour: "2-digit",
                      minute: "2-digit",
                    })
                  : "—";
                const autor = ev.usuario?.nombreCompleto ?? ev.usuario?.nombre ?? ev.usuario?.correo ?? "Sistema";
                const esConfirmacion = ev.tipoEvento === "Confirmacion Resguardo";
                const esCierreMantenimiento = ev.tipoEvento === "Cierre Mantenimiento";
                // Tomar las imágenes del mantenimiento más reciente cuya fecha sea ≤ este evento
                const imagenesMantenimiento = esCierreMantenimiento
                  ? Object.values(imgsPorMantenimiento).find((imgs) => imgs.length > 0) ?? []
                  : [];

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
                          <p className="historial-modal__event-desc">
                            {esConfirmacion
                              ? descPrincipal(ev.descripcion)
                              : descripcionSinSufijoAuditoria(ev.descripcion)}
                          </p>
                        )}
                        {esConfirmacion && (
                          <ChecklistResguardo
                            desc={ev.descripcion}
                            fotosConf={fotosConf}
                            variant={variant}
                          />
                        )}
                        {esCierreMantenimiento && (
                          <EvidenciasMantenimiento
                            imagenes={imagenesMantenimiento}
                            variant={variant}
                          />
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
