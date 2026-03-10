import { useState, useEffect } from "react";
import Modal from "../../../../components/Modal/Modal";
import Input from "../../../../components/Input/Input";
import Button from "../../../../components/Button/Button";
import { X } from "lucide-react";
import { ControlsChevronDown, FilesSave } from "@heathmont/moon-icons";
import Icon from "../../../../components/Icon/Icon";
import { api } from "../../../../api/client";
import {
  validarCurp,
  validarNombre,
  validarCorreo,
  validarFechaNacimiento,
  validarRol,
  validarArea,
} from "../../../../utils/validaciones";
import "./NewUserModal.css";

export default function NewUserModal({ open, onClose, onGuardar }) {
  const [form, setForm] = useState({
    nombre: "",
    correo: "",
    nacimiento: "",
    curp: "",
    rol: "",
    area: "",
  });
  const [errores, setErrores] = useState({});
  const [loading, setLoading] = useState(false);
  const [roles, setRoles] = useState([]);
  const [areas, setAreas] = useState([]);

  useEffect(() => {
    if (open) {
      api
        .getRoles()
        .then((res) => setRoles(Array.isArray(res.data) ? res.data : []))
        .catch(() => setRoles([]));
      api
        .getAreas()
        .then((res) => setAreas(Array.isArray(res.data) ? res.data : []))
        .catch(() => setAreas([]));
    }
  }, [open]);

  const handleChange = (field) => (e) => {
    const value = e.target.value;
    setForm((prev) => ({ ...prev, [field]: value }));
    setErrores((prev) => ({ ...prev, [field]: null }));
  };

  const validarTodo = () => {
    const e = {};
    e.nombre = validarNombre(form.nombre);
    e.correo = validarCorreo(form.correo);
    e.nacimiento = validarFechaNacimiento(form.nacimiento);
    e.curp = validarCurp(form.curp);
    e.rol = validarRol(form.rol);
    e.area = validarArea(form.area);
    setErrores(e);
    return !Object.values(e).some(Boolean);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validarTodo()) return;

    setLoading(true);
    try {
      await api.register({
        nombreCompleto: form.nombre.trim(),
        correo: form.correo.trim().toLowerCase(),
        fechaNacimiento: form.nacimiento,
        curp: form.curp.trim().toUpperCase(),
        idRol: Number(form.rol),
        idArea: Number(form.area),
      });
      setForm({ nombre: "", correo: "", nacimiento: "", curp: "", rol: "", area: "" });
      setErrores({});
      onGuardar?.();
      onClose?.();
    } catch (err) {
      setErrores((prev) => ({ ...prev, _form: err.message }));
    } finally {
      setLoading(false);
    }
  };

  const handleClose = () => {
    setForm({ nombre: "", correo: "", nacimiento: "", curp: "", rol: "", area: "" });
    setErrores({});
    onClose?.();
  };

  return (
    <Modal open={open} onClose={handleClose} className="nuevo-usuario-modal">
      <div className="nuevo-usuario-modal__inner">
        <header className="nuevo-usuario-modal__header">
          <div>
            <h2 className="nuevo-usuario-modal__title">Registrar Nuevo Usuario</h2>
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
          {errores._form && (
            <div className="nuevo-usuario-modal__error" role="alert">
              {errores._form}
            </div>
          )}

          {/* Nombre */}
          <div className="nuevo-usuario-modal__field">
            <label className="nuevo-usuario-modal__label">Nombre Completo*</label>
            <Input
              placeholder="Nombre completo (sin puntos ni comas al inicio)"
              value={form.nombre}
              onChange={handleChange("nombre")}
              className="nuevo-usuario-modal__input"
              aria-invalid={!!errores.nombre}
            />
            {errores.nombre && (
              <span className="nuevo-usuario-modal__error-msg">{errores.nombre}</span>
            )}
          </div>

          {/* Correo + Fecha nacimiento */}
          <div className="nuevo-usuario-modal__row">
            <div className="nuevo-usuario-modal__field nuevo-usuario-modal__field--flex">
              <label className="nuevo-usuario-modal__label">Correo Electrónico*</label>
              <Input
                type="email"
                placeholder="usuario@institucion.edu.mx"
                value={form.correo}
                onChange={handleChange("correo")}
                className="nuevo-usuario-modal__input"
                aria-invalid={!!errores.correo}
              />
              {errores.correo && (
                <span className="nuevo-usuario-modal__error-msg">{errores.correo}</span>
              )}
            </div>
            <div className="nuevo-usuario-modal__field nuevo-usuario-modal__field--flex">
              <label className="nuevo-usuario-modal__label">Fecha de Nacimiento*</label>
              <Input
                type="date"
                value={form.nacimiento}
                onChange={handleChange("nacimiento")}
                className="nuevo-usuario-modal__input"
                aria-invalid={!!errores.nacimiento}
              />
              {errores.nacimiento && (
                <span className="nuevo-usuario-modal__error-msg">{errores.nacimiento}</span>
              )}
            </div>
          </div>

          {/* CURP */}
          <div className="nuevo-usuario-modal__field">
            <label className="nuevo-usuario-modal__label">CURP*</label>
            <Input
              placeholder="18 caracteres (formato oficial mexicano)"
              value={form.curp}
              maxLength={18}
              onChange={(e) =>
                handleChange("curp")({
                  target: {
                    value: e.target.value.replace(/[^A-Za-z0-9]/g, "").toUpperCase(),
                  },
                })
              }
              className="nuevo-usuario-modal__input"
              aria-invalid={!!errores.curp}
            />
            {errores.curp && (
              <span className="nuevo-usuario-modal__error-msg">{errores.curp}</span>
            )}
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
                  aria-invalid={!!errores.rol}
                >
                  <option value="">Seleccionar...</option>
                  {roles.map((r) => (
                    <option key={r.id} value={r.id}>
                      {r.nombre}
                    </option>
                  ))}
                </select>
                <Icon
                  icon={ControlsChevronDown}
                  size={30}
                  className="nuevo-usuario-modal__select-icon"
                  aria-hidden
                />
              </div>
              {errores.rol && (
                <span className="nuevo-usuario-modal__error-msg">{errores.rol}</span>
              )}
            </div>
            <div className="nuevo-usuario-modal__field nuevo-usuario-modal__field--flex">
              <label className="nuevo-usuario-modal__label">Área*</label>
              <div className="nuevo-usuario-modal__select-wrap">
                <select
                  value={form.area}
                  onChange={handleChange("area")}
                  className="nuevo-usuario-modal__select"
                  required
                  aria-invalid={!!errores.area}
                >
                  <option value="">Seleccionar...</option>
                  {areas.map((a) => (
                    <option key={a.id} value={a.id}>
                      {a.nombre}
                    </option>
                  ))}
                </select>
                <Icon
                  icon={ControlsChevronDown}
                  size={30}
                  className="nuevo-usuario-modal__select-icon"
                  aria-hidden
                />
              </div>
              {errores.area && (
                <span className="nuevo-usuario-modal__error-msg">{errores.area}</span>
              )}
            </div>
          </div>

          <footer className="nuevo-usuario-modal__footer">
            <Button type="button" variant="outline" size="small" onClick={handleClose}>
              Cancelar
            </Button>
            <Button
              type="submit"
              variant="primary"
              size="small"
              iconLeft={FilesSave}
              disabled={loading}
            >
              {loading ? "Guardando…" : "Guardar Usuario"}
            </Button>
          </footer>
        </form>
      </div>
    </Modal>
  );
}
