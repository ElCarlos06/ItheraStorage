import { useState, useMemo } from "react";
import PageHeader   from "../../components/dashboard/PageHeader";
import Pagination   from "../../components/layout/Pagination";
import Buscador     from "../../../../components/Buscador/Buscador";
import EmptyState   from "../../../../components/EmptyState/EmptyState";
import LoadingState from "../../../../components/LoadingState/LoadingState";
import ErrorBanner  from "../../../../components/ErrorBanner/ErrorBanner";
import ReporteInfoModal from "./ReporteInfoModal";
import { solicitudesApi } from "../../../../api/solicitudesApi";
import { usePaginatedQuery } from "../../../../hooks/usePaginatedQuery";
import "./Requests.css";

// ─── Normaliza la respuesta del backend ───────────────────────────────────────
function mapSolicitud(s) {
  if (!s || typeof s !== "object") return null;

  // Activo
  const activo = s.activo ?? {};
  const espacio = activo.espacio ?? {};
  const edificio = espacio.edificio ?? {};
  const campus = edificio.campus ?? {};

  // Usuario
  const usuario = s.usuarioReporta ?? s.usuario ?? {};

  // Tipo falla y prioridad (objetos del backend)
  const tipoFalla  = s.tipoFalla?.nombre  ?? s.tipoFalla  ?? "-";
  const prioridad = s.prioridad?.nombre ?? s.prioridad?.nivel ?? "Media";

  return {
    id:            s.id ?? s.id_reporte,
    codigo:        activo.codigoActivo ?? activo.codigo ?? s.codigo ?? "-",
    activoNombre:  activo.nombre ?? "-",
    nombreUsuario: usuario.nombreCompleto ?? usuario.nombre ?? s.nombreUsuario ?? "-",
    tipoFalla,
    descripcion:   s.descripcionFalla ?? s.descripcion ?? "-",
    campus:        campus.nombre ?? s.campus ?? "-",
    edificio:      edificio.nombre ?? s.edificio ?? "-",
    aula:          espacio.nombre ?? s.aula ?? "-",
    prioridad,
    estatus:       s.estadoReporte ?? s.estatus ?? "Pendiente",
    fechaReporte:  s.fechaReporte ?? s.fecha_reporte ?? null,
  };
}

function mapResponse(content) {
  const list = Array.isArray(content) ? content : content?.content ?? content?.data ?? [];
  return list.map(mapSolicitud).filter(Boolean);
}

// ─── Badge de prioridad ───────────────────────────────────────────────────────
const PRIORIDAD_STYLES = {
  Alta:  { background: "var(--color-prioridad-alta,  #99e6da)", color: "#065f55" },
  Media: { background: "var(--color-prioridad-media, #fef08a)", color: "#854d0e" },
  Baja:  { background: "var(--color-prioridad-baja,  #e5e7eb)", color: "#374151" },
};

function PrioridadBadge({ prioridad }) {
  const style = PRIORIDAD_STYLES[prioridad] ?? PRIORIDAD_STYLES.Baja;
  return (
    <span
      style={{
        ...style,
        padding: "2px 12px",
        borderRadius: "999px",
        fontSize: "0.78rem",
        fontWeight: 600,
        display: "inline-block",
      }}
    >
      {prioridad ?? "—"}
    </span>
  );
}

// ─── Tabs ─────────────────────────────────────────────────────────────────────
const TABS = [
  { key: "reportes",       label: "Reportes" },
  { key: "mantenimientos", label: "Mantenimiento" },
];

