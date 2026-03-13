import { useState } from "react";
import FormModal from "../../../../components/FormModal/FormModal";
import Input from "../../../../components/Input/Input";
import { FilesSave } from "@heathmont/moon-icons";
import "./RegisterTipoActivoModal.css";

export default function RegisterTipoActivoModal({ open, onClose, onGuardar }) {

  const initialState = {
    nombre: "",
    marca: "",
    modelo: "",
    tipoBien: "Mueble",
    descripcion: "",
  };

  const [form, setForm] = useState(initialState);

  const handleChange = (field) => (e) => {
    setForm((prev) => ({
      ...prev,
      [field]: e.target.value,
    }));
  };

  const handleTipoBien = (valor) => {
    setForm((prev) => ({
      ...prev,
      tipoBien: valor,
    }));
  };

  const resetForm = () => {
    setForm(initialState);
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    const data = {
      ...form,
      nombre: form.nombre.trim(),
      marca: form.marca.trim(),
      modelo: form.modelo.trim(),
      descripcion: form.descripcion?.trim(),
    };

    onGuardar?.(data);

    resetForm();
  };

  const handleClose = () => {
    resetForm();
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
          placeholder="Ej: Laptop, Monitor, Impresora"
          value={form.nombre}
          onChange={handleChange("nombre")}
          required
        />
      </div>

      <div className="form-modal__row">

        <div className="form-modal__field form-modal__field--flex">
          <Input
            label="Marca*"
            placeholder="Ej: HP, Dell"
            value={form.marca}
            onChange={handleChange("marca")}
            required
          />
        </div>

        <div className="form-modal__field form-modal__field--flex">
          <Input
            label="Modelo*"
            placeholder="Ej: EliteBook"
            value={form.modelo}
            onChange={handleChange("modelo")}
            required
          />
        </div>

      </div>

      <div className="form-modal__field">

        <label className="form-modal__label">Tipo de bien*</label>

        <div className="registrar-tipo-activo-modal__tipo-bien">

          <button
            type="button"
            className={`registrar-tipo-activo-modal__tipo-btn ${
              form.tipoBien === "Mueble"
                ? "registrar-tipo-activo-modal__tipo-btn--active"
                : ""
            }`}
            onClick={() => handleTipoBien("Mueble")}
          >
            Mueble
          </button>

          <button
            type="button"
            className={`registrar-tipo-activo-modal__tipo-btn ${
              form.tipoBien === "Inmueble"
                ? "registrar-tipo-activo-modal__tipo-btn--active"
                : ""
            }`}
            onClick={() => handleTipoBien("Inmueble")}
          >
            Vehículo
          </button>

        </div>
      </div>

      <div className="form-modal__field">

        <label className="form-modal__label">
          Descripción / Características
        </label>

        <textarea
          className="form-modal__textarea"
          value={form.descripcion}
          onChange={handleChange("descripcion")}
          rows={4}
        />

      </div>

    </FormModal>
  );
}