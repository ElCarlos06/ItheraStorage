import Buscador from "../../../../../components/Buscador/Buscador";
import Button from "../../../../../components/Button/Button";
import Select from "../../../../../components/Select/Select";
import { GenericPlus } from "@heathmont/moon-icons";
import { MAIN_TABS, SECTIONS } from "../constants/catalogConfig";

export default function CatalogToolbar({
  mainTab,
  subTab,
  search,
  onSearchChange,
  onMainTabChange,
  onSubTabChange,
  onPageReset,
  onNuevoClick,
  isLocations,
  hasLocations,
}) {
  const currentMain = MAIN_TABS.find((t) => t.id === mainTab);
  const config = SECTIONS[subTab] ?? {};

  const handleSubTabChange = (val) => {
    onSubTabChange?.(val);
    onPageReset?.();
  };

  return (
    <div className="catalogs-card d-flex flex-row align-items-center gap-3 flex-wrap p-4 mb-4">
      <div className="catalogs-card__buscador flex-grow-1 min-w-0">
        <Buscador
          placeholder={config.searchPlaceholder}
          value={search}
          onChange={(e) => onSearchChange?.(e.target.value)}
          aria-label={`Buscar ${subTab}`}
        />
      </div>
      <div className="catalogs-tabs__main d-flex gap-3 flex-shrink-0 flex-wrap">
        {MAIN_TABS.map((tab) => (
          <button
            key={tab.id}
            type="button"
            className={`catalogs-tabs__btn ${mainTab === tab.id ? "catalogs-tabs__btn--active" : ""}`}
            title={`Ver catálogo: ${tab.label}`}
            onClick={() => onMainTabChange?.(tab.id)}
          >
            {tab.label}
          </button>
        ))}
      </div>
      <div className="catalogs-card__selector-wrap">
        <Select
          value={subTab}
          onChange={handleSubTabChange}
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
      <div className="catalogs-card__actions flex-shrink-0 ms-auto">
        <Button
          variant="primary"
          iconLeft={GenericPlus}
          iconSize={30}
          title={
            config?.title
              ? `Agregar nuevo registro en ${config.title}`
              : "Agregar nuevo registro en esta categoría"
          }
          onClick={onNuevoClick}
        >
          Nuevo
        </Button>
      </div>
    </div>
  );
}
