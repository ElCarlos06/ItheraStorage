import { useState, useEffect, useMemo } from "react";
import { toast } from "../../../../utils/toast.jsx";
import PageHeader from "../../components/dashboard/PageHeader";
import Buscador from "../../../../components/Buscador/Buscador";
import Button from "../../../../components/Button/Button";
import Select from "../../../../components/Select/Select";
import CatalogSection from "./CatalogSection";
import RegisterTipoActivoModal from "./RegisterTipoActivoModal";
import RegisterLocationModal from "./RegisterLocationModal";
import RegisterCampusModal from "./RegisterCampusModal";
import RegisterBuildingModal from "./RegisterBuildingModal";
import RegisterClassroomModal from "./RegisterClassroomModal";
import ConfirmDeleteModal from "../../../../components/ConfirmDeleteModal/ConfirmDeleteModal";
import ErrorBanner from "../../../../components/ErrorBanner/ErrorBanner";
import Pagination from "../../components/layout/Pagination";
import { GenericPlus } from "@heathmont/moon-icons";
import { ubicacionesApi } from "../../../../api/ubicacionesApi";
import "./Catalogs.css";
import { tipoActivosApi } from "../../../../api/tipoActivosApi.js";
import { getCached, setCache } from "../../../../utils/apiCache";

const MAIN_TABS = [
  {
    id: "tipos-activos",
    label: "Tipos de Activos",
    sub: ["muebles", "vehiculos"],
  },
  {
    id: "ubicaciones",
    label: "Ubicaciones",
    sub: ["campus", "edificios", "aulas"],
  },
];

const SECTIONS = {
  muebles: {
    title: "Muebles",
    searchPlaceholder: "Buscar tipo de activo por nombre....",
    emptyMessage: "No hay tipos de activos para mostrar",
    countLabel: "activos registrados",
  },
  vehiculos: {
    title: "Vehículos",
    searchPlaceholder: "Buscar tipo de activo por nombre....",
    emptyMessage: "No hay tipos de activos para mostrar",
    countLabel: "activos registrados",
  },
  campus: {
    title: "Campus",
    searchPlaceholder: "Buscar campus por nombre...",
    emptyMessage: "No hay campus para mostrar",
    countLabel: "edificios",
  },
  edificios: {
    title: "Edificios",
    searchPlaceholder: "Buscar edificio por nombre...",
    emptyMessage: "No hay edificios para mostrar",
    countLabel: "aulas",
  },
  aulas: {
    title: "Aulas",
    searchPlaceholder: "Buscar aula por nombre...",
    emptyMessage: "No hay aulas para mostrar",
    countLabel: "registros",
  },
};

function mapCampusItems(campus = [], edificios = [], espacios = []) {
  const edifPorCampus = {};
  const aulasPorCampus = {};
  (edificios ?? []).forEach((e) => {
    const cid = e.campus?.id;
    if (cid) edifPorCampus[cid] = (edifPorCampus[cid] ?? 0) + 1;
  });
  (espacios ?? []).forEach((s) => {
    const cid = s.edificio?.campus?.id;
    if (cid) aulasPorCampus[cid] = (aulasPorCampus[cid] ?? 0) + 1;
  });
  return (campus ?? []).map((c) => ({
    id: c.id,
    nombre: c.nombre ?? c.name,
    descripcion: c.descripcion,
    edificios: edifPorCampus[c.id] ?? 0,
    aulas: aulasPorCampus[c.id] ?? 0,
  }));
}

function mapEdificioItems(edificios = [], espacios = []) {
  const aulasPorEdificio = {};
  (espacios ?? []).forEach((s) => {
    const eid = s.edificio?.id;
    if (eid) aulasPorEdificio[eid] = (aulasPorEdificio[eid] ?? 0) + 1;
  });
  return (edificios ?? []).map((e) => ({
    id: e.id,
    nombre: e.nombre ?? e.name,
    campus: e.campus?.nombre,
    idCampus: e.campus?.id,
    edificios: 0,
    aulas: aulasPorEdificio[e.id] ?? 0,
  }));
}

