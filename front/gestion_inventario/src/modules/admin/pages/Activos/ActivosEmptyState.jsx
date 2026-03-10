import fantasmitaSvg from "../../../../assets/fantasmita.svg";
import "./ActivosEmptyState.css";

export default function ActivosEmptyState({ message = "No hay activos para mostrar", hasSearch = false }) {
  const texto = hasSearch ? "No hay activos o no coinciden con la búsqueda." : message;
  return (
    <div className="activos-empty" role="status" aria-live="polite">
      <p className="activos-empty__text">{texto}</p>
      <div className="activos-empty__illustration">
        <img src={fantasmitaSvg} alt="" width={190} height={190} aria-hidden />
      </div>
    </div>
  );
}
