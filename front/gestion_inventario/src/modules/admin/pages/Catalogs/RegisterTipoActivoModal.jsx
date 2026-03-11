import { useState } from "react";
import FormModal from "../../../../components/FormModal/FormModal";
import Input from "../../../../components/Input/Input";
import { FilesSave } from "@heathmont/moon-icons";
import "./RegisterTipoActivoModal.css";

export default function RegisterTipoActivoModal({ open, onClose, onGuardar }) {
  const [form, setForm] = useState({
    nombre: "",
    marca: "",
    modelo: "",
    tipoBien: "mueble",
    descripcion: "",
  });

  const handleChange = (field) => (e) => {
    setForm((prev) => ({ ...prev, [field]: e.target.value }));
  };

  const handleTipoBien = (valor) => {
    setForm((prev) => ({ ...prev, tipoBien: valor }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onGuardar?.(form);
    setForm({ nombre: "", marca: "", modelo: "", tipoBien: "mueble", descripcion: "" });
    onClose?.();
  };

  const handleClose = () => {
    setForm({ nombre: "", marca: "", modelo: "", tipoBien: "mueble", descripcion: "" });
    onClose?.();
  };

  return (
    <FormModal
      open={open}
      onClose={handleClose}
      className="registrar-tipo-activo-modal"
      title="Registrar Tipo de Activo"
      subtitle="Define las características del tipo de activo"
      submitLabel="Guardar Tipo"
      submitIcon={FilesSave}
      submitIconSize={30}
      onSubmit={handleSubmit}
    >
      <div className="form-modal__field">
        <Input
          label="Nombre del Activo*"
          labelClassName="form-modal__label"
          placeholder="Ej: Laptop, Monitor, Impresora, Proyector"
          value={form.nombre}
          onChange={handleChange("nombre")}
          className="form-modal__input"
          required
        />
      </div>

      <div className="form-modal__row">
        <div className="form-modal__field form-modal__field--flex">
          <Input
            label="Marca*"
            labelClassName="form-modal__label"
            placeholder="Ej: HP, Dell, Lenovo, Epson"
            value={form.marca}
            onChange={handleChange("marca")}
            className="form-modal__input"
            required
          />
        </div>
        <div className="form-modal__field form-modal__field--flex">
          <Input
            label="Modelo*"
            labelClassName="form-modal__label"
            placeholder="Ej: EliteBook 840, ThinkPad X1"
            value={form.modelo}
            onChange={handleChange("modelo")}
            className="form-modal__input"
            required
          />
        </div>
      </div>

      <div className="form-modal__field">
        <label className="form-modal__label">Tipo de bien*</label>
        <div className="registrar-tipo-activo-modal__tipo-bien">
          <button
            type="button"
            className={`registrar-tipo-activo-modal__tipo-btn ${form.tipoBien === "mueble" ? "registrar-tipo-activo-modal__tipo-btn--active" : ""}`}
            onClick={() => handleTipoBien("mueble")}
          >
            Mueble
          </button>
          <button
            type="button"
            className={`registrar-tipo-activo-modal__tipo-btn ${form.tipoBien === "vehiculo" ? "registrar-tipo-activo-modal__tipo-btn--active" : ""}`}
            onClick={() => handleTipoBien("vehiculo")}
          >
            Vehículo
          </button>
        </div>
      </div>

      <div className="form-modal__field">
        <label className="form-modal__label">Descripción / Características</label>
        <textarea
          className="form-modal__textarea"
          placeholder="Describe las características generales de este tipo de activo: especificaciones técnicas, usos comunes, etc."
          value={form.descripcion}
          onChange={handleChange("descripcion")}
          rows={4}
        />
      </div>
    </FormModal>
  );
}
