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
    <div className="catalogs-card">
      <div className="catalogs-card__header">
        <div className="catalogs-tabs__main">
          {MAIN_TABS.map((tab) => (
            <button
              key={tab.id}
              type="button"
              className={`catalogs-tabs__btn ${mainTab === tab.id ? "catalogs-tabs__btn--active" : ""}`}
              onClick={() => onMainTabChange?.(tab.id)}
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
            onChange={(e) => onSearchChange?.(e.target.value)}
            aria-label={`Buscar ${subTab}`}
          />
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
        <div className="catalogs-card__actions">
          <Button
            variant="primary"
            iconLeft={GenericPlus}
            iconSize={30}
            onClick={onNuevoClick}
          >
            Nuevo
          </Button>
        </div>
      </div>
    </div>
  );
}
