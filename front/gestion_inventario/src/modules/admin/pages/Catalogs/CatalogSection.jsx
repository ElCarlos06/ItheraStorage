import { useState, useMemo } from "react";
import Buscador from "../../../../components/Buscador/Buscador";
import Button from "../../../../components/Button/Button";
import Icon from "../../../../components/Icon/Icon";
import { GenericPlus, TravelHotel, ControlsChevronRight } from "@heathmont/moon-icons";
import CatalogEmptyState from "./CatalogEmptyState";
import "./CatalogSection.css";

export default function CatalogSection({
  showToolbar = true,
  title = "Catálogos del Sistema",
  searchPlaceholder = "Buscar...",
  emptyMessage = "No hay elementos para mostrar",
  sectionKey = "campus",
  items = [],
  search: searchProp = "",
  onSearchChange,
  countLabel = "registros",
  onNew,
  emptyActionLabel,
  onEmptyAction,
  onEdit,
}) {
  const [internalSearch, setInternalSearch] = useState("");
  const search = onSearchChange ? (searchProp ?? "") : internalSearch;
  const handleSearchChange = (e) => {
    const val = e?.target?.value ?? "";
    if (onSearchChange) onSearchChange(val);
    else setInternalSearch(val);
  };

  const filtered = useMemo(() => {
    const list = Array.isArray(items) ? items : [];
    const q = (search ?? "").trim().toLowerCase();
    if (!q) return list;
    return list.filter((item) => {
      const name = (item.nombre ?? item.name ?? "").toLowerCase();
      return name.includes(q);
    });
  }, [items, search]);

  const showEmptyState = filtered.length === 0;

  return (
    <div className={`catalog-section ${showEmptyState ? "catalog-section--empty" : ""}`}>
      <section className="catalog-section__view" aria-label={title}>
        {showToolbar && (
          <div className="catalog-section__toolbar">
            <div className="catalog-section__buscador">
              <Buscador
                placeholder={searchPlaceholder}
                value={search}
                onChange={handleSearchChange}
                aria-label={`Buscar ${sectionKey}`}
              />
            </div>
            <div className="catalog-section__actions">
              <Button variant="primary" iconLeft={GenericPlus} iconSize={30} onClick={() => onNew?.()}>
                Nuevo
              </Button>
            </div>
          </div>
        )}

        <div className="catalog-section__list">
          {showEmptyState ? (
            <CatalogEmptyState
              message={emptyMessage}
              hasSearch={!!(search ?? "").trim()}
              actionLabel={emptyActionLabel}
              onAction={onEmptyAction}
            />
          ) : (
            filtered.map((item) => {
              const nombre = item.nombre ?? item.name ?? "—";
              const cantidad = item.cantidad ?? item.count ?? item.edificios ?? item.aulas ?? 0;
              const subtitle = `${cantidad} ${countLabel}`;
              return (
<div key={item.id ?? nombre} className="catalog-section__card-wrap">
                <div
                  className="catalog-section__card"
                  role="button"
                  tabIndex={0}
                  onClick={() => onEdit?.(item)}
                  onKeyDown={(e) => e.key === "Enter" && onEdit?.(item)}
                >
                    <div className="catalog-section__card-icon">
                      <Icon icon={TravelHotel} size={32} />
                    </div>
                    <div className="catalog-section__card-content">
                      <p className="catalog-section__card-title">{nombre}</p>
                      <p className="catalog-section__card-subtitle">{subtitle}</p>
                    </div>
                    <div className="catalog-section__card-cantidad">
                      <p className="catalog-section__card-cantidad-label">Cantidad</p>
                      <p className="catalog-section__card-cantidad-value">{cantidad}</p>
                    </div>
                    <div className="catalog-section__card-chevron" aria-hidden>
                      <Icon icon={ControlsChevronRight} size={20} />
                    </div>
                  </div>
                </div>
              );
            })
          )}
        </div>
      </section>
    </div>
  );
}
