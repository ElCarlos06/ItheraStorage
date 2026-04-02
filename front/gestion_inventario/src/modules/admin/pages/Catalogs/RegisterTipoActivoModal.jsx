import { useState, useEffect } from "react";
import FormModal from "../../../../components/FormModal/FormModal";
import FormModalTextarea from "../../../../components/FormModal/FormModalTextarea";
import Input from "../../../../components/Input/Input";
import { FilesSave } from "@heathmont/moon-icons";
import { toast } from "../../../../utils/toast.jsx";
import "./RegisterTipoActivoModal.css";

export default function RegisterTipoActivoModal({ open, onClose, onGuardar, initialData }) {
  const isEdit = !!initialData;
  const [loading, setLoading] = useState(false);
  const initialState = {
    nombre: "",
    marca: "",
    modelo: "",
    tipoBien: "Mueble",
    descripcion: "",
  };

  const [form, setForm] = useState(initialState);

  useEffect(() => {
    if (open && initialData) {
      setForm({
        nombre: initialData.nombre ?? "",
        marca: initialData.marca ?? "",
        modelo: initialData.modelo ?? "",
        tipoBien: initialData.tipoBien ?? "Mueble",
        descripcion: initialData.descripcion ?? "",
      });
    } else if (open) {
      setForm(initialState);
    }
  }, [open, initialData]);

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

  const validarCampos = () => {
    const faltantes = [];
    if (!form.nombre?.trim()) faltantes.push("Nombre del activo");
    if (!form.marca?.trim()) faltantes.push("Marca");
    if (!form.modelo?.trim()) faltantes.push("Modelo");
    if (faltantes.length > 0) {
      toast.error(`Complete los campos obligatorios: ${faltantes.join(", ")}`);
      return false;
    }
    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validarCampos()) return;
    const data = {
      ...form,
      nombre: form.nombre.trim(),
      marca: form.marca.trim(),
      modelo: form.modelo.trim(),
      tipoBien: form.tipoBien,
      descripcion: form.descripcion?.trim() || null,
    };
    setLoading(true);
    try {
      await onGuardar?.(data);
      resetForm();
      onClose?.();
    } catch {
      // El error ya lo muestra el padre
    } finally {
      setLoading(false);
    }
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
      title={isEdit ? "Editar Tipo de Activo" : "Registrar Tipo de Activo"}
      subtitle="Define las características del tipo de activo"
      submitLabel={isEdit ? "Guardar cambios" : "Guardar Tipo"}
      submitIcon={FilesSave}
      submitIconSize={30}
      loading={loading}
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

      <FormModalTextarea
        label="Descripción / Características"
        value={form.descripcion}
        onChange={handleChange("descripcion")}
        rows={4}
      />

    </FormModal>
  );
}