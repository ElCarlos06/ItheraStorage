import { useState, useMemo } from "react";
import { useQueryClient } from "@tanstack/react-query";
import PageHeader from "../../components/dashboard/PageHeader";
import Pagination from "../../components/layout/Pagination";
import Buscador from "../../../../components/Buscador/Buscador";
import EmptyState from "../../../../components/EmptyState/EmptyState";
import LoadingState from "../../../../components/LoadingState/LoadingState";
import ErrorBanner from "../../../../components/ErrorBanner/ErrorBanner";
import ConfirmDeleteModal from "../../../../components/ConfirmDeleteModal/ConfirmDeleteModal";
import ReporteInfoModal from "./ReporteInfoModal";
import MantenimientoInfoModal from "./MantenimientoInfoModal";
import AssignTechnicianModal from "./AssignTechnicianModal";
import StatusBadge from "../../../../components/StatusBadge/StatusBadge";
import Icon from "../../../../components/Icon/Icon";
import { GenericUser, GenericDelete } from "@heathmont/moon-icons";
import { solicitudesApi } from "../../../../api/solicitudesApi";
import { usePaginatedQuery } from "../../../../hooks/usePaginatedQuery";
import { toast } from "../../../../utils/toast.jsx";
import "./Requests.css";

// Activo sin campo nombre en API: armamos etiqueta con tipo + serie / etiqueta / descripción
function activoLabelDesdeApi(activo) {
  if (!activo || typeof activo !== "object") return "—";
  const tipoRaw = activo.tipoActivo ?? activo.tipo_activo;
  const tipo =
    typeof tipoRaw === "string"
      ? tipoRaw
      : (tipoRaw?.nombre ?? activo.tipo_activo?.nombre ?? "");
  const serie = activo.numeroSerie ?? activo.numero_serie ?? "";
  const etq = activo.etiqueta ?? "";
  const desc = (activo.descripcion ?? "").trim();
  if (tipo && serie) return `${tipo} · ${serie}`;
  if (tipo && etq) return `${tipo} · ${etq}`;
  if (tipo) return String(tipo);
  if (serie) return serie;
  if (etq) return etq;
  if (desc)
    return desc.length > 72 ? `${desc.slice(0, 69)}…` : desc;
  return "—";
}

function prioridadToBadgeStatus(prioridad) {
  const p = (prioridad ?? "").toString().toLowerCase();
  if (p.includes("alta")) return "disponible";
  if (p.includes("media")) return "en-proceso";
  if (p.includes("baja")) return "neutral";
  return "neutral";
}

function mapReporteRow(s) {
  if (!s || typeof s !== "object") return null;
  const activo = s.activo ?? {};
  const espacio = activo.espacio ?? {};
  const edificio = espacio.edificio ?? {};
  const campus = edificio.campus ?? {};
  const usuario = s.usuarioReporta ?? s.usuario ?? {};
  const prioridadObj = s.prioridad ?? {};
  const tipoFalla = s.tipoFalla?.nombre ?? s.tipoFalla ?? "-";
  const prioridad = prioridadObj.nombre ?? s.prioridad?.nivel ?? "Media";

  const mant = s.mantenimiento ?? {};
  const tecnicoMant = mant.usuarioTecnico ?? {};
  const tecnicoAsignado =
    s.nombreTecnicoAsignado ??
    s.nombre_tecnico_asignado ??
    tecnicoMant.nombreCompleto ??
    tecnicoMant.nombre ??
    tecnicoMant.correo ??
    "—";

  return {
    rowKind: "reporte",
    id: s.id ?? s.id_reporte,
    idReporte: s.id ?? s.id_reporte,
    idActivo: activo.id ?? activo.id_activo,
    idPrioridad: prioridadObj.id ?? s.id_prioridad,
    codigo:
      activo.etiqueta ??
      activo.codigoActivo ??
      activo.codigo ??
      s.codigo ??
      "-",
    activoNombre: activoLabelDesdeApi(activo),
    nombreUsuario:
      usuario.nombreCompleto ?? usuario.nombre ?? s.nombreUsuario ?? "-",
    tipoFalla,
    descripcion: s.descripcionFalla ?? s.descripcion ?? "-",
    accionesRealizadas: null,
    campus: campus.nombre ?? s.campus ?? "-",
    edificio: edificio.nombre ?? s.edificio ?? "-",
    aula: espacio.nombreEspacio ?? espacio.nombre ?? s.aula ?? "-",
    tecnicoAsignado,
    prioridad,
    estatus: s.estadoReporte ?? s.estatus ?? "Pendiente",
    fechaReporte: s.fechaReporte ?? s.fecha_reporte ?? null,
  };
}