// ─── Componente principal ─────────────────────────────────────────────────────
export default function Requests() {
  const [activeTab, setActiveTab]     = useState("reportes");
  const [search,    setSearch]        = useState("");
  const [modalReporte, setModalReporte] = useState(null);
  const pageSize = 10;

  const {
    isLoading: loading,
    error,
    invalidate,
    currentPage,
    setCurrentPage,
    content,
    totalPages,
    totalElements,
  } = usePaginatedQuery({
    queryKey: ["solicitudes", activeTab],
    queryFn: (page, size) =>
      activeTab === "reportes"
        ? solicitudesApi.getReportes(page, size)
        : solicitudesApi.getMantenimientos(page, size),
    errorMessage: "Error al cargar solicitudes",
    pageSize,
  });

  const solicitudes = useMemo(() => mapResponse(content), [content]);

  const handleTabChange = (tab) => {
    setActiveTab(tab);
    setCurrentPage(0);
    setSearch("");
  };

  // Filtrado local por búsqueda
  const filtered = useMemo(() => {
    const q = search.trim().toLowerCase();
    if (!q) return solicitudes;
    return solicitudes.filter(
      (s) =>
        (s.codigo        ?? "").toLowerCase().includes(q) ||
        (s.nombreUsuario ?? "").toLowerCase().includes(q) ||
        (s.tipoFalla     ?? "").toLowerCase().includes(q) ||
        (s.descripcion   ?? "").toLowerCase().includes(q),
    );
  }, [solicitudes, search]);

  const paginatedItems = useMemo(() => {
    const start = currentPage * pageSize;
    return filtered.slice(start, start + pageSize);
  }, [filtered, currentPage]);

  const showEmpty = !loading && filtered.length === 0;

  return (
    <div className="requests-page">
      <PageHeader
        overline="SOLICITUDES"
        title="Solicitudes de los usuarios"
        subtitle="Administra los reportes y los mantenimientos realizados"
      />

      <section className="requests-view">
        {/* ── Tabs + Buscador ── */}
        <div className="requests-view__controls">
          <div className="requests-view__buscador">
            <Buscador
              placeholder="Buscar tipo de activo por nombre..."
              value={search}
              onChange={(e) => {
                setSearch(e.target.value);
                setCurrentPage(0);
              }}
            />
          </div>

          <div className="requests-view__tabs">
            {TABS.map((tab) => (
              <button
                key={tab.key}
                type="button"
                className={`requests-view__tab ${activeTab === tab.key ? "requests-view__tab--active" : ""}`}
                onClick={() => handleTabChange(tab.key)}
              >
                {tab.label}
              </button>
            ))}
          </div>
        </div>

        {error && (
          <ErrorBanner message={error} onDismiss={() => invalidate()} />
        )}

        {/* ── Lista ── */}
        {loading ? (
          <LoadingState message="Cargando solicitudes…" />
        ) : showEmpty ? (
          <EmptyState
            message="No hay solicitudes para mostrar"
            hasSearch={!!search.trim()}
            searchMessage="No coinciden con la búsqueda."
          />
        ) : (
          <div className="requests-view__list">
            {paginatedItems.map((sol, idx) => (
              <div
                key={sol.id ?? `sol-${idx}`}
                className="requests-view__card-wrap"
                onClick={() => setModalReporte(sol)}
              >
                <div className="requests-view__card">
                  {/* Fila 1: código + tipo de falla + descripción */}
                  <div className="requests-view__card-top">
                    <div className="requests-view__card-id">
                      <p className="requests-view__codigo">{sol.codigo}</p>
                      <p className="requests-view__usuario">{sol.nombreUsuario}</p>
                    </div>

                    <div className="requests-view__card-col">
                      <p className="requests-view__label">Tipo de falla</p>
                      <p className="requests-view__value requests-view__value--bold">
                        {sol.tipoFalla}
                      </p>
                    </div>

                    <div className="requests-view__card-col requests-view__card-col--desc">
                      <p className="requests-view__label">Descripción</p>
                      <p className="requests-view__value requests-view__value--truncate">
                        {sol.descripcion}
                      </p>
                    </div>

                    {/* Botón detalle — visible en hover */}
                    <button
                      type="button"
                      className="requests-view__detail-btn"
                      title="Ver detalles"
                      onClick={(e) => { e.stopPropagation(); setModalReporte(sol); }}
                    >
                      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                        <rect x="8" y="2" width="8" height="4" rx="1" ry="1"/>
                        <path d="M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2"/>
                        <path d="M12 11h4"/><path d="M12 16h4"/><path d="M8 11h.01"/><path d="M8 16h.01"/>
                      </svg>
                    </button>
                  </div>

                  {/* Fila 2: campus + edificio + aula + prioridad */}
                  <div className="requests-view__card-bottom">
                    <div className="requests-view__card-col">
                      <p className="requests-view__label">Campus</p>
                      <p className="requests-view__value requests-view__value--bold">{sol.campus}</p>
                    </div>
                    <div className="requests-view__card-col">
                      <p className="requests-view__label">Edificio</p>
                      <p className="requests-view__value requests-view__value--bold">{sol.edificio}</p>
                    </div>
                    <div className="requests-view__card-col">
                      <p className="requests-view__label">Aula</p>
                      <p className="requests-view__value requests-view__value--bold">{sol.aula}</p>
                    </div>
                    <div className="requests-view__card-col">
                      <p className="requests-view__label">Prioridad</p>
                      <PrioridadBadge prioridad={sol.prioridad} />
                    </div>
                  </div>
                </div>
              </div>
            ))}

            <Pagination
              currentPage={currentPage}
              totalPages={Math.ceil(filtered.length / pageSize)}
              totalElements={filtered.length}
              pageSize={pageSize}
              onPageChange={setCurrentPage}
            />
          </div>
        )}
      </section>

      <ReporteInfoModal
        open={!!modalReporte}
        onClose={() => setModalReporte(null)}
        reporte={modalReporte}
      />
    </div>
  );
}