import { useState } from "react";
import Modal from "../../../../components/Modal/Modal";
import Input from "../../../../components/Input/Input";
import Button from "../../../../components/Button/Button";
import { X } from "lucide-react";
import { ControlsChevronDown, FilesSave } from "@heathmont/moon-icons";
import Icon from "../../../../components/Icon/Icon";
import "./NewUserModal.css";

export default function NewUserModal({ open, onClose, onGuardar }) {

  const [form, setForm] = useState({
    nombre: "",
    correo: "",
    nacimiento: "",
    curp: "",
    rol: "",
    area: "",
    telefono: "",
  });

  const handleChange = (field) => (e) => {
    setForm((prev) => ({ ...prev, [field]: e.target.value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onGuardar?.(form);

    setForm({
      nombre: "",
      correo: "",
      nacimiento: "",
      curp: "",
      rol: "",
      area: "",
      telefono: "",
    });

    onClose?.();
  };

  const handleClose = () => {
    setForm({
      nombre: "",
      correo: "",
      nacimiento: "",
      curp: "",
      rol: "",
      area: "",
      telefono: "",
    });

    onClose?.();
  };

  return (
    <Modal open={open} onClose={handleClose} className="nuevo-usuario-modal">

      <div className="nuevo-usuario-modal__inner">

        <header className="nuevo-usuario-modal__header">
          <div>
            <h2 className="nuevo-usuario-modal__title">
              Registrar Nuevo Usuario
            </h2>

            <p className="nuevo-usuario-modal__subtitle">
              Crea una nueva cuenta de acceso y perfil de empleado
            </p>
          </div>

          <button
            type="button"
            className="nuevo-usuario-modal__close"
            onClick={handleClose}
            aria-label="Cerrar"
          >
            <X size={30} strokeWidth={2.5} />
          </button>
        </header>

        <form onSubmit={handleSubmit} className="nuevo-usuario-modal__form">

          {/* Nombre */}
          <div className="nuevo-usuario-modal__field">
            <label className="nuevo-usuario-modal__label">Nombre Completo*</label>

            <Input
              placeholder="Nombre completo"
              value={form.nombre}
              onChange={handleChange("nombre")}
              className="nuevo-usuario-modal__input"
            />
          </div>

          {/* Correo + Fecha nacimiento */}
          <div className="nuevo-usuario-modal__row">

            <div className="nuevo-usuario-modal__field nuevo-usuario-modal__field--flex">
              <label className="nuevo-usuario-modal__label">
                Correo Electrónico*
              </label>

              <Input
                type="email"
                placeholder="usuario@institucion.edu.mx"
                value={form.correo}
                onChange={handleChange("correo")}
                className="nuevo-usuario-modal__input"
              />
            </div>

            <div className="nuevo-usuario-modal__field nuevo-usuario-modal__field--flex">
              <label className="nuevo-usuario-modal__label">
                Fecha de Nacimiento*
              </label>

              <Input
                type="date"
                value={form.nacimiento}
                onChange={handleChange("nacimiento")}
                className="nuevo-usuario-modal__input"
              />
            </div>

          </div>

          {/* CURP */}
          <div className="nuevo-usuario-modal__field">
            <label className="nuevo-usuario-modal__label">CURP*</label>

            <Input
                placeholder="Ingresa la CURP completa"
                value={form.curp}
                maxLength={18}
                onChange={(e) =>
                handleChange("curp")({
                    target: {
                    value: e.target.value.replace(/[^A-Za-z0-9]/g, "").toUpperCase()
                    }
                })
                }
                className="nuevo-usuario-modal__input"
            />
          </div>

          {/* Rol + Área */}
          <div className="nuevo-usuario-modal__row">

            <div className="nuevo-usuario-modal__field nuevo-usuario-modal__field--flex">
              <label className="nuevo-usuario-modal__label">Rol*</label>

              <div className="nuevo-usuario-modal__select-wrap">

                <select
                  value={form.rol}
                  onChange={handleChange("rol")}
                  className="nuevo-usuario-modal__select"
                  required
                >
                  <option value="">Seleccionar...</option>
                  <option value="Administrador">Administrador</option>
                  <option value="Técnico">Técnico</option>
                  <option value="Usuario">Usuario</option>
                </select>

                <Icon
                  icon={ControlsChevronDown}
                  size={30}
                  className="nuevo-usuario-modal__select-icon"
                  aria-hidden
                />

              </div>
            </div>

            <div className="nuevo-usuario-modal__field nuevo-usuario-modal__field--flex">
              <label className="nuevo-usuario-modal__label">Área*</label>

              <Input
                placeholder="Área del usuario"
                value={form.area}
                onChange={handleChange("area")}
                className="nuevo-usuario-modal__input"
              />

            </div>

          </div>

          <footer className="nuevo-usuario-modal__footer">

            <Button
              type="button"
              variant="secondary"
              size="small"
              onClick={handleClose}
            >
              Cancelar
            </Button>

            <Button
              type="submit"
              variant="primary"
              size="small"
              iconLeft={FilesSave}
            >
              Guardar Usuario
            </Button>

          </footer>

        </form>
      </div>

    </Modal>
  );
}