import { AlertCircle } from "lucide-react";
import guardadoSvg from "../../assets/guardado.svg";
import "./ToastWithProgress.css";

const ICONS = {
  success: guardadoSvg,
  error: null,
};

export default function ToastWithProgress({ message, type = "success", duration = 3000 }) {
  return (
    <div className={`toast-progress toast-progress--${type}`} role="status" aria-live="polite">
      <div className="toast-progress__inner">
        <div className="toast-progress__icon">
          {type === "error" ? (
            <AlertCircle size={48} strokeWidth={2} aria-hidden />
          ) : (
            <img src={ICONS[type] || guardadoSvg} alt="" width={48} height={48} aria-hidden />
          )}
        </div>
        <p className="toast-progress__message">{message}</p>
      </div>
      <div
        className="toast-progress__bar-wrap"
        style={{ "--toast-duration": `${duration}ms` }}
      >
        <div className="toast-progress__bar" />
      </div>
    </div>
  );
}
