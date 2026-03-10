import { useState, useMemo } from "react";
import NewAssetModal from "./NewAssetModal";
import PageHeader from "../../components/dashboard/PageHeader";
import StatCard from "../../components/dashboard/StatCard";
import Card from "../../../../components/Card/Card";
import Buscador from "../../../../components/Buscador/Buscador";
import StatusBadge from "../../../../components/StatusBadge/StatusBadge";
import ActivosEmptyState from "./ActivosEmptyState";
import Button from "../../../../components/Button/Button";
import {
  ShopBag,
  GenericUser,
  GenericSettings,
  NotificationsBell,
  GenericDelete,
  GenericEdit,
  TimeTime,
  SecurityPassport,
  GenericPlus,
  FilesImport,
} from "@heathmont/moon-icons";
import Icon from "../../../../components/Icon/Icon";
import "./Activos.css";

const STAT_ICONS = [ShopBag, NotificationsBell, GenericUser, GenericSettings];

export default function Activos({
  activos: activosProp = [],
  stats: statsProp = [],
  loading: loadingProp = false,
  error: errorProp = null,
  onSearch,
  onImportExcel,
  onNuevo,
  onEliminar,
  onEditar,
  onHistorial,
  onDetalles,
}) {
  const [search, setSearch] = useState("");
  const [modalNuevoOpen, setModalNuevoOpen] = useState(false);

  const activos = Array.isArray(activosProp) ? activosProp : [];
  const stats = Array.isArray(statsProp) ? statsProp : [];
  const loading = loadingProp;
  const error = errorProp;

  const filtered = useMemo(() => {
    let list = Array.isArray(activos) ? activos : [];
    const q = search.trim().toLowerCase();
    if (q) list = list.filter((a) => (a.nombre ?? "").toLowerCase().includes(q) || (a.codigo ?? "").toLowerCase().includes(q) || (a.descripcionCorta ?? "").toLowerCase().includes(q));
    return list;
  }, [activos, search]);

  const handleSearchChange = (e) => {
    const value = e.target.value;
    setSearch(value);
    onSearch?.(value);
  };

  const statusLabel = (s) => {
    const labels = { disponible: "Disponible", resguardado: "Resguardado", mantenimiento: "Mantenimiento", "en proceso": "En proceso", baja: "Baja", reportado: "Reportado" };
    return labels[s] ?? s;
  };

  const showEmptyState = filtered.length === 0;

  return (
    <div className={`activos-page ${showEmptyState ? "activos-page--empty" : ""}`}>
      <PageHeader
        overline="GESTIÓN DE INVENTARIO"
        title="Inventario de Activos"
        subtitle="Administra tipos de activos y ubicaciones"
      />

      <section className="activos-view" aria-label="Inventario de activos">
        <div className="activos-view__stats row g-3 mb-4">
          {stats.map((stat, i) => (
            <div key={i} className="col-12 col-sm-6 col-xl-3">
              <StatCard icon={STAT_ICONS[i]} {...stat} />
            </div>
          ))}
        </div>

        <div className="activos-view__toolbar">
          <div className="activos-view__buscador">
            <Buscador
              placeholder="Buscar activo por nombre...."
              value={search}
              onChange={handleSearchChange}
              aria-label="Buscar activos"
            />
          </div>
          <div className="activos-view__actions">
            <Button variant="secondary" iconLeft={FilesImport} iconSize={30} onClick={() => onImportExcel?.()}>
              Importar Excel
            </Button>
            <Button variant="primary" iconLeft={GenericPlus} onClick={() => setModalNuevoOpen(true)}>
              Nuevo
            </Button>
          </div>
        </div>

        {error && (
          <div className="activos-view__error" role="alert">
            {error}
          </div>
        )}

        {loading ? (
          <div className="activos-view__loading">Cargando activos…</div>
        ) : (
          <div className="activos-view__list">
            {showEmptyState ? (
              <ActivosEmptyState hasSearch={!!search.trim()} />
            ) : (
              filtered.map((item) => (
                <div key={item.id} className="activos-view__asset-card-wrap">
                  <Card padding="medium" className="activos-view__asset-card">
                    <div className="activos-view__asset-content">
                      <div className="activos-view__asset-row activos-view__asset-row--1">
                    <div className="activos-view__asset-col">
                      <p className="activos-view__asset-code">{item.codigo ?? "—"}</p>
                      <p className="activos-view__asset-desc">{item.descripcionCorta ?? "—"}</p>
                    </div>
                    <div className="activos-view__asset-col">
                      <p className="activos-view__asset-label">Activo</p>
                      <p className="activos-view__asset-value">{item.nombre ?? "—"}</p>
                    </div>
                    <div className="activos-view__asset-col">
                      <p className="activos-view__asset-label">Asignado a</p>
                      <p className="activos-view__asset-value">{item.asignadoA ?? "—"}</p>
                    </div>
                    <div className="activos-view__asset-col">
                      <p className="activos-view__asset-label">Tipo de activo</p>
                      <p className="activos-view__asset-value">{item.tipoActivo ?? "—"}</p>
                    </div>
                  </div>
                  <div className="activos-view__asset-row activos-view__asset-row--2">
                    <div className="activos-view__asset-col">
                      <p className="activos-view__asset-label">Estado</p>
                      <StatusBadge status={item.status ?? "disponible"} size="small">
                        {statusLabel(item.status ?? "disponible")}
                      </StatusBadge>
                    </div>
                    <div className="activos-view__asset-col">
                      <p className="activos-view__asset-label">Campus</p>
                      <p className="activos-view__asset-value">{item.campus ?? "—"}</p>
                    </div>
                    <div className="activos-view__asset-col">
                      <p className="activos-view__asset-label">Edificio</p>
                      <p className="activos-view__asset-value">{item.edificio ?? "—"}</p>
                    </div>
                    <div className="activos-view__asset-col">
                      <p className="activos-view__asset-label">Aula</p>
                      <p className="activos-view__asset-value">{item.aula ?? "—"}</p>
                    </div>
                  </div>
                    </div>
                    <div className="activos-view__asset-actions" aria-label="Acciones del activo">
                      <button type="button" className="activos-view__action-btn activos-view__action-btn--delete" title="Eliminar" aria-label="Eliminar" onClick={() => onEliminar?.(item)}>
                        <Icon icon={GenericDelete} size={30} />
                      </button>
                      <button type="button" className="activos-view__action-btn" title="Editar" aria-label="Editar" onClick={() => onEditar?.(item)}>
                        <Icon icon={GenericEdit} size={30} />
                      </button>
                      <button type="button" className="activos-view__action-btn" title="Historial" aria-label="Historial" onClick={() => onHistorial?.(item)}>
                        <Icon icon={TimeTime} size={30} />
                      </button>
                      <button type="button" className="activos-view__action-btn" title="Detalles" aria-label="Detalles" onClick={() => onDetalles?.(item)}>
                        <Icon icon={SecurityPassport} size={30} />
                      </button>
                    </div>
                  </Card>
                </div>
              ))
            )}
          </div>
        )}
      </section>

      <NewAssetModal
        open={modalNuevoOpen}
        onClose={() => setModalNuevoOpen(false)}
        onGuardar={(data) => { onNuevo?.(data); setModalNuevoOpen(false); }}
      />
    </div>
  );
}
