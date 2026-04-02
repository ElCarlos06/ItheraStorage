/**
 * Modal para registrar/editar Campus.
 */
import { useState, useEffect } from "react";
import FormModal from "../../../../components/FormModal/FormModal";
import FormModalTextarea from "../../../../components/FormModal/FormModalTextarea";
import Input from "../../../../components/Input/Input";
import { FilesSave } from "@heathmont/moon-icons";
import { toast } from "../../../../utils/toast.jsx";
import "./RegisterLocationModal.css";

export default function RegisterCampusModal({ open, onClose, onGuardar, initialData }) {
  const isEdit = !!initialData;
  const [form, setForm] = useState({ nombre: "", descripcion: "" });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (open && initialData) {
      setForm({
        nombre: initialData.nombre ?? initialData.name ?? "",
        descripcion: initialData.descripcion ?? "",
      });
    } else if (open) {
      setForm({ nombre: "", descripcion: "" });
    }
  }, [open, initialData]);

  const handleChange = (field) => (e) => {
    setForm((prev) => ({ ...prev, [field]: e.target.value }));
  };

  const validarCampos = () => {
    if (!form.nombre?.trim()) {
      toast.error("Complete los campos obligatorios: Nombre del campus");
      return false;
    }
    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validarCampos()) return;
    setLoading(true);
    try {
      await onGuardar?.({ nombre: form.nombre.trim(), descripcion: form.descripcion?.trim() || null });
      setForm({ nombre: "", descripcion: "" });
      onClose?.();
    } finally {
      setLoading(false);
    }
  };

  const handleClose = () => {
    setForm({ nombre: "", descripcion: "" });
    onClose?.();
  };

  return (
    <FormModal
      open={open}
      onClose={handleClose}
      className="register-location-modal"
      title={isEdit ? "Editar Campus" : "Registrar Campus"}
      subtitle="Define la ubicación física para los activos"
      submitLabel={isEdit ? "Guardar cambios" : "Guardar Campus"}
      submitIcon={FilesSave}
      submitIconSize={30}
      loading={loading}
      onSubmit={handleSubmit}
    >
      <div className="form-modal__field">
        <Input
          label="Nombre del campus*"
          labelClassName="form-modal__label"
          placeholder="Ej: Campus Norte, Universidad Tecnológica Emiliano Zapata"
          value={form.nombre}
          onChange={handleChange("nombre")}
          className="form-modal__input"
          required
        />
      </div>
      <FormModalTextarea
        label="Descripción"
        placeholder="Describe la ubicación o detalles del campus"
        value={form.descripcion}
        onChange={handleChange("descripcion")}
        rows={3}
      />
    </FormModal>
  );
}