function mapMantenimientoRow(m) {
  if (!m || typeof m !== "object") return null;
  const reporte = m.reporte ?? {};
  const activo = m.activo ?? reporte.activo ?? {};
  const espacio = activo.espacio ?? {};
  const edificio = espacio.edificio ?? {};
  const campus = edificio.campus ?? {};
  const usuario = reporte.usuarioReporta ?? {};
  const prioridadObj = m.prioridad ?? reporte.prioridad ?? {};
  const tipoFalla = reporte.tipoFalla?.nombre ?? reporte.tipoFalla ?? "-";
  const prioridad = prioridadObj.nombre ?? "Media";
  const ut = m.usuarioTecnico ?? {};
  const tecnicoAsignado =
    ut.nombreCompleto ?? ut.nombre ?? ut.correo ?? "—";

  return {
    rowKind: "mantenimiento",
    id: m.id ?? m.id_mantenimiento,
    idMantenimiento: m.id ?? m.id_mantenimiento,
    idReporte: reporte.id ?? reporte.id_reporte,
    idActivo: activo.id ?? activo.id_activo,
    idPrioridad: prioridadObj.id,
    codigo:
      activo.etiqueta ??
      activo.codigoActivo ??
      activo.codigo ??
      "-",
    activoNombre: activoLabelDesdeApi(activo),
    nombreUsuario:
      usuario.nombreCompleto ?? usuario.nombre ?? "-",
    tipoFalla,
    descripcion: reporte.descripcionFalla ?? "-",
    accionesRealizadas: m.accionesRealizadas ?? "—",
    campus: campus.nombre ?? "-",
    edificio: edificio.nombre ?? "-",
    aula: espacio.nombreEspacio ?? espacio.nombre ?? "-",
    tecnicoAsignado,
    prioridad,
    estatus: m.estadoMantenimiento ?? "-",
    fechaReporte: reporte.fechaReporte ?? m.fechaInicio ?? null,
  };
}

function mapResponse(content, activeTab) {
  const list = Array.isArray(content)
    ? content
    : (content?.content ?? content?.data ?? []);
  const mapper = activeTab === "reportes" ? mapReporteRow : mapMantenimientoRow;
  return list.map(mapper).filter(Boolean);
}

const TABS = [
  {
    key: "reportes",
    label: "Reportes",
    title: "Pendientes de asignar técnico (desaparecen al asignar)",
  },
  {
    key: "mantenimientos",
    label: "Mantenimiento",
    title: "Mantenimientos y seguimiento por técnico",
  },
];

export default function Requests() {
  const queryClient = useQueryClient();
  const invalidateSolicitudes = () =>
    queryClient.invalidateQueries({ queryKey: ["solicitudes"] });

  const [activeTab, setActiveTab] = useState("reportes");
  const [search, setSearch] = useState("");
  const [modalReporte, setModalReporte] = useState(null);
  const [modalMantenimientoId, setModalMantenimientoId] = useState(null);
  const [assignReporte, setAssignReporte] = useState(null);
  const [confirmDelete, setConfirmDelete] = useState(null);
  const pageSize = 10;

  const {
    isLoading: loading,
    error,
    currentPage,
    setCurrentPage,
    content,
  } = usePaginatedQuery({
    queryKey: ["solicitudes", activeTab],
    queryFn: (page, size) =>
      activeTab === "reportes"
        ? solicitudesApi.reportes.getReportes(page, size, "DESC", true)
        : solicitudesApi.mantenimientos.getMantenimientos(page, size),
    errorMessage: "Error al cargar solicitudes",
    pageSize,
  });

  const solicitudes = useMemo(
    () => mapResponse(content, activeTab),
    [content, activeTab],
  );

  const handleTabChange = (tab) => {
    setActiveTab(tab);
    setCurrentPage(0);
    setSearch("");
  };

  const filtered = useMemo(() => {
    const q = search.trim().toLowerCase();
    if (!q) return solicitudes;
    return solicitudes.filter(
      (s) =>
        (s.codigo ?? "").toLowerCase().includes(q) ||
        (s.activoNombre ?? "").toLowerCase().includes(q) ||
        (s.nombreUsuario ?? "").toLowerCase().includes(q) ||
        (s.tipoFalla ?? "").toLowerCase().includes(q) ||
        (s.descripcion ?? "").toLowerCase().includes(q) ||
        (s.accionesRealizadas ?? "").toLowerCase().includes(q),
    );
  }, [solicitudes, search]);

  const paginatedItems = useMemo(() => {
    const start = currentPage * pageSize;
    return filtered.slice(start, start + pageSize);
  }, [filtered, currentPage]);

  const showEmpty = !loading && filtered.length === 0;

  const openDetail = (sol) => {
    if (sol.rowKind === "mantenimiento") {
      setModalMantenimientoId(sol.idMantenimiento ?? sol.id);
      setModalReporte(null);
    } else {
      setModalReporte(sol);
      setModalMantenimientoId(null);
    }
  };

  return (
    <div className="requests-page pb-4">
      <PageHeader
        overline="SOLICITUDES"
        title="Solicitudes de los usuarios"
        subtitle="Reportes: bandeja sin técnico. Mantenimiento: ya asignados. Al asignar, el ítem pasa a Mantenimiento."
      />

      <section className="requests-view d-flex flex-column gap-3">
        <div className="catalogs-card d-flex flex-row align-items-center gap-3 flex-wrap p-4 mb-2">
          <div className="catalogs-card__buscador flex-grow-1 min-w-0">
            <Buscador
              placeholder="Buscar por código, usuario o descripción..."
              value={search}
              onChange={(e) => {
                setSearch(e.target.value);
                setCurrentPage(0);
              }}
              aria-label="Buscar solicitudes"
            />
          </div>
          <div className="catalogs-tabs__main d-flex gap-3 flex-shrink-0 flex-wrap">
            {TABS.map((tab) => (
              <button
                key={tab.key}
                type="button"
                className={`catalogs-tabs__btn ${activeTab === tab.key ? "catalogs-tabs__btn--active" : ""}`}
                title={tab.title}
                onClick={() => handleTabChange(tab.key)}
              >
                {tab.label}
              </button>
            ))}
          </div>
        </div>

        {error && (
          <ErrorBanner message={error} onDismiss={() => invalidateSolicitudes()} />
        )}

        {loading ? (
          <LoadingState message="Cargando solicitudes…" />
        ) : showEmpty ? (
          <EmptyState
            message="No hay solicitudes para mostrar"
            hasSearch={!!search.trim()}
            searchMessage="No coinciden con la búsqueda."
          />
        ) : (
          <div className="requests-view__list d-flex flex-column gap-3">
            {paginatedItems.map((sol, idx) => (
              <div
                key={sol.id ?? `sol-${idx}`}
                className="requests-view__card-wrap"
                role="presentation"
              >
                <div className="requests-view__card">
                  <div
                    className="requests-view__card-inner d-flex align-items-center gap-4"
                    title="Abre el panel con información completa de la solicitud"
                  >
                    <div
                      role="button"
                      tabIndex={0}
                      className="requests-view__card-body requests-view__card-body--clickable"
                      onClick={() => openDetail(sol)}
                      onKeyDown={(e) => {
                        if (e.key === "Enter" || e.key === " ") {
                          e.preventDefault();
                          openDetail(sol);
                        }
                      }}
                      aria-label="Ver detalles de la solicitud"
                    >
                      <div className="requests-view__card-body-inner">
                        <p className="requests-view__numero">{sol.codigo}</p>
                        <p className="requests-view__activo-nombre" title={sol.activoNombre}>
                          {sol.activoNombre}
                        </p>
                        <div className="requests-view__data-row d-flex flex-wrap align-items-center">
                          <div className="requests-view__data-col">
                            <p className="requests-view__label">Reportado por</p>
                            <p className="requests-view__value">
                              {sol.nombreUsuario}
                            </p>
                          </div>
                          <div className="requests-view__data-col">
                            <p className="requests-view__label">Tipo de falla</p>
                            <p className="requests-view__value">{sol.tipoFalla}</p>
                          </div>
                          <div className="requests-view__data-col requests-view__data-col--desc">
                            <p className="requests-view__label">
                              {activeTab === "reportes"
                                ? "Descripción"
                                : "Acciones realizadas"}
                            </p>
                            <p className="requests-view__value requests-view__value--truncate">
                              {activeTab === "reportes"
                                ? sol.descripcion
                                : sol.accionesRealizadas}
                            </p>
                          </div>
                          <div className="requests-view__data-col">
                            <p className="requests-view__label">Campus</p>
                            <p className="requests-view__value">{sol.campus}</p>
                          </div>
                          <div className="requests-view__data-col">
                            <p className="requests-view__label">Edificio</p>
                            <p className="requests-view__value">{sol.edificio}</p>
                          </div>
                          <div className="requests-view__data-col">
                            <p className="requests-view__label">Aula</p>
                            <p className="requests-view__value">{sol.aula}</p>
                          </div>
                          <div className="requests-view__data-col requests-view__data-col--prioridad">
                            <p className="requests-view__label">Prioridad</p>
                            <StatusBadge
                              status={prioridadToBadgeStatus(sol.prioridad)}
                              size="small"
                            >
                              {sol.prioridad ?? "—"}
                            </StatusBadge>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div className="requests-view__card-actions d-flex align-items-center flex-shrink-0">
                      <button
                        type="button"
                        className="requests-view__action-btn requests-view__action-btn--delete"
                        title="Eliminar"
                        aria-label="Eliminar solicitud"
                        onClick={(e) => {
                          e.stopPropagation();
                          setConfirmDelete(sol);
                        }}
                      >
                        <Icon icon={GenericDelete} size={30} />
                      </button>
                      {activeTab === "reportes" && (
                        <button
                          type="button"
                          className="requests-view__action-btn"
                          title="Asignar técnico"
                          aria-label="Asignar técnico"
                          onClick={(e) => {
                            e.stopPropagation();
                            setAssignReporte(sol);
                          }}
                        >
                          <Icon icon={GenericUser} size={30} />
                        </button>
                      )}
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

      <MantenimientoInfoModal
        open={!!modalMantenimientoId}
        onClose={() => setModalMantenimientoId(null)}
        mantenimientoId={modalMantenimientoId}
      />

      <AssignTechnicianModal
        open={!!assignReporte}
        onClose={() => setAssignReporte(null)}
        reporte={assignReporte}
        onAssigned={() => {
          invalidateSolicitudes();
          setAssignReporte(null);
          setModalReporte(null);
        }}
      />

      <ConfirmDeleteModal
        open={!!confirmDelete}
        onClose={() => setConfirmDelete(null)}
        title="¿Confirmar eliminación?"
        message={
          confirmDelete?.rowKind === "mantenimiento"
            ? `Se eliminará el registro de mantenimiento del activo ${confirmDelete?.codigo ?? "—"}. Esta acción no se puede deshacer.`
            : `Se eliminará el reporte del activo ${confirmDelete?.codigo ?? "—"}. Si tiene mantenimiento asignado o evidencias, también se eliminarán. Esta acción no se puede deshacer.`
        }
        onConfirm={async () => {
          const row = confirmDelete;
          if (!row) return;
          try {
            if (row.rowKind === "mantenimiento") {
              const id = row.idMantenimiento ?? row.id;
              await solicitudesApi.mantenimientos.deleteMantenimiento(id);
            } else {
              const id = row.idReporte ?? row.id;
              await solicitudesApi.reportes.deleteReporte(id);
            }
            toast.success("Eliminado correctamente");
            invalidateSolicitudes();
            setModalReporte(null);
            setModalMantenimientoId(null);
            setAssignReporte(null);
          } catch (err) {
            toast.error(err.message || "No se pudo eliminar");
            throw err;
          }
        }}
      />
    </div>
  );
}
