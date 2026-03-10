import Modal from "../../../../components/Modal/Modal";
import Button from "../../../../components/Button/Button";
import { X } from "lucide-react";
import "./UserInfoModal.css";

export default function UserInfoModal({ open, onClose, user }) {
  if (!user) return null;

  return (
    <Modal open={open} onClose={onClose} className="user-info-modal">
      <div className="user-info-modal__inner">
        <header className="user-info-modal__header">
          <h2 className="user-info-modal__title">Información del usuario</h2>
          <button
            type="button"
            className="user-info-modal__close"
            onClick={onClose}
            aria-label="Cerrar"
          >
            <X size={24} strokeWidth={2.5} />
          </button>
        </header>

        <div className="user-info-modal__body">
          <div className="user-info-modal__row">
            <span className="user-info-modal__label">Nº Empleado</span>
            <span className="user-info-modal__value">{user.numeroEmpleado ?? "—"}</span>
          </div>
          <div className="user-info-modal__row">
            <span className="user-info-modal__label">Rol</span>
            <span className="user-info-modal__value">{user.rol ?? "—"}</span>
          </div>
          <div className="user-info-modal__row">
            <span className="user-info-modal__label">Nombre completo</span>
            <span className="user-info-modal__value">{user.nombre ?? "—"}</span>
          </div>
          <div className="user-info-modal__row">
            <span className="user-info-modal__label">CURP</span>
            <span className="user-info-modal__value">{user.curp ?? "—"}</span>
          </div>
          <div className="user-info-modal__row">
            <span className="user-info-modal__label">Correo</span>
            <span className="user-info-modal__value">{user.correo ?? "—"}</span>
          </div>
          <div className="user-info-modal__row">
            <span className="user-info-modal__label">Área</span>
            <span className="user-info-modal__value">{user.area ?? "—"}</span>
          </div>
        </div>

        <footer className="user-info-modal__footer">
          <Button variant="outline" onClick={onClose}>
            Cerrar
          </Button>
        </footer>
      </div>
    </Modal>
  );
}
