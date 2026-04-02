import { X } from "lucide-react";
import Modal from "../Modal/Modal";
import Button from "../Button/Button";
import "./FormModal.css";

export default function FormModal({
  open,
  onClose,
  title,
  subtitle,
  onSubmit,
  submitLabel = "Guardar",
  submitIcon,
  submitIconSize = 30,
  loading = false,
  /** Deshabilita enviar sin mostrar estado de carga (p. ej. formulario inválido o datos pendientes). */
  submitDisabled = false,
  className = "",
  children,
}) {
  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit?.(e);
  };

  return (
    <Modal open={open} onClose={onClose} className={`form-modal ${className}`.trim()}>
      <div className="form-modal__inner d-flex flex-column w-100 min-w-0">
        <header className="form-modal__header d-flex align-items-center justify-content-between">
          <div className="form-modal__header-text flex-grow-1 min-w-0">
            <h2 className="form-modal__title">{title}</h2>
            {subtitle && <p className="form-modal__subtitle">{subtitle}</p>}
          </div>
          <button
            type="button"
            className="form-modal__close d-flex align-items-center justify-content-center p-0 border-0 bg-transparent rounded-3"
            onClick={onClose}
            aria-label="Cerrar"
          >
            <X size={24} strokeWidth={2.5} />
          </button>
        </header>

        <form onSubmit={handleSubmit} className="form-modal__form d-flex flex-column gap-4 flex-grow-1">
          {children}
          <footer className="form-modal__footer d-flex align-items-center justify-content-end gap-3">
            <Button type="button" variant="outline" size="small" onClick={onClose} disabled={loading}>
              Cancelar
            </Button>
            <Button
              type="submit"
              variant="primary"
              size="small"
              iconLeft={submitIcon}
              iconSize={submitIconSize}
              disabled={loading || submitDisabled}
            >
              {loading ? "Guardando…" : submitLabel}
            </Button>
          </footer>
        </form>
      </div>
    </Modal>
  );
}
