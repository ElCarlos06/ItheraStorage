import { useState, useEffect } from "react";
import Modal from "../../../../components/Modal/Modal";
import Input from "../../../../components/Input/Input";
import Button from "../../../../components/Button/Button";
import Select from "../../../../components/Select/Select";
import { X } from "lucide-react";
import { FilesSave } from "@heathmont/moon-icons";
import { api } from "../../../../api/client"; // roles/areas vía api
import {
  validarCurp,
  validarNombre,
  validarCorreo,
  validarFechaNacimiento,
  validarRol,
  validarArea,
} from "../../../../utils/validaciones";
import ErrorBanner from "../../../../components/ErrorBanner/ErrorBanner";
import "./NewUserModal.css";

export default function NewUserModal({ open, onClose, onGuardar, initialData }) {
  const isEdit = !!initialData;
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
        .then((res) => setRoles(Array.isArray(res?.data) ? res.data : []))
        .catch(() => setRoles([]));
      api
        .getAreas()
        .then((res) => setAreas(Array.isArray(res?.data) ? res.data : []))
        .catch(() => setAreas([]));
    }
  }, [open]);

  useEffect(() => {
    if (open && initialData) {
      setForm({
        nombre: initialData.nombre ?? initialData.nombreCompleto ?? "",
        correo: initialData.correo ?? "",
        nacimiento: initialData.nacimiento ?? initialData.fechaNacimiento ?? "",
        curp: initialData.curp ?? "",
        rol: String(initialData.idRol ?? initialData.rol ?? ""),
        area: String(initialData.idArea ?? initialData.area ?? ""),
      });
    } else if (open) {
      setForm({ nombre: "", correo: "", nacimiento: "", curp: "", rol: "", area: "" });
    }
  }, [open, initialData]);

  const handleChange = (field) => (e) => {
    const value = e.target.value;
    setForm((prev) => ({ ...prev, [field]: value }));
    setErrores((prev) => ({ ...prev, [field]: null }));
  };

  const handleSelect = (field) => (value) => {
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
      const payload = {
        nombreCompleto: form.nombre.trim(),
        correo: form.correo.trim().toLowerCase(),
        fechaNacimiento: form.nacimiento,
        curp: form.curp.trim().toUpperCase(),
        idRol: Number(form.rol),
        idArea: Number(form.area),
      };
      if (isEdit && initialData?.id) {
        await api.updateUser(initialData.id, payload);
      } else {
        await api.register(payload);
      }
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
            <h2 className="nuevo-usuario-modal__title">
              {isEdit ? "Editar Usuario" : "Registrar Nuevo Usuario"}
            </h2>
            <p className="nuevo-usuario-modal__subtitle">
              {isEdit
                ? "Actualiza la información del empleado"
                : "Crea una nueva cuenta de acceso y perfil de empleado"}
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
            <ErrorBanner
              message={errores._form}
              onDismiss={() => setErrores((p) => ({ ...p, _form: null }))}
              className="nuevo-usuario-modal__error-banner"
            />
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
              <Select
                label="Rol"
                labelClassName="nuevo-usuario-modal__label"
                value={form.rol}
                onChange={handleSelect("rol")}
                options={roles.map((r) => ({ value: String(r.id), label: r.nombre }))}
                placeholder="Seleccionar..."
                required
                aria-invalid={!!errores.rol}
                variant="ghost"
              />
              {errores.rol && (
                <span className="nuevo-usuario-modal__error-msg">{errores.rol}</span>
              )}
            </div>
            <div className="nuevo-usuario-modal__field nuevo-usuario-modal__field--flex">
              <Select
                label="Área"
                labelClassName="nuevo-usuario-modal__label"
                value={form.area}
                onChange={handleSelect("area")}
                options={areas.map((a) => ({ value: String(a.id), label: a.nombre }))}
                placeholder="Seleccionar..."
                required
                aria-invalid={!!errores.area}
                variant="ghost"
              />
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
              iconSize={30}
              disabled={loading}
            >
              {loading ? "Guardando…" : isEdit ? "Guardar cambios" : "Guardar Usuario"}
            </Button>
          </footer>
        </form>
      </div>
    </Modal>
  );
}
