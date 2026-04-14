import loadingSvg from "../../assets/loading.svg";
import "./ActionLoader.css";

export default function ActionLoader({ message = "Procesando…" }) {
  return (
    <div
      className="action-loader d-flex flex-column align-items-center justify-content-center gap-4 w-100 text-center py-4 px-5"
      role="status"
      aria-live="polite"
      aria-busy="true"
    >
      <p className="action-loader__text order-1">{message}</p>
      <div className="action-loader__illustration order-2 d-flex align-items-center justify-content-center flex-shrink-0">
        <img
          src={loadingSvg}
          alt=""
        width={44}
        height={44}
          aria-hidden
          className="action-loader__img"
        />
      </div>
    </div>
  );
}
