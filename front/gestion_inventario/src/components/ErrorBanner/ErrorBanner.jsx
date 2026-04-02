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
      className={`error-banner d-flex align-items-start gap-3 mb-3 p-3 rounded-3 ${className}`}
      role="alert"
      aria-live="polite"
    >
      <div className="error-banner__icon flex-shrink-0">
        <AlertCircle size={20} strokeWidth={2.5} />
      </div>
      <p className="error-banner__message mb-0 flex-grow-1 fw-medium">{message}</p>
      {onDismiss && (
        <button
          type="button"
          className="error-banner__dismiss flex-shrink-0"
          onClick={onDismiss}
          aria-label="Cerrar mensaje de error"
        >
          ×
        </button>
      )}
    </div>
  );
}
