/**
 * Modal para registrar la primera ubicación (cuando no hay ninguna).
 */
import { useState } from "react";
import FormModal from "../../../../components/FormModal/FormModal";
import Input from "../../../../components/Input/Input";
import { FilesSave } from "@heathmont/moon-icons";
import { toast } from "../../../../utils/toast.jsx";
import "./RegisterLocationModal.css";

export default function RegisterLocationModal({ open, onClose, onGuardar }) {
  const [form, setForm] = useState({
    campus: "",
    edificio: "",
    aula: "",
    descripcion: "",
  });
  const [loading, setLoading] = useState(false);

  const handleChange = (field) => (e) => {
    setForm((prev) => ({ ...prev, [field]: e.target.value }));
  };

  const validarCampos = () => {
    const faltantes = [];
    if (!form.campus?.trim()) faltantes.push("Campus");
    if (!form.edificio?.trim()) faltantes.push("Edificio");
    if (!form.aula?.trim()) faltantes.push("Aula / Laboratorio");
    if (faltantes.length > 0) {
      toast.error(`Complete los campos obligatorios: ${faltantes.join(", ")}`);
      return false;
    }
    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validarCampos()) return;
    setLoading(true);
    try {
      await onGuardar?.(form);
      setForm({ campus: "", edificio: "", aula: "", descripcion: "" });
      onClose?.();
    } finally {
      setLoading(false);
    }
  };

  const handleClose = () => {
    setForm({ campus: "", edificio: "", aula: "", descripcion: "" });
    onClose?.();
  };

  return (
    <FormModal
      open={open}
      onClose={handleClose}
      className="register-location-modal"
      title="Registrar Ubicación"
      subtitle="Define la ubicación física para los activos"
      submitLabel="Guardar Ubicación"
      submitIcon={FilesSave}
      submitIconSize={30}
      loading={loading}
      onSubmit={handleSubmit}
    >
      <div className="form-modal__field">
        <Input
          label="Campus*"
          labelClassName="form-modal__label"
          placeholder="Ej: Campus Norte, Campus Sur, Campus Central"
          value={form.campus}
          onChange={handleChange("campus")}
          className="form-modal__input"
          required
        />
      </div>

      <div className="form-modal__row">
        <div className="form-modal__field form-modal__field--flex">
          <Input
            label="Edificio*"
            labelClassName="form-modal__label"
            placeholder="Ej: Edificio A, Torre 1, Anexo B"
            value={form.edificio}
            onChange={handleChange("edificio")}
            className="form-modal__input"
            required
          />
        </div>
        <div className="form-modal__field form-modal__field--flex">
          <Input
            label="Aula / Laboratorio*"
            labelClassName="form-modal__label"
            placeholder="Ej: Sala 101, Lab. Computación, Aula 3B"
            value={form.aula}
            onChange={handleChange("aula")}
            className="form-modal__input"
            required
          />
        </div>
      </div>

      <div className="form-modal__field">
        <label className="form-modal__label">Descripción del campus</label>
        <textarea
          className="form-modal__textarea"
          placeholder="Describe detalles adicionales de la ubicación"
          value={form.descripcion}
          onChange={handleChange("descripcion")}
          rows={4}
        />
      </div>
    </FormModal>
  );
}
