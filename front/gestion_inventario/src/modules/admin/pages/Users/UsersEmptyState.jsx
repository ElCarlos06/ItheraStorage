import fantasmitaSvg from "../../../../assets/fantasmita.svg";
import "./UsersEmptyState.css";

export default function UsersEmptyState({ message = "No hay usuarios para mostrar", hasSearch = false }) {

  const texto = hasSearch
    ? "No hay usuarios o no coinciden con la búsqueda."
    : message;

  return (
    <div className="users-empty" role="status" aria-live="polite">
      <p className="users-empty__text">{texto}</p>

      <div className="users-empty__illustration">
        <img
          src={fantasmitaSvg}
          alt=""
          width={190}
          height={190}
          aria-hidden
        />
      </div>
    </div>
  );
}