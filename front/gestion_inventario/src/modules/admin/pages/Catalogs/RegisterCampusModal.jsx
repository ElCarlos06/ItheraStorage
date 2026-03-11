/**
 * Modal para registrar/editar Campus.
 */
import { useState, useEffect } from "react";
import FormModal from "../../../../components/FormModal/FormModal";
import Input from "../../../../components/Input/Input";
import Select from "../../../../components/Select/Select";
import { FilesSave } from "@heathmont/moon-icons";
import "./RegisterLocationModal.css";

export default function RegisterCampusModal({
  open,
  onClose,
  onGuardar,
  edificios = [],
  aulas = [],
  initialData,
}) {
  const isEdit = !!initialData;
  const [form, setForm] = useState({
    campus: "",
    edificio: "",
    aula: "",
    descripcion: "",
  });

  useEffect(() => {
    if (open && initialData) {
      setForm({
        campus: initialData.campus ?? initialData.nombre ?? "",
        edificio: initialData.edificio ?? initialData.idEdificio ?? "",
        aula: initialData.aula ?? initialData.idAula ?? "",
        descripcion: initialData.descripcion ?? "",
      });
    } else if (open) {
      setForm({ campus: "", edificio: "", aula: "", descripcion: "" });
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
    setForm({ campus: "", edificio: "", aula: "", descripcion: "" });
    onClose?.();
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
      title={isEdit ? "Editar Campus" : "Registrar Campus"}
      subtitle="Define la ubicación física para los activos"
      submitLabel={isEdit ? "Guardar cambios" : "Guardar Ubicación"}
      submitIcon={FilesSave}
      submitIconSize={30}
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
