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
  className = "",
  children,
}) {
  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit?.(e);
  };

  return (
    <Modal open={open} onClose={onClose} className={`form-modal ${className}`.trim()}>
      <div className="form-modal__inner">
        <header className="form-modal__header">
          <div className="form-modal__header-text">
            <h2 className="form-modal__title">{title}</h2>
            {subtitle && <p className="form-modal__subtitle">{subtitle}</p>}
          </div>
          <button
            type="button"
            className="form-modal__close"
            onClick={onClose}
            aria-label="Cerrar"
          >
            <X size={24} strokeWidth={2.5} />
          </button>
        </header>

        <form onSubmit={handleSubmit} className="form-modal__form">
          {children}
          <footer className="form-modal__footer">
            <Button type="button" variant="outline" size="small" onClick={onClose}>
              Cancelar
            </Button>
            <Button
              type="submit"
              variant="primary"
              size="small"
              iconLeft={submitIcon}
              iconSize={submitIconSize}
            >
              {submitLabel}
            </Button>
          </footer>
        </form>
      </div>
    </Modal>
  );
}
