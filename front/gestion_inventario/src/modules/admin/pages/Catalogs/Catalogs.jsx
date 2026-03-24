import { useState, useCallback } from "react";
import { toast } from "../../../../utils/toast.jsx";
import PageHeader from "../../components/dashboard/PageHeader";
import ErrorBanner from "../../../../components/ErrorBanner/ErrorBanner";
import CatalogSection from "./CatalogSection";
import CatalogToolbar from "./components/CatalogToolbar";
import CatalogModals from "./components/CatalogModals";
import { ubicacionesApi } from "../../../../api/ubicacionesApi";
import { tipoActivosApi } from "../../../../api/tipoActivosApi";
import { useCatalogData } from "./hooks/useCatalogData";
import { MAIN_TABS, SECTIONS } from "./constants/catalogConfig";
import "./Catalogs.css";

export default function Catalogs() {
  const [mainTab, setMainTab] = useState("tipos-activos");
  const [subTab, setSubTab] = useState("muebles");
  const [search, setSearch] = useState("");
  const [modalTipoActivoOpen, setModalTipoActivoOpen] = useState(false);
  const [modalLocationOpen, setModalLocationOpen] = useState(false);
  const [modalCampusOpen, setModalCampusOpen] = useState(false);
  const [modalBuildingOpen, setModalBuildingOpen] = useState(false);
  const [modalClassroomOpen, setModalClassroomOpen] = useState(false);
  const [editLocation, setEditLocation] = useState(null);
  const [editTipoActivo, setEditTipoActivo] = useState(null);
  const [confirmDeleteLocation, setConfirmDeleteLocation] = useState(null);
  const [confirmDeleteTipoActivo, setConfirmDeleteTipoActivo] = useState(null);

  const {
    isLoading: loading,
    error,
    invalidate,
    currentPage,
    setCurrentPage,
    totalPages,
    totalElements,
    items,
    tiposActivosItems,
    campusList,
    edificiosList,
    hasLocations,
  } = useCatalogData(mainTab, subTab);

  const config = SECTIONS[subTab] ?? {};
  const isLocations = mainTab === "ubicaciones";
  const refreshLocations = invalidate;

  const handleMainTab = useCallback((id) => {
    setMainTab(id);
    setCurrentPage(0);
    const tab = MAIN_TABS.find((t) => t.id === id);
    if (tab?.sub?.[0]) setSubTab(tab.sub[0]);
  }, [setCurrentPage]);

  const handleNewLocation = useCallback(() => {
    setEditLocation(null);
    if (!hasLocations) setModalLocationOpen(true);
    else if (subTab === "campus") setModalCampusOpen(true);
    else if (subTab === "edificios") setModalBuildingOpen(true);
    else if (subTab === "aulas") setModalClassroomOpen(true);
  }, [hasLocations, subTab]);

  const handleNuevoClick = useCallback(() => {
    if (mainTab === "tipos-activos") {
      setEditTipoActivo(null);
      setModalTipoActivoOpen(true);
    } else if (isLocations) {
      handleNewLocation();
    }
  }, [mainTab, isLocations, handleNewLocation]);

  const handleEditLocation = useCallback((item) => {
    if (!item) return;
    setEditLocation(item);
    if (subTab === "campus") setModalCampusOpen(true);
    else if (subTab === "edificios") setModalBuildingOpen(true);
    else if (subTab === "aulas") setModalClassroomOpen(true);
  }, [subTab]);

  const handleEditTipoActivo = useCallback((item) => {
    if (!item) return;
    setEditTipoActivo(item);
    setModalTipoActivoOpen(true);
  }, []);

  const handleGuardarCampus = useCallback(async (data) => {
    try {
      if (editLocation?.id) {
        await ubicacionesApi.updateCampus(editLocation.id, data);
        toast.success("Campus actualizado correctamente");
      } else {
        await ubicacionesApi.createCampus(data);
        toast.success("Campus registrado correctamente");
      }
      refreshLocations();
    } catch (err) {
      toast.error(err?.message ?? "Error al guardar");
      throw err;
    }
  }, [editLocation, refreshLocations]);

  const handleGuardarEdificio = useCallback(async (data) => {
    try {
      if (editLocation?.id) {
        await ubicacionesApi.updateEdificio(editLocation.id, data);
        toast.success("Edificio actualizado correctamente");
      } else {
        await ubicacionesApi.createEdificio(data);
        toast.success("Edificio registrado correctamente");
      }
      refreshLocations();
    } catch (err) {
      toast.error(err?.message ?? "Error al guardar");
      throw err;
    }
  }, [editLocation, refreshLocations]);

  const handleGuardarAula = useCallback(async (data) => {
    try {
      if (editLocation?.id) {
        await ubicacionesApi.updateEspacio(editLocation.id, data);
        toast.success("Aula actualizada correctamente");
      } else {
        await ubicacionesApi.createEspacio(data);
        toast.success("Aula registrada correctamente");
      }
      refreshLocations();
    } catch (err) {
      toast.error(err?.message ?? "Error al guardar");
      throw err;
    }
  }, [editLocation, refreshLocations]);

  const handleConfirmDeleteLocation = useCallback(async () => {
    if (!confirmDeleteLocation?.id) return;
    try {
      if (subTab === "campus") {
        await ubicacionesApi.toggleStatusCampus(confirmDeleteLocation.id);
        toast.success("Campus eliminado correctamente");
      } else if (subTab === "edificios") {
        await ubicacionesApi.toggleStatusEdificio(confirmDeleteLocation.id);
        toast.success("Edificio eliminado correctamente");
      } else if (subTab === "aulas") {
        await ubicacionesApi.toggleStatusEspacio(confirmDeleteLocation.id);
        toast.success("Aula eliminada correctamente");
      }
      setConfirmDeleteLocation(null);
      refreshLocations();
    } catch (err) {
      toast.error(err?.message ?? "Error al eliminar");
    }
  }, [confirmDeleteLocation, subTab, refreshLocations]);

  const handleConfirmDeleteTipoActivo = useCallback(async () => {
    if (!confirmDeleteTipoActivo?.id) return;
    try {
      await tipoActivosApi.toggleStatusTipoActivo(confirmDeleteTipoActivo.id);
      toast.success("Tipo de activo eliminado correctamente");
      setConfirmDeleteTipoActivo(null);
      invalidate();
    } catch (err) {
      toast.error(err?.message ?? "Error al eliminar");
    }
  }, [confirmDeleteTipoActivo, invalidate]);

  const handleGuardarTipoActivo = useCallback(async (data) => {
    try {
      if (editTipoActivo?.id) {
        await tipoActivosApi.actualizarTipoActivo(editTipoActivo.id, data);
        toast.success("Tipo de activo actualizado correctamente");
      } else {
        await tipoActivosApi.crearTipoActivo(data);
        toast.success("Tipo de activo guardado correctamente");
      }
      invalidate();
      setModalTipoActivoOpen(false);
      setEditTipoActivo(null);
    } catch (error) {
      toast.error(error?.message ?? "Error al guardar tipo de activo");
      throw error;
    }
  }, [editTipoActivo, invalidate]);

  const getDeleteMessage = useCallback(() => {
    if (!confirmDeleteLocation) return "";
    const nombre = confirmDeleteLocation.nombre ?? "este elemento";
    if (subTab === "campus") return `Se eliminará el campus "${nombre}". Esta acción no se puede deshacer.`;
    if (subTab === "edificios") return `Se eliminará el edificio "${nombre}". Esta acción no se puede deshacer.`;
    if (subTab === "aulas") return `Se eliminará el aula "${nombre}". Esta acción no se puede deshacer.`;
    return `Se eliminará ${nombre}. Esta acción no se puede deshacer.`;
  }, [confirmDeleteLocation, subTab]);

  return (
    <div className="catalogs-page">
      <PageHeader
        overline="GESTIÓN DE CATÁLOGOS"
        title="Catálogos del Sistema"
        subtitle="Administra tipos de activos y ubicaciones"
      />

      <CatalogToolbar
        mainTab={mainTab}
        subTab={subTab}
        search={search}
        onSearchChange={setSearch}
        onMainTabChange={handleMainTab}
        onSubTabChange={setSubTab}
        onPageReset={() => setCurrentPage(0)}
        onNuevoClick={handleNuevoClick}
        isLocations={isLocations}
        hasLocations={hasLocations}
      />

      <CatalogModals
        modalTipoActivoOpen={modalTipoActivoOpen}
        onCloseTipoActivo={() => { setModalTipoActivoOpen(false); setEditTipoActivo(null); }}
        editTipoActivo={editTipoActivo}
        onGuardarTipoActivo={handleGuardarTipoActivo}
        modalLocationOpen={modalLocationOpen}
        onCloseLocation={() => setModalLocationOpen(false)}
        refreshLocations={refreshLocations}
        modalCampusOpen={modalCampusOpen}
        onCloseCampus={() => { setModalCampusOpen(false); setEditLocation(null); }}
        editLocation={editLocation}
        onGuardarCampus={handleGuardarCampus}
        modalBuildingOpen={modalBuildingOpen}
        onCloseBuilding={() => { setModalBuildingOpen(false); setEditLocation(null); }}
        campusList={campusList}
        items={items}
        onGuardarEdificio={handleGuardarEdificio}
        modalClassroomOpen={modalClassroomOpen}
        onCloseClassroom={() => { setModalClassroomOpen(false); setEditLocation(null); }}
        edificiosList={edificiosList}
        onGuardarAula={handleGuardarAula}
        subTab={subTab}
        confirmDeleteLocation={confirmDeleteLocation}
        onCloseConfirmDeleteLocation={() => setConfirmDeleteLocation(null)}
        onConfirmDeleteLocation={handleConfirmDeleteLocation}
        getDeleteMessage={getDeleteMessage}
        confirmDeleteTipoActivo={confirmDeleteTipoActivo}
        onCloseConfirmDeleteTipoActivo={() => setConfirmDeleteTipoActivo(null)}
        onConfirmDeleteTipoActivo={handleConfirmDeleteTipoActivo}
      />

      {error && <ErrorBanner message={error} onDismiss={() => invalidate()} />}

      <CatalogSection
        showToolbar={false}
        searchPlaceholder={config.searchPlaceholder}
        emptyMessage={config.emptyMessage}
        sectionKey={subTab}
        loadingMessage={mainTab === "tipos-activos" ? "Cargando tipos de activos…" : "Cargando ubicaciones…"}
        items={isLocations ? items : tiposActivosItems}
        loading={loading}
        search={search}
        onSearchChange={setSearch}
        countLabel={config.countLabel}
        onEdit={mainTab === "tipos-activos" ? handleEditTipoActivo : (isLocations && hasLocations ? handleEditLocation : undefined)}
        onDelete={mainTab === "tipos-activos" ? (item) => setConfirmDeleteTipoActivo(item) : (isLocations && hasLocations ? (item) => setConfirmDeleteLocation(item) : undefined)}
        serverPagination={isLocations}
        currentPage={currentPage}
        totalPages={totalPages}
        totalElements={totalElements}
        onPageChange={setCurrentPage}
      />
    </div>
  );
}
