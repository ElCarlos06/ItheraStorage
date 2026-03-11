import { useEffect } from "react";
import guardadoSvg from "../../assets/guardado.svg";
import "./SuccessToast.css";

export default function SuccessToast({ open, onClose, message = "Guardado correctamente", duration = 3000 }) {
  useEffect(() => {
    if (!open) return;
    const t = setTimeout(() => onClose?.(), duration);
    return () => clearTimeout(t);
  }, [open, duration, onClose]);

  if (!open) return null;

  return (
    <div className="success-toast" role="status" aria-live="polite">
      <div className="success-toast__inner">
        <div className="success-toast__icon">
          <img src={guardadoSvg} alt="" width={48} height={48} aria-hidden />
        </div>
        <p className="success-toast__message">{message}</p>
      </div>
    </div>
  );
}
