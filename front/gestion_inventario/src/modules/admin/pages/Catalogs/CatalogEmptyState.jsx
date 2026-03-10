import fantasmitaSvg from "../../../../assets/fantasmita.svg";
import "./CatalogEmptyState.css";

export default function CatalogEmptyState({ message = "No hay elementos para mostrar", hasSearch = false }) {
  const texto = hasSearch ? "No hay resultados o no coinciden con la búsqueda." : message;
  return (
    <div className="catalog-empty" role="status" aria-live="polite">
      <p className="catalog-empty__text">{texto}</p>
      <div className="catalog-empty__illustration">
        <img src={fantasmitaSvg} alt="" width={190} height={190} aria-hidden />
      </div>
    </div>
  );
}
