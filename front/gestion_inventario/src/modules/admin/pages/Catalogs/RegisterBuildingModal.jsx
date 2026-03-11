/**
 * Modal para registrar/editar Edificio.
 */
import { useState, useEffect } from "react";
import FormModal from "../../../../components/FormModal/FormModal";
import Input from "../../../../components/Input/Input";
import Select from "../../../../components/Select/Select";
import { FilesSave } from "@heathmont/moon-icons";
import "./RegisterLocationModal.css";

export default function RegisterBuildingModal({ open, onClose, onGuardar, campus = [], initialData }) {
  const isEdit = !!initialData;
  const [form, setForm] = useState({ nombre: "", idCampus: "" });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (open && initialData) {
      setForm({
        nombre: initialData.nombre ?? initialData.name ?? "",
        idCampus: String(initialData.campus?.id ?? initialData.idCampus ?? ""),
      });
    } else if (open) {
      setForm({ nombre: "", idCampus: "" });
    }
  }, [open, initialData]);

  const handleChange = (field) => (e) => {
    setForm((prev) => ({ ...prev, [field]: e.target.value }));
  };

  const handleSelect = (field) => (value) => {
    setForm((prev) => ({ ...prev, [field]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const idCampus = form.idCampus ? Number(form.idCampus) : null;
    if (!idCampus) return;
    setLoading(true);
    try {
      await onGuardar?.({ nombre: form.nombre.trim(), idCampus });
      setForm({ nombre: "", idCampus: "" });
      onClose?.();
    } finally {
      setLoading(false);
    }
  };

  const handleClose = () => {
    setForm({ nombre: "", idCampus: "" });
    onClose?.();
  };

  return (
    <FormModal
      open={open}
      onClose={handleClose}
      className="register-location-modal"
      title={isEdit ? "Editar Edificio" : "Registrar Edificio"}
      subtitle="Define el edificio dentro de un campus"
      submitLabel={isEdit ? "Guardar cambios" : "Guardar Edificio"}
      submitIcon={FilesSave}
      submitIconSize={30}
      loading={loading}
      onSubmit={handleSubmit}
    >
      <div className="form-modal__field">
        <Select
          label="Campus*"
          labelClassName="form-modal__label"
          value={form.idCampus}
          onChange={handleSelect("idCampus")}
          options={campus.map((c) => ({ value: String(c.id), label: c.nombre ?? c.name }))}
          placeholder="Seleccionar campus..."
          variant="ghost"
          required
        />
      </div>
      <div className="form-modal__field">
        <Input
          label="Nombre del edificio*"
          labelClassName="form-modal__label"
          placeholder="Ej: Torre 1, Edificio D1, Centro de desarrollo"
          value={form.nombre}
          onChange={handleChange("nombre")}
          className="form-modal__input"
          required
        />
      </div>
    </FormModal>
  );
}
