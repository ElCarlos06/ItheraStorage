import { useState } from "react";
import PageHeader from "../../components/dashboard/PageHeader";
import Buscador from "../../../../components/Buscador/Buscador";
import Button from "../../../../components/Button/Button";
import CatalogSection from "./CatalogSection";
import RegisterTipoActivoModal from "./RegisterTipoActivoModal";
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

  const currentMain = MAIN_TABS.find((t) => t.id === mainTab);
  const currentSection = subTab;
  const config = SECTIONS[currentSection];

  const handleMainTab = (id) => {
    setMainTab(id);
    const tab = MAIN_TABS.find((t) => t.id === id);
    if (tab?.sub?.[0]) setSubTab(tab.sub[0]);
  };

  return (
    <div className="catalogs-page">
      <PageHeader
        overline="GESTIÓN DE CATÁLOGOS"
        title="Catálogos del Sistema"
        subtitle="Administra tipos de activos y ubicaciones"
      />

      <div className="catalogs-card">
        <div className="catalogs-card__tabs">
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
          <div className="catalogs-tabs__sub">
            {currentMain?.sub?.map((subId) => (
              <button
                key={subId}
                type="button"
                className={`catalogs-tabs__sub-btn ${subTab === subId ? "catalogs-tabs__sub-btn--active" : ""}`}
                onClick={() => setSubTab(subId)}
              >
                {SECTIONS[subId]?.title ?? subId}
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
          <div className="catalogs-card__actions">
            <Button
              variant="primary"
              iconLeft={GenericPlus}
              onClick={() => mainTab === "tipos-activos" && setModalTipoActivoOpen(true)}
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

      <CatalogSection
        showToolbar={false}
        searchPlaceholder={config.searchPlaceholder}
        emptyMessage={config.emptyMessage}
        sectionKey={currentSection}
        items={[]}
        search={search}
        onSearchChange={setSearch}
        countLabel={config.countLabel}
      />
    </div>
  );
}
