import Button from "../Button/Button";
import { GenericPlus } from "@heathmont/moon-icons";
import fantasmitaSvg from "../../assets/fantasmita.svg";
import "./EmptyState.css";

/**
 * Componente unificado para estados vacíos.
 * Reemplaza ActivosEmptyState, UsersEmptyState y CatalogEmptyState.
 */
export default function EmptyState({
  message = "No hay elementos para mostrar",
  hasSearch = false,
  searchMessage,
  actionLabel,
  onAction,
}) {
  const texto = hasSearch
    ? searchMessage ?? "No hay resultados o no coinciden con la búsqueda."
    : message;
  const showAction = !hasSearch && actionLabel && onAction;

  return (
    <div
      className="empty-state d-flex flex-column align-items-center justify-content-center gap-4 w-100 text-center py-4 px-5"
      role="status"
      aria-live="polite"
    >
      <p className="empty-state__text">{texto}</p>
      {showAction && (
        <Button
          variant="primary"
          iconLeft={GenericPlus}
          iconSize={24}
          onClick={onAction}
          className="empty-state__action"
        >
          {actionLabel}
        </Button>
      )}
      <div className="empty-state__illustration d-flex align-items-center justify-content-center flex-shrink-0">
        <img src={fantasmitaSvg} alt="" width={190} height={190} aria-hidden />
      </div>
    </div>
  );
}
