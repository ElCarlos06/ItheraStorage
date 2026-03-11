import cargandoSvg from "../../assets/cargando.svg";
import "./LoadingState.css";

export default function LoadingState({ message = "Cargando…" }) {
  return (
    <div className="loading-state" role="status" aria-live="polite" aria-busy="true">
      <p className="loading-state__text">{message}</p>
      <div className="loading-state__illustration">
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
