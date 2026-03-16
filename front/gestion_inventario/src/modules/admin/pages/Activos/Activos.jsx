import { useState, useMemo } from "react";
import NewAssetModal from "./NewAssetModal";
import PageHeader from "../../components/dashboard/PageHeader";
import StatCard from "../../components/dashboard/StatCard";
import Card from "../../../../components/Card/Card";
import Buscador from "../../../../components/Buscador/Buscador";
import StatusBadge from "../../../../components/StatusBadge/StatusBadge";
import EmptyState from "../../../../components/EmptyState/EmptyState";
import LoadingState from "../../../../components/LoadingState/LoadingState";
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
import { toast } from "../../../../utils/toast.jsx";
import ConfirmDeleteModal from "../../../../components/ConfirmDeleteModal/ConfirmDeleteModal";
import ErrorBanner from "../../../../components/ErrorBanner/ErrorBanner";
import "./Activos.css";
import Pagination from "../../components/layout/Pagination.jsx";

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
  const [modalEditAsset, setModalEditAsset] = useState(null);
  const [confirmDeleteAsset, setConfirmDeleteAsset] = useState(null);

  const activos = Array.isArray(activosProp) ? activosProp : [];
  const stats = Array.isArray(statsProp) ? statsProp : [];
  const loading = loadingProp;
  const error = errorProp;

  const filtered = useMemo(() => {
    let list = Array.isArray(activos) ? activos : [];
    const q = search.trim().toLowerCase();
    if (q)
      list = list.filter(
        (a) =>
          (a.nombre ?? "").toLowerCase().includes(q) ||
          (a.codigo ?? "").toLowerCase().includes(q) ||
          (a.descripcionCorta ?? "").toLowerCase().includes(q),
      );
    return list;
  }, [activos, search]);

  const handleSearchChange = (e) => {
    const value = e.target.value;
    setSearch(value);
    onSearch?.(value);
  };

  const statusLabel = (s) => {
    const labels = {
      disponible: "Disponible",
      resguardado: "Resguardado",
      mantenimiento: "Mantenimiento",
      "en proceso": "En proceso",
      baja: "Baja",
      reportado: "Reportado",
    };
    return labels[s] ?? s;
  };

  const showEmptyState = filtered.length === 0;

  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;

  const totalItems = filtered.length;
  const totalPages = Math.ceil(totalItems / itemsPerPage);

  const paginatedItems = useMemo(() => {
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    return filtered.slice(startIndex, endIndex);
  }, [filtered, currentPage, itemsPerPage]);

  const handlePageChange = (page) => {
    setCurrentPage(page);
    window.scrollTo(0, 0);
  };

  return (
    <div
      className={`activos-page ${showEmptyState ? "activos-page--empty" : ""}`}
    >
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
            <Button
              variant="secondary"
              iconLeft={FilesImport}
              iconSize={30}
              onClick={() => onImportExcel?.()}
            >
              Importar Excel
            </Button>
            <Button
              variant="primary"
              iconLeft={GenericPlus}
              iconSize={30}
              onClick={() => setModalNuevoOpen(true)}
            >
              Nuevo
            </Button>
          </div>
        </div>

        {error && <ErrorBanner message={error} />}

        {loading ? (
          <div className="activos-view__loading">
            <LoadingState message="Cargando activos…" />
          </div>
        ) : (
          <div className="activos-view__list">
            {showEmptyState ? (
              <EmptyState
                message="No hay activos para mostrar"
                hasSearch={!!search.trim()}
                searchMessage="No hay activos o no coinciden con la búsqueda."
              />
            ) : (
              paginatedItems.map((item) => (
                <div key={item.id} className="activos-view__asset-card-wrap">
                  <Card padding="medium" className="activos-view__asset-card">
                    <div className="activos-view__asset-content">
                      <div className="activos-view__asset-row activos-view__asset-row--1">
                        <div className="activos-view__asset-col">
                          <p className="activos-view__asset-code">
                            {item.codigo ?? "—"}
                          </p>
                          <p className="activos-view__asset-desc">
                            {item.descripcionCorta ?? "—"}
                          </p>
                        </div>
                        <div className="activos-view__asset-col">
                          <p className="activos-view__asset-label">Activo</p>
                          <p className="activos-view__asset-value">
                            {item.nombre ?? "—"}
                          </p>
                        </div>
                        <div className="activos-view__asset-col">
                          <p className="activos-view__asset-label">
                            Asignado a
                          </p>
                          <p className="activos-view__asset-value">
                            {item.asignadoA ?? "—"}
                          </p>
                        </div>
                        <div className="activos-view__asset-col">
                          <p className="activos-view__asset-label">
                            Tipo de activo
                          </p>
                          <p className="activos-view__asset-value">
                            {item.tipoActivo ?? "—"}
                          </p>
                        </div>
                      </div>
                      <div className="activos-view__asset-row activos-view__asset-row--2">
                        <div className="activos-view__asset-col">
                          <p className="activos-view__asset-label">Estado</p>
                          <StatusBadge
                            status={item.status ?? "disponible"}
                            size="small"
                          >
                            {statusLabel(item.status ?? "disponible")}
                          </StatusBadge>
                        </div>
                        <div className="activos-view__asset-col">
                          <p className="activos-view__asset-label">Campus</p>
                          <p className="activos-view__asset-value">
                            {item.campus ?? "—"}
                          </p>
                        </div>
                        <div className="activos-view__asset-col">
                          <p className="activos-view__asset-label">Edificio</p>
                          <p className="activos-view__asset-value">
                            {item.edificio ?? "—"}
                          </p>
                        </div>
                        <div className="activos-view__asset-col">
                          <p className="activos-view__asset-label">Aula</p>
                          <p className="activos-view__asset-value">
                            {item.aula ?? "—"}
                          </p>
                        </div>
                      </div>
                    </div>
                    <div
                      className="activos-view__asset-actions"
                      aria-label="Acciones del activo"
                    >
                      <button
                        type="button"
                        className="activos-view__action-btn activos-view__action-btn--delete"
                        title="Eliminar"
                        aria-label="Eliminar"
                        onClick={() => setConfirmDeleteAsset(item)}
                      >
                        <Icon icon={GenericDelete} size={30} />
                      </button>
                      <button
                        type="button"
                        className="activos-view__action-btn"
                        title="Editar"
                        aria-label="Editar"
                        onClick={() => setModalEditAsset(item)}
                      >
                        <Icon icon={GenericEdit} size={30} />
                      </button>
                      <button
                        type="button"
                        className="activos-view__action-btn"
                        title="Historial"
                        aria-label="Historial"
                        onClick={() => onHistorial?.(item)}
                      >
                        <Icon icon={TimeTime} size={30} />
                      </button>
                      <button
                        type="button"
                        className="activos-view__action-btn"
                        title="Detalles"
                        aria-label="Detalles"
                        onClick={() => onDetalles?.(item)}
                      >
                        <Icon icon={SecurityPassport} size={30} />
                      </button>
                    </div>
                  </Card>
                </div>
              ))
            )}
          </div>
        )}

        <Pagination
          totalPages={totalPages}
          currentPage={currentPage}
          totalElements={totalItems}
          pageSize={itemsPerPage}
          onPageChange={handlePageChange}
        />
      </section>

      <NewAssetModal
        open={modalNuevoOpen}
        onClose={() => setModalNuevoOpen(false)}
        onGuardar={async (data) => {
          try {
            await onNuevo?.(data);
            setModalNuevoOpen(false);
            toast.success("Activo guardado correctamente");
          } catch {
            // El toast de error lo maneja el padre (ActivosPage)
          }
        }}
      />
      <NewAssetModal
        open={!!modalEditAsset}
        onClose={() => setModalEditAsset(null)}
        initialData={modalEditAsset}
        onGuardar={async (data) => {
          try {
            await onEditar?.(modalEditAsset, data);
            setModalEditAsset(null);
            toast.success("Activo actualizado correctamente");
          } catch {
            // El toast de error lo maneja el padre
          }
        }}
      />

      <ConfirmDeleteModal
        open={!!confirmDeleteAsset}
        onClose={() => setConfirmDeleteAsset(null)}
        onConfirm={async () => {
          await onEliminar?.(confirmDeleteAsset);
          toast.success("Activo eliminado correctamente");
        }}
        title="¿Confirmar eliminación?"
        message={`Se eliminará el activo "${confirmDeleteAsset?.nombre ?? confirmDeleteAsset?.codigo ?? "este elemento"}". Esta acción no se puede deshacer.`}
      />
    </div>
  );
}
