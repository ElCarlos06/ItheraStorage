import { useState, useMemo, useRef } from "react";
import NewAssetModal from "./NewAssetModal";
import AssignResguardoModal from "./components/AssignResguardoModal";
import PageHeader from "../../components/dashboard/PageHeader";
import StatCard from "../../components/dashboard/StatCard";
import Buscador from "../../../../components/Buscador/Buscador";
import EmptyState from "../../../../components/EmptyState/EmptyState";
import LoadingState from "../../../../components/LoadingState/LoadingState";
import Button from "../../../../components/Button/Button";
import {
  ShopBag,
  GenericUser,
  GenericSettings,
  NotificationsBell,
  GenericPlus,
  FilesImport,
} from "@heathmont/moon-icons";
import { toast } from "../../../../utils/toast.jsx";
import ConfirmDeleteModal from "../../../../components/ConfirmDeleteModal/ConfirmDeleteModal";
import ErrorBanner from "../../../../components/ErrorBanner/ErrorBanner";
import "./Activos.css";
import Pagination from "../../components/layout/Pagination.jsx";
import ActivosCard from "./components/ActivosCard.jsx";
import { getProfileFromToken } from "../../../../api/authApi.js";
import { importApi } from "../../../../api/importApi.js";

const STAT_ICONS = [ShopBag, NotificationsBell, GenericUser, GenericSettings];

export default function Activos({
  activos: activosProp = [],
  stats: statsProp = [],
  loading: loadingProp = false,
  error: errorProp = null,
  onSearch,
  onRefresh,
  onNuevo,
  onEliminar,
  onEditar,
  onHistorial,
  onDetalles,
}) {
  const [search, setSearch] = useState("");
  const [modalNuevoOpen, setModalNuevoOpen] = useState(false);
  const fileInputRef = useRef(null);
  const [modalEditAsset, setModalEditAsset] = useState(null);
  const [confirmDeleteAsset, setConfirmDeleteAsset] = useState(null);
  const [modalAssignAsset, setModalAssignAsset] = useState(null);

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

  const handleUploadExcel = async (e) => {
    const file = e.target.files?.[0];
    if (!file) return;

    try {
      await importApi.upload(file);
      toast.success("Activos importados correctamente");
      if (onRefresh) {
        await onRefresh();
      }
    } catch (error) {
      toast.error(error.message || "Error al importar el archivo");
    } finally {
      e.target.value = null;
    }
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
            <input
              type="file"
              ref={fileInputRef}
              style={{ display: "none" }}
              accept=".xlsx, .xls"
              onChange={handleUploadExcel}
            />
            <Button
              variant="secondary"
              iconLeft={FilesImport}
              iconSize={30}
              onClick={() => fileInputRef.current?.click()}
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
                <ActivosCard
                  key={item.id}
                  item={item}
                  onEliminar={() => setConfirmDeleteAsset(item)}
                  onEditar={() => setModalEditAsset(item)}
                  onHistorial={onHistorial}
                  onDetalles={() => setModalAssignAsset(item)}
                />
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
      <AssignResguardoModal
        open={!!modalAssignAsset}
        onClose={() => setModalAssignAsset(null)}
        onGuardar={async (data) => {
          try {
            await onDetalles?.(modalAssignAsset, data);
            setModalAssignAsset(null);
            // El toast lo muestra ActivosPage.handleAsignarResguardo
          } catch (err) {
            toast.error(err.message ?? "Error al asignar resguardo");
          }
        }}
      />
    </div>
  );
}
