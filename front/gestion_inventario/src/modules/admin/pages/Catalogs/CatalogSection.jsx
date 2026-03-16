import { useState, useMemo } from "react";
import Buscador from "../../../../components/Buscador/Buscador";
import Button from "../../../../components/Button/Button";
import Icon from "../../../../components/Icon/Icon";
import {
  GenericPlus,
  TravelHotel,
  ControlsChevronRight,
  GenericDelete,
  GenericEdit,
} from "@heathmont/moon-icons";
import { getTipoActivoIcon } from "../../../../utils/tipoActivoIcons";
import EmptyState from "../../../../components/EmptyState/EmptyState";
import Pagination from "../../components/layout/Pagination";
import LoadingState from "../../../../components/LoadingState/LoadingState";
import "./CatalogSection.css";

export default function CatalogSection({
  showToolbar = true,
  title = "Catálogos del Sistema",
  searchPlaceholder = "Buscar...",
  emptyMessage = "No hay elementos para mostrar",
  sectionKey = "campus",
  items = [],
  loading = false,
  search: searchProp = "",
  onSearchChange,
  countLabel = "registros",
  onNew,
  emptyActionLabel,
  onEmptyAction,
  onEdit,
  onDelete,
  loadingMessage,
  // Props para paginación del servidor (opcionales)
  serverPagination = false,
  currentPage: serverCurrentPage,
  totalPages: serverTotalPages,
  totalElements: serverTotalElements,
  onPageChange: onServerPageChange,
}) {
  const [internalSearch, setInternalSearch] = useState("");
  const [internalPage, setInternalPage] = useState(1);
  const itemsPerPage = 10;

  const search = onSearchChange ? (searchProp ?? "") : internalSearch;
  const handleSearchChange = (e) => {
    const val = e?.target?.value ?? "";
    if (onSearchChange) onSearchChange(val);
    else setInternalSearch(val);
    setInternalPage(1); // Reset local page on search
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

  // Decidimos qué valores de paginación usar
  const currentPage = serverPagination ? serverCurrentPage : internalPage;
  const totalItems = serverPagination ? serverTotalElements : filtered.length;
  const totalPages = serverPagination ? serverTotalPages : Math.ceil(filtered.length / itemsPerPage);

  const paginatedItems = useMemo(() => {
    if (serverPagination) return filtered; // El servidor ya mandó solo 10
    const startIndex = (internalPage - 1) * itemsPerPage;
    return filtered.slice(startIndex, startIndex + itemsPerPage);
  }, [serverPagination, filtered, internalPage, itemsPerPage]);

  const handlePageChange = (page) => {
    if (serverPagination) {
      onServerPageChange?.(page);
    } else {
      setInternalPage(page);
    }
    window.scrollTo(0, 0);
  };

  const showEmptyState = filtered.length === 0;

  return (
    <div
      className={`catalog-section ${showEmptyState && !loading ? "catalog-section--empty" : ""} ${loading ? "catalog-section--loading" : ""}`}
    >
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
              <Button
                variant="primary"
                iconLeft={GenericPlus}
                iconSize={30}
                onClick={() => onNew?.()}
              >
                Nuevo
              </Button>
            </div>
          </div>
        )}

        <div className="catalog-section__list">
          {loading ? (
            <div className="catalog-section__list-loading">
              <LoadingState
                message={loadingMessage ?? (sectionKey === "muebles" || sectionKey === "vehiculos" ? "Cargando tipos de activos…" : "Cargando ubicaciones…")}
              />
            </div>
          ) : showEmptyState ? (
            <EmptyState
              message={emptyMessage}
              hasSearch={!!(search ?? "").trim()}
              searchMessage="No hay resultados o no coinciden con la búsqueda."
              actionLabel={emptyActionLabel}
              onAction={onEmptyAction}
            />
          ) : (
            paginatedItems.map((item) => {
              const nombre = item.nombre ?? item.name ?? "—";
              const edificiosCount = item.edificios ?? 0;
              const aulasCount = item.aulas ?? 0;
              const campus = item.campus ?? "—";
              const edificio = item.edificio ?? "—";

              let subtitle;
              let col1Label, col1Value, col2Label, col2Value;
              if (sectionKey === "campus") {
                subtitle = item.descripcion ?? "—";
                col1Label = "Edificios";
                col1Value = edificiosCount;
                col2Label = "Aulas";
                col2Value = aulasCount;
              } else if (sectionKey === "edificios") {
                subtitle = campus;
                col1Label = "Aulas";
                col1Value = aulasCount;
                col2Label = "Campus";
                col2Value = campus;
              } else if (sectionKey === "aulas") {
                subtitle = edificio;
                col1Label = "Edificio";
                col1Value = edificio;
                col2Label = "Campus";
                col2Value = campus;
              } else {
                subtitle = `${item.cantidad ?? item.count ?? 0} ${countLabel}`;
                col1Label = "Cantidad";
                col1Value = item.cantidad ?? item.count ?? 0;
                col2Label = null;
                col2Value = null;
              }

              const isTipoActivo = sectionKey === "muebles" || sectionKey === "vehiculos";
              const hasActions =
                (onEdit || onDelete) &&
                (sectionKey === "campus" ||
                  sectionKey === "edificios" ||
                  sectionKey === "aulas" ||
                  isTipoActivo);

              const cardIcon = isTipoActivo ? getTipoActivoIcon(item) : TravelHotel;

              return (
                <div
                  key={item.id ?? nombre}
                  className="catalog-section__card-wrap"
                >
                  <div
                    className="catalog-section__card"
                    role="button"
                    tabIndex={0}
                    onClick={() => onEdit?.(item)}
                    onKeyDown={(e) => e.key === "Enter" && onEdit?.(item)}
                  >
                    <div className="catalog-section__card-icon">
                      <Icon icon={cardIcon} size={32} />
                    </div>
                    <div className="catalog-section__card-content">
                      <p className="catalog-section__card-title">{nombre}</p>
                      <p className="catalog-section__card-subtitle">
                        {subtitle}
                      </p>
                    </div>
                    <div className="catalog-section__card-columns">
                      <div className="catalog-section__card-cantidad">
                        <p className="catalog-section__card-cantidad-label">
                          {col1Label}
                        </p>
                        <p className="catalog-section__card-cantidad-value">
                          {col1Value}
                        </p>
                      </div>
                      {col2Label != null && (
                        <div className="catalog-section__card-cantidad">
                          <p className="catalog-section__card-cantidad-label">
                            {col2Label}
                          </p>
                          <p className="catalog-section__card-cantidad-value">
                            {col2Value}
                          </p>
                        </div>
                      )}
                    </div>
                    {hasActions ? (
                      <div
                        className="catalog-section__card-actions"
                        onClick={(e) => e.stopPropagation()}
                      >
                        {onDelete && (
                          <button
                            type="button"
                            className="catalog-section__action-btn catalog-section__action-btn--delete"
                            title="Eliminar"
                            aria-label="Eliminar"
                            onClick={() => onDelete(item)}
                          >
                            <Icon icon={GenericDelete} size={30} />
                          </button>
                        )}
                        {onEdit && (
                          <button
                            type="button"
                            className="catalog-section__action-btn catalog-section__action-btn--edit"
                            title="Editar"
                            aria-label="Editar"
                            onClick={() => onEdit(item)}
                          >
                            <Icon icon={GenericEdit} size={30} />
                          </button>
                        )}
                      </div>
                    ) : null}
                    {!hasActions && (
                      <div
                        className="catalog-section__card-chevron"
                        aria-hidden
                      >
                        <Icon icon={ControlsChevronRight} size={20} />
                      </div>
                    )}
                  </div>
                </div>
              );
            })
          )}
        </div>

        {!loading && !showEmptyState && (
          <div style={{ marginTop: "2rem" }}>
            <Pagination
              currentPage={currentPage}
              totalPages={totalPages}
              totalElements={totalItems}
              pageSize={itemsPerPage}
              onPageChange={handlePageChange}
            />
          </div>
        )}
      </section>
    </div>
  );
}
