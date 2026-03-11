import Button from "../../../../components/Button/Button";
import { GenericPlus } from "@heathmont/moon-icons";
import fantasmitaSvg from "../../../../assets/fantasmita.svg";
import "./CatalogEmptyState.css";

export default function CatalogEmptyState({
  message = "No hay elementos para mostrar",
  hasSearch = false,
  actionLabel,
  onAction,
}) {
  const texto = hasSearch ? "No hay resultados o no coinciden con la búsqueda." : message;
  const showAction = !hasSearch && actionLabel && onAction;
  return (
    <div className="catalog-empty" role="status" aria-live="polite">
      <p className="catalog-empty__text">{texto}</p>
      {showAction && (
        <Button
          variant="primary"
          iconLeft={GenericPlus}
          iconSize={24}
          onClick={onAction}
          className="catalog-empty__action"
        >
          {actionLabel}
        </Button>
      )}
      <div className="catalog-empty__illustration">
        <img src={fantasmitaSvg} alt="" width={190} height={190} aria-hidden />
      </div>
    </div>
  );
}