function mapAulaItems(espacios = []) {
  return (espacios ?? []).map((s) => ({
    id: s.id,
    nombre: s.nombreEspacio ?? s.nombre,
    edificio: s.edificio?.nombre,
    idEdificio: s.edificio?.id,
    campus: s.edificio?.campus?.nombre,
    aulas: 0,
  }));
}

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
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [items, setItems] = useState(() => getCached(`catalog-${subTab}`)?.items ?? []);
  const [campusList, setCampusList] = useState([]);
  const [edificiosList, setEdificiosList] = useState([]);

  // Pagination states for all views
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const pageSize = 10;

  const currentMain = MAIN_TABS.find((t) => t.id === mainTab);
  const currentSection = subTab;
  const config = SECTIONS[currentSection];
  const isLocations = mainTab === "ubicaciones";

  const hasLocations = items.length > 0;

  const tiposActivosItems = useMemo(() => {
    const activos = items.filter((t) => t.esActivo !== false);
    if (subTab === "muebles") {
      return activos.filter((t) => t.tipoBien === "Mueble");
    }

    if (subTab === "vehiculos") {
      return activos.filter((t) => t.tipoBien === "Inmueble");
    }

    return [];
  }, [items, subTab]);

  useEffect(() => {
    if (mainTab === "ubicaciones") {
      cargarUbicaciones();
    } else if (mainTab === "tipos-activos") {
      cargarTiposActivos();
    }
  }, [mainTab, subTab, currentPage]);

  const cargarUbicaciones = async () => {
    const ck = `catalog-${subTab}-p${currentPage}`;
    const hasCached = !!getCached(ck);
    if (hasCached) {
      const c = getCached(ck);
      setItems(c.items);
      setTotalPages(c.totalPages);
      setTotalElements(c.totalElements);
    }
    if (!hasCached) setLoading(true);
    setError(null);
    try {
      const page = currentPage - 1;
      let res;
      let mapped = [];

      if (subTab === "campus") {
        const [campusRes, edificiosRes, espaciosRes] = await Promise.all([
          ubicacionesApi.getCampus(page, pageSize),
          ubicacionesApi.getEdificios(0, 500),
          ubicacionesApi.getEspacios(0, 500),
        ]);
        res = campusRes;
        const edificios = edificiosRes?.data?.content ?? edificiosRes?.data ?? [];
        const espacios = espaciosRes?.data?.content ?? espaciosRes?.data ?? [];
        mapped = mapCampusItems(campusRes?.data?.content ?? [], edificios, espacios);
      } else if (subTab === "edificios") {
        const [edificiosRes, espaciosRes] = await Promise.all([
          ubicacionesApi.getEdificios(page, pageSize),
          ubicacionesApi.getEspacios(0, 500),
        ]);
        res = edificiosRes;
        const espacios = espaciosRes?.data?.content ?? espaciosRes?.data ?? [];
        mapped = mapEdificioItems(edificiosRes?.data?.content ?? [], espacios);
      } else if (subTab === "aulas") {
        res = await ubicacionesApi.getEspacios(page, pageSize);
        mapped = mapAulaItems(res?.data?.content ?? []);
      }

      setItems(mapped);
      const tp = res?.data?.totalPages ?? 0;
      const te = res?.data?.totalElements ?? 0;
      setTotalPages(tp);
      setTotalElements(te);
      setCache(ck, { items: mapped, totalPages: tp, totalElements: te });

      // Cargar listas auxiliares para los modales
      if (subTab === "edificios" || subTab === "aulas" || subTab === "campus") {
        ubicacionesApi.getCampus(0, 200).then((r) => setCampusList(r?.data?.content ?? []));
      }
      if (subTab === "aulas" || subTab === "campus") {
        ubicacionesApi.getEdificios(0, 500).then((r) => setEdificiosList(r?.data?.content ?? r?.data ?? []));
      }
    } catch (err) {
      setError(err?.message ?? "Error al cargar ubicaciones");
    } finally {
      setLoading(false);
    }
  };

  const refreshLocations = () => {
    cargarUbicaciones();
  };

  const cargarTiposActivos = async () => {
    const ck = `catalog-tipos-p${currentPage}`;
    const hasCached = !!getCached(ck);
    if (hasCached) {
      const c = getCached(ck);
      setItems(c.items);
      setTotalPages(c.totalPages);
      setTotalElements(c.totalElements);
    }
    if (!hasCached) {
      setLoading(true);
      setItems([]);
    }
    setError(null);
    try {
      const res = await tipoActivosApi.getTipoActivos(
        currentPage - 1,
        pageSize,
      );
      const content = res?.data?.content ?? [];
      const tp = res?.data?.totalPages ?? 0;
      const te = res?.data?.totalElements ?? 0;
      setItems(content);
      setTotalPages(tp);
      setTotalElements(te);
      setCache(ck, { items: content, totalPages: tp, totalElements: te });
    } catch (err) {
      if (!hasCached) setError(err?.message ?? "Error al cargar tipos de activos");
    } finally {
      setLoading(false);
    }
  };

  const handleMainTab = (id) => {
    setMainTab(id);
    setCurrentPage(1); 
    const tab = MAIN_TABS.find((t) => t.id === id);
    if (tab?.sub?.[0]) setSubTab(tab.sub[0]);
  };

  const handleNewLocation = () => {
    setEditLocation(null);
    if (!hasLocations) setModalLocationOpen(true);
    else if (subTab === "campus") setModalCampusOpen(true);
    else if (subTab === "edificios") setModalBuildingOpen(true);
    else if (subTab === "aulas") setModalClassroomOpen(true);
  };

  const handleEditLocation = (item) => {
    if (!item) return;
    setEditLocation(item);
    if (subTab === "campus") setModalCampusOpen(true);
    else if (subTab === "edificios") setModalBuildingOpen(true);
    else if (subTab === "aulas") setModalClassroomOpen(true);
  };

  const handleEditTipoActivo = (item) => {
    if (!item) return;
    setEditTipoActivo(item);
    setModalTipoActivoOpen(true);
  };

  const handleDeleteTipoActivo = (item) => setConfirmDeleteTipoActivo(item);

  const handleConfirmDeleteTipoActivo = async () => {
    if (!confirmDeleteTipoActivo?.id) return;
    try {
      await tipoActivosApi.toggleStatusTipoActivo(confirmDeleteTipoActivo.id);
      toast.success("Tipo de activo eliminado correctamente");
      setConfirmDeleteTipoActivo(null);
      cargarTiposActivos();
    } catch (err) {
      toast.error(err?.message ?? "Error al eliminar");
    }
  };

  const handleGuardarCampus = async (data) => {
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
  };

  const handleGuardarEdificio = async (data) => {
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
  };

  const handleDeleteLocation = (item) => setConfirmDeleteLocation(item);

  const handleConfirmDeleteLocation = async () => {
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
  };

  const getDeleteMessage = () => {
    if (!confirmDeleteLocation) return "";
    const nombre = confirmDeleteLocation.nombre ?? "este elemento";
    if (subTab === "campus")
      return `Se eliminará el campus "${nombre}". Esta acción no se puede deshacer.`;
    if (subTab === "edificios")
      return `Se eliminará el edificio "${nombre}". Esta acción no se puede deshacer.`;
    if (subTab === "aulas")
      return `Se eliminará el aula "${nombre}". Esta acción no se puede deshacer.`;
    return `Se eliminará ${nombre}. Esta acción no se puede deshacer.`;
  };

  const handleGuardarAula = async (data) => {
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
  };

  return (
    <div className="catalogs-page">
      <PageHeader
        overline="GESTIÓN DE CATÁLOGOS"
        title="Catálogos del Sistema"
        subtitle="Administra tipos de activos y ubicaciones"
      />

      <div className="catalogs-card">
        <div className="catalogs-card__header">
          <div className="catalogs-tabs__main">
            {MAIN_TABS.map((tab) => (
              <button
                key={tab.id}
                type="button"
                className={`catalogs-tabs__btn ${mainTab === tab.id ? "catalogs-tabs__btn--active" : ""}`}
                onClick={() => handleMainTab(tab.id)}
              >
                {tab.label}
              </button>
            ))}
          </div>
        </div>
        <div className="catalogs-card__toolbar">
          <div className="catalogs-card__buscador">
            <Buscador
              placeholder={config.searchPlaceholder}
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              aria-label={`Buscar ${currentSection}`}
            />
          </div>
          <div className="catalogs-card__selector-wrap">
            <Select
              value={subTab}
              onChange={(val) => {
                setSubTab(val);
                setCurrentPage(1); // Reset page on subtab change
              }}
              options={(currentMain?.sub ?? []).map((subId) => ({
                value: subId,
                label: SECTIONS[subId]?.title ?? subId,
              }))}
              placeholder="Seleccionar categoría"
              variant="ghost"
              size="sm"
              className="catalogs-card__selector"
            />
          </div>
          <div className="catalogs-card__actions">
            <Button
              variant="primary"
              iconLeft={GenericPlus}
              iconSize={30}
              onClick={() =>
                mainTab === "tipos-activos"
                  ? (setEditTipoActivo(null), setModalTipoActivoOpen(true))
                  : isLocations
                    ? handleNewLocation()
                    : null
              }
            >
              Nuevo
            </Button>
          </div>
        </div>
      </div>

      <RegisterTipoActivoModal
        open={modalTipoActivoOpen}
        onClose={() => {
          setModalTipoActivoOpen(false);
          setEditTipoActivo(null);
        }}
        initialData={editTipoActivo}
        onGuardar={async (data) => {
          try {
            if (editTipoActivo?.id) {
              await tipoActivosApi.actualizarTipoActivo(editTipoActivo.id, data);
              toast.success("Tipo de activo actualizado correctamente");
            } else {
              await tipoActivosApi.crearTipoActivo(data);
              toast.success("Tipo de activo guardado correctamente");
            }
            await cargarTiposActivos();
            setModalTipoActivoOpen(false);
            setEditTipoActivo(null);
          } catch (error) {
            toast.error(error?.message ?? "Error al guardar tipo de activo");
            throw error;
          }
        }}
      />

      <RegisterLocationModal
        open={modalLocationOpen}
        onClose={() => setModalLocationOpen(false)}
        onGuardar={async (data) => {
          try {
            const campusRes = await ubicacionesApi.createCampus({
              nombre: data.campus.trim(),
              descripcion: data.descripcion?.trim() || null,
            });
            const campusId = campusRes?.data?.id;
            if (!campusId) throw new Error("No se obtuvo el campus creado");
            const edificioRes = await ubicacionesApi.createEdificio({
              idCampus: campusId,
              nombre: data.edificio.trim(),
            });
            const edificioId = edificioRes?.data?.id;
            if (!edificioId) throw new Error("No se obtuvo el edificio creado");
            await ubicacionesApi.createEspacio({
              idEdificio: edificioId,
              nombreEspacio: data.aula.trim(),
            });
            toast.success("Ubicación registrada correctamente");
            refreshLocations();
          } catch (err) {
            toast.error(err?.message ?? "Error al guardar");
            throw err;
          }
          setModalLocationOpen(false);
        }}
      />
      <RegisterCampusModal
        open={modalCampusOpen}
        onClose={() => {
          setModalCampusOpen(false);
          setEditLocation(null);
        }}
        initialData={subTab === "campus" ? editLocation : undefined}
        onGuardar={async (data) => {
          await handleGuardarCampus(data);
          setModalCampusOpen(false);
          setEditLocation(null);
        }}
      />
      <RegisterBuildingModal
        open={modalBuildingOpen}
        onClose={() => {
          setModalBuildingOpen(false);
          setEditLocation(null);
        }}
        campus={campusList}
        initialData={
          subTab === "edificios"
            ? (items.find((e) => e.id === editLocation?.id) ?? editLocation)
            : undefined
        }
        onGuardar={async (data) => {
          await handleGuardarEdificio(data);
          setModalBuildingOpen(false);
          setEditLocation(null);
        }}
      />
      <RegisterClassroomModal
        open={modalClassroomOpen}
        onClose={() => {
          setModalClassroomOpen(false);
          setEditLocation(null);
        }}
        edificios={edificiosList}
        initialData={
          subTab === "aulas"
            ? (items.find((s) => s.id === editLocation?.id) ?? editLocation)
            : undefined
        }
        onGuardar={async (data) => {
          await handleGuardarAula(data);
          setModalClassroomOpen(false);
          setEditLocation(null);
        }}
      />

      <ConfirmDeleteModal
        open={!!confirmDeleteLocation}
        onClose={() => setConfirmDeleteLocation(null)}
        onConfirm={handleConfirmDeleteLocation}
        message={getDeleteMessage()}
      />

      <ConfirmDeleteModal
        open={!!confirmDeleteTipoActivo}
        onClose={() => setConfirmDeleteTipoActivo(null)}
        onConfirm={handleConfirmDeleteTipoActivo}
        title="¿Confirmar eliminación?"
        message={
          confirmDeleteTipoActivo
            ? `Se eliminará el tipo de activo "${confirmDeleteTipoActivo.nombre ?? "este elemento"}". Esta acción no se puede deshacer.`
            : ""
        }
      />

      {error && (
        <ErrorBanner message={error} onDismiss={() => setError(null)} />
      )}

      <CatalogSection
        showToolbar={false}
        searchPlaceholder={config.searchPlaceholder}
        emptyMessage={config.emptyMessage}
        sectionKey={currentSection}
        loadingMessage={mainTab === "tipos-activos" ? "Cargando tipos de activos…" : "Cargando ubicaciones…"}
        items={isLocations ? items : tiposActivosItems}
        loading={loading}
        search={search}
        onSearchChange={setSearch}
        countLabel={config.countLabel}
        onEdit={
          mainTab === "tipos-activos"
            ? handleEditTipoActivo
            : isLocations && hasLocations
              ? handleEditLocation
              : undefined
        }
        onDelete={
          mainTab === "tipos-activos"
            ? handleDeleteTipoActivo
            : isLocations && hasLocations
              ? handleDeleteLocation
              : undefined
        }
        // Configuración de paginación híbrida
        serverPagination={isLocations} // Solo ubicaciones maneja el total real desde el servidor
        currentPage={currentPage}
        totalPages={totalPages}
        totalElements={totalElements}
        onPageChange={setCurrentPage}
      />
    </div>
  );
}
