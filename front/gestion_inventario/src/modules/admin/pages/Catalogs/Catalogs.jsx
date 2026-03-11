import { useState } from "react";
import PageHeader from "../../components/dashboard/PageHeader";
import Buscador from "../../../../components/Buscador/Buscador";
import Button from "../../../../components/Button/Button";
import Select from "../../../../components/Select/Select";
import CatalogSection from "./CatalogSection";
import CatalogEmptyState from "./CatalogEmptyState";
import RegisterTipoActivoModal from "./RegisterTipoActivoModal";
import RegisterLocationModal from "./RegisterLocationModal";
import RegisterCampusModal from "./RegisterCampusModal";
import RegisterBuildingModal from "./RegisterBuildingModal";
import RegisterClassroomModal from "./RegisterClassroomModal";
import { GenericPlus } from "@heathmont/moon-icons";
import "./Catalogs.css";

const MAIN_TABS = [
  { id: "tipos-activos", label: "Tipos de Activos", sub: ["muebles", "vehiculos"] },
  { id: "ubicaciones", label: "Ubicaciones", sub: ["campus", "edificios", "aulas"] },
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

  const currentMain = MAIN_TABS.find((t) => t.id === mainTab);
  const currentSection = subTab;
  const config = SECTIONS[currentSection];
  const isLocations = mainTab === "ubicaciones";
  const locationItems = []; // TODO: conectar con API
  const hasLocations = locationItems.length > 0;

  const handleMainTab = (id) => {
    setMainTab(id);
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
              onChange={setSubTab}
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
                  ? setModalTipoActivoOpen(true)
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
        onClose={() => setModalTipoActivoOpen(false)}
        onGuardar={(data) => {
          setModalTipoActivoOpen(false);
        }}
      />

      <RegisterLocationModal
        open={modalLocationOpen}
        onClose={() => setModalLocationOpen(false)}
        onGuardar={(data) => {
          setModalLocationOpen(false);
        }}
      />
      <RegisterCampusModal
        open={modalCampusOpen}
        onClose={() => {
          setModalCampusOpen(false);
          setEditLocation(null);
        }}
        edificios={[]}
        aulas={[]}
        initialData={subTab === "campus" ? editLocation : undefined}
        onGuardar={(data) => {
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
        campus={[]}
        aulas={[]}
        initialData={subTab === "edificios" ? editLocation : undefined}
        onGuardar={(data) => {
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
        campus={[]}
        edificios={[]}
        initialData={subTab === "aulas" ? editLocation : undefined}
        onGuardar={(data) => {
          setModalClassroomOpen(false);
          setEditLocation(null);
        }}
      />

      <CatalogSection
        showToolbar={false}
        searchPlaceholder={config.searchPlaceholder}
        emptyMessage={config.emptyMessage}
        sectionKey={currentSection}
        items={isLocations ? locationItems : []}
        search={search}
        onSearchChange={setSearch}
        countLabel={config.countLabel}
        onEdit={isLocations && hasLocations ? handleEditLocation : undefined}
      />
    </div>
  );
}
