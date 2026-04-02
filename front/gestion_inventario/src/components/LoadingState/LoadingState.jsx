import cargandoSvg from "../../assets/cargando.svg";
import "./LoadingState.css";

export default function LoadingState({ message = "Cargando…" }) {
  return (
    <div
      className="loading-state d-flex flex-column align-items-center justify-content-center gap-4 w-100 text-center py-4 px-5"
      role="status"
      aria-live="polite"
      aria-busy="true"
    >
      <p className="loading-state__text order-1">{message}</p>
      <div className="loading-state__illustration order-2 d-flex align-items-center justify-content-center flex-shrink-0">
        <img
          src={cargandoSvg}
          alt=""
          width={190}
          height={190}
          aria-hidden
          className="loading-state__img"
        />
      </div>
    </div>
  );
}
