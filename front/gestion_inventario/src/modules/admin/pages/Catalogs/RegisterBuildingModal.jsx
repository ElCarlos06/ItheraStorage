/**
 * Modal para registrar/editar Edificio.
 */
import { useState, useEffect } from "react";
import FormModal from "../../../../components/FormModal/FormModal";
import Input from "../../../../components/Input/Input";
import Select from "../../../../components/Select/Select";
import { FilesSave } from "@heathmont/moon-icons";
import "./RegisterLocationModal.css";

export default function RegisterBuildingModal({
  open,
  onClose,
  onGuardar,
  campus = [],
  aulas = [],
  initialData,
}) {
  const isEdit = !!initialData;
  const [form, setForm] = useState({
    edificio: "",
    campus: "",
    aula: "",
    descripcion: "",
  });

  useEffect(() => {
    if (open && initialData) {
      setForm({
        edificio: initialData.edificio ?? initialData.nombre ?? "",
        campus: initialData.campus ?? initialData.idCampus ?? "",
        aula: initialData.aula ?? initialData.idAula ?? "",
        descripcion: initialData.descripcion ?? "",
      });
    } else if (open) {
      setForm({ edificio: "", campus: "", aula: "", descripcion: "" });
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
    setForm({ edificio: "", campus: "", aula: "", descripcion: "" });
    onClose?.();
  };

  const handleClose = () => {
    setForm({ edificio: "", campus: "", aula: "", descripcion: "" });
    onClose?.();
  };

  return (
    <FormModal
      open={open}
      onClose={handleClose}
      className="register-location-modal"
      title={isEdit ? "Editar Edificio" : "Registrar Edificio"}
      subtitle="Define la ubicación física para los activos"
      submitLabel={isEdit ? "Guardar cambios" : "Guardar Ubicación"}
      submitIcon={FilesSave}
      submitIconSize={30}
      onSubmit={handleSubmit}
    >
      <div className="form-modal__field">
        <Input
          label="Edificio*"
          labelClassName="form-modal__label"
          placeholder="Ej: Centro de desarrollo, Torre 1"
          value={form.edificio}
          onChange={handleChange("edificio")}
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
            label="Aula / Laboratorio*"
            labelClassName="form-modal__label"
            value={form.aula}
            onChange={handleSelect("aula")}
            options={aulas.map((a) => ({ value: String(a.id), label: a.nombre ?? a.name }))}
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
