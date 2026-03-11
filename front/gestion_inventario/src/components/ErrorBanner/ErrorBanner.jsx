import { AlertCircle } from "lucide-react";
import "./ErrorBanner.css";

/**
 * Banner de error amigable para mostrar mensajes al usuario.
 * Incluye icono y estilo consistente.
 */
export default function ErrorBanner({ message, onDismiss, className = "" }) {
  if (!message) return null;

  return (
    <div
      className={`error-banner ${className}`}
      role="alert"
      aria-live="polite"
    >
      <div className="error-banner__icon">
        <AlertCircle size={20} strokeWidth={2.5} />
      </div>
      <p className="error-banner__message">{message}</p>
      {onDismiss && (
        <button
          type="button"
          className="error-banner__dismiss"
          onClick={onDismiss}
          aria-label="Cerrar mensaje de error"
        >
          ×
        </button>
      )}
    </div>
  );
}
