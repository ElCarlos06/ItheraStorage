/**
 * Modal para registrar/editar Aula.
 */
import { useState, useEffect } from "react";
import FormModal from "../../../../components/FormModal/FormModal";
import Input from "../../../../components/Input/Input";
import Select from "../../../../components/Select/Select";
import { FilesSave } from "@heathmont/moon-icons";
import "./RegisterLocationModal.css";

export default function RegisterClassroomModal({
  open,
  onClose,
  onGuardar,
  campus = [],
  edificios = [],
  initialData,
}) {
  const isEdit = !!initialData;
  const [form, setForm] = useState({
    aula: "",
    campus: "",
    edificio: "",
    descripcion: "",
  });

  useEffect(() => {
    if (open && initialData) {
      setForm({
        aula: initialData.aula ?? initialData.nombre ?? "",
        campus: initialData.campus ?? initialData.idCampus ?? "",
        edificio: initialData.edificio ?? initialData.idEdificio ?? "",
        descripcion: initialData.descripcion ?? "",
      });
    } else if (open) {
      setForm({ aula: "", campus: "", edificio: "", descripcion: "" });
    }
  }, [open, initialData]);

  const handleChange = (field) => (e) => {
    setForm((prev) => ({ ...prev, [field]: e.target.value }));
  };

  const handleSelect = (field) => (value) => {
    setForm((prev) => ({ ...prev, [field]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onGuardar?.(form);
    setForm({ aula: "", campus: "", edificio: "", descripcion: "" });
    onClose?.();
  };

  const handleClose = () => {
    setForm({ aula: "", campus: "", edificio: "", descripcion: "" });
    onClose?.();
  };

  return (
    <FormModal
      open={open}
      onClose={handleClose}
      className="register-location-modal"
      title={isEdit ? "Editar Aula" : "Registrar Aula"}
      subtitle="Define la ubicación física para los activos"
      submitLabel={isEdit ? "Guardar cambios" : "Guardar Ubicación"}
      submitIcon={FilesSave}
      submitIconSize={30}
      onSubmit={handleSubmit}
    >
      <div className="form-modal__field">
        <Input
          label="Aula*"
          labelClassName="form-modal__label"
          placeholder="Ej: Aula de cómputo, Sala 101"
          value={form.aula}
          onChange={handleChange("aula")}
          className="form-modal__input"
          required
        />
      </div>

      <div className="form-modal__row">
        <div className="form-modal__field form-modal__field--flex">
          <Select
            label="Campus*"
            labelClassName="form-modal__label"
            value={form.campus}
            onChange={handleSelect("campus")}
            options={campus.map((c) => ({ value: String(c.id), label: c.nombre ?? c.name }))}
            placeholder="Seleccionar..."
            variant="ghost"
          />
        </div>
        <div className="form-modal__field form-modal__field--flex">
          <Select
            label="Edificio*"
            labelClassName="form-modal__label"
            value={form.edificio}
            onChange={handleSelect("edificio")}
            options={edificios.map((e) => ({ value: String(e.id), label: e.nombre ?? e.name }))}
            placeholder="Seleccionar..."
            variant="ghost"
          />
        </div>
      </div>

      <div className="form-modal__field">
        <label className="form-modal__label">Descripción</label>
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
