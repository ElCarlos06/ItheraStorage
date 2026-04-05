import { useState, useRef, useMemo, useCallback } from "react";
import NewAssetModal from "./NewAssetModal";
import AssignResguardoModal from "./components/AssignResguardoModal";
import ReleaseResguardoModal from "./components/ReleaseResguardoModal";
import HistorialActivoModal from "./components/HistorialActivoModal";
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
import { importApi } from "../../../../api/importApi.js";

const STAT_ICONS = [ShopBag, NotificationsBell, GenericUser, GenericSettings];

export default function Activos({
  activos: activosProp = [],
  stats: statsProp = [],
  loading: loadingProp = false,
  fetching: fetchingProp = false,
  error: errorProp = null,
  currentPage = 0,
  totalPages = 1,
  totalElements = 0,
  pageSize = 10,
  onPageChange,
  onSearch,
  onRefresh,
  onNuevo,
  onEliminar,
  onEditar,
  onHistorial,
  onDetalles,
  onLiberar,
}) {
  const [search, setSearch] = useState("");
  const [modalNuevoOpen, setModalNuevoOpen] = useState(false);
  const fileInputRef = useRef(null);
  const [modalEditAsset, setModalEditAsset] = useState(null);
  const [confirmDeleteAsset, setConfirmDeleteAsset] = useState(null);
  const [modalAssignAsset, setModalAssignAsset] = useState(null);
  const [modalReleaseAsset, setModalReleaseAsset] = useState(null);
  const [modalHistorialAsset, setModalHistorialAsset] = useState(null);

  const activos = Array.isArray(activosProp) ? activosProp : [];
  const stats = Array.isArray(statsProp) ? statsProp : [];
  const loading = loadingProp;
  const fetching = fetchingProp;
  const error = errorProp;

  // Filtrado local solo para búsqueda en la página actual
  const filtered = useMemo(
    () =>
      activos.filter((a) => {
        const q = search.trim().toLowerCase();
        if (!q) return true;
        return (
          (a.etiqueta ?? "").toLowerCase().includes(q) ||
          (a.numeroSerie ?? "").toLowerCase().includes(q) ||
          (a.tipoActivo?.nombre ?? "").toLowerCase().includes(q) ||
          (a.descripcion ?? "").toLowerCase().includes(q)
        );
      }),
    [activos, search],
  );

  const handleSearchChange = useCallback(
    (e) => {
      const value = e.target.value;
      setSearch(value);
      onSearch?.(value);
    },
    [onSearch],
  );

  const showEmptyState = !loading && filtered.length === 0;

  const handlePageChange = useCallback(
    (page) => {
      onPageChange?.(page);
      window.scrollTo(0, 0);
    },
    [onPageChange],
  );

  const handleUploadExcel = useCallback(
    async (e) => {
      const file = e.target.files?.[0];
      if (!file) return;
      try {
        const result = await importApi.upload(file);

        if (result?.error) {
          // Hubo rechazos, mostramos toast de error con el detalle por más tiempo
          toast.error(
            result.message || "Importación terminada con errores.",
            8000,
          );
        } else {
          // Todo perfecto
          toast.success(
            result?.message || "Activos importados correctamente",
            5000,
          );
        }
        await onRefresh?.();
      } catch (error) {
        toast.error(error.message || "Error al importar el archivo");
      } finally {
        e.target.value = null;
      }
    },
    [onRefresh],
  );

  return (
    <div
      className={`activos-page ${showEmptyState ? "activos-page--empty d-flex flex-column" : ""}`}
    >
      <PageHeader
        overline="GESTIÓN DE INVENTARIO"
        title="Inventario de Activos"
        subtitle="Administra tipos de activos y ubicaciones"
      />

      <section
        className="activos-view d-flex flex-column flex-grow-1 min-vh-0"
        aria-label="Inventario de activos"
      >
        <div className="activos-view__stats row g-3 mb-4">
          {stats.map((stat, i) => (
            <div key={i} className="col-12 col-sm-6 col-xl-3">
              <StatCard icon={STAT_ICONS[i]} {...stat} />
            </div>
          ))}
        </div>

        <div className="activos-view__toolbar d-flex flex-wrap align-items-center gap-3 mb-4 p-4">
          <div className="activos-view__buscador flex-grow-1">
            <Buscador
              placeholder="Buscar activo por nombre...."
              value={search}
              onChange={handleSearchChange}
              aria-label="Buscar activos"
            />
          </div>
          <div className="activos-view__actions d-flex align-items-center gap-3 ms-auto">
            <input
              type="file"
              ref={fileInputRef}
              className="d-none"
              accept=".xlsx, .xls"
              onChange={handleUploadExcel}
            />
            <Button
              variant="secondary"
              iconLeft={FilesImport}
              iconSize={30}
              title="Importar activos desde un archivo Excel (.xlsx, .xls)"
              onClick={() => fileInputRef.current?.click()}
            >
              Importar Excel
            </Button>
            <Button
              variant="primary"
              iconLeft={GenericPlus}
              iconSize={30}
              title="Registrar un nuevo activo en el inventario"
              onClick={() => setModalNuevoOpen(true)}
            >
              Nuevo
            </Button>
          </div>
        </div>

        {error && <ErrorBanner message={error} />}

        {loading ? (
          <div className="activos-view__loading d-flex align-items-center justify-content-center py-5 px-4">
            <LoadingState message="Cargando activos…" />
          </div>
        ) : (
          <div
            className={`activos-view__list d-flex flex-column gap-3 min-vh-0 ${
              fetching ? "activos-view__list--fetching" : ""
            }`}
          >
            {showEmptyState ? (
              <EmptyState
                message="No hay activos para mostrar"
                hasSearch={!!search.trim()}
                searchMessage="No hay activos o no coinciden con la búsqueda."
              />
            ) : (
              filtered.map((item) => (
                <ActivosCard
                  key={item.id}
                  item={item}
                  onEliminar={() => setConfirmDeleteAsset(item)}
                  onEditar={() => setModalEditAsset(item)}
                  onHistorial={
                    onHistorial ?? ((item) => setModalHistorialAsset(item))
                  }
                  onDetalles={() => {
                    const isAsignado =
                      item.estadoCustodia === "Resguardado" ||
                      item.estadoCustodia === "En Proceso";
                    if (isAsignado) {
                      setModalReleaseAsset(item);
                    } else {
                      setModalAssignAsset(item);
                    }
                  }}
                />
              ))
            )}
          </div>
        )}

        <Pagination
          totalPages={totalPages}
          currentPage={currentPage}
          totalElements={totalElements}
          pageSize={pageSize}
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
          } catch (err) {
            toast.error(err.message ?? "Error al asignar resguardo");
          }
        }}
      />
      <ReleaseResguardoModal
        open={!!modalReleaseAsset}
        onClose={() => setModalReleaseAsset(null)}
        asset={modalReleaseAsset}
        onLiberar={async (resguardoId, data) => {
          try {
            await onLiberar?.(resguardoId, data);
            setModalReleaseAsset(null);
          } catch (err) {}
        }}
      />
      <HistorialActivoModal
        open={!!modalHistorialAsset}
        onClose={() => setModalHistorialAsset(null)}
        asset={modalHistorialAsset}
      />
    </div>
  );
}
