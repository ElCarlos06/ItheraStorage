import { useState, useEffect } from "react";
import FormModal from "../../../../components/FormModal/FormModal";
import Input from "../../../../components/Input/Input";
import Select from "../../../../components/Select/Select";
import { FilesSave } from "@heathmont/moon-icons";
import "./NewAssetModal.css";

const TIPO_ACTIVO_OPTIONS = [
  { value: "Laptop", label: "Laptop" },
  { value: "Periférico", label: "Periférico" },
  { value: "Equipo de cómputo", label: "Equipo de cómputo" },
  { value: "Audiovisual", label: "Audiovisual" },
];

const CAMPUS_OPTIONS = [
  { value: "Universidad Tecnológica Emiliano Zapata", label: "Universidad Tecnológica Emiliano Zapata" },
  { value: "Campus Norte", label: "Campus Norte" },
  { value: "Campus Centro", label: "Campus Centro" },
];

const EDIFICIO_OPTIONS = [
  { value: "D1", label: "D1" },
  { value: "A2", label: "A2" },
  { value: "C1", label: "C1" },
];

const AULA_OPTIONS = [
  { value: "A1", label: "A1" },
  { value: "B3", label: "B3" },
  { value: "Sala 2", label: "Sala 2" },
];

export default function NewAssetModal({ open, onClose, onGuardar, initialData }) {
  const isEdit = !!initialData;
  const [form, setForm] = useState({
    numeroSerie: "",
    tipoActivo: "",
    costo: "",
    campus: "",
    edificio: "",
    aula: "",
    descripcion: "",
  });

  useEffect(() => {
    if (open && initialData) {
      setForm({
        numeroSerie: initialData.numeroSerie ?? initialData.codigo ?? "",
        tipoActivo: initialData.tipoActivo ?? "",
        costo: initialData.costo ?? "",
        campus: initialData.campus ?? "",
        edificio: initialData.edificio ?? "",
        aula: initialData.aula ?? "",
        descripcion: initialData.descripcion ?? initialData.descripcionCorta ?? "",
      });
    } else if (open) {
      setForm({
        numeroSerie: "",
        tipoActivo: "",
        costo: "",
        campus: "",
        edificio: "",
        aula: "",
        descripcion: "",
      });
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
    setForm({ numeroSerie: "", tipoActivo: "", costo: "", campus: "", edificio: "", aula: "", descripcion: "" });
    onClose?.();
  };

  const handleClose = () => {
    setForm({ numeroSerie: "", tipoActivo: "", costo: "", campus: "", edificio: "", aula: "", descripcion: "" });
    onClose?.();
  };

  return (
    <FormModal
      open={open}
      onClose={handleClose}
      className="nuevo-activo-modal"
      title={isEdit ? "Editar Activo" : "Registrar Nuevo Activo"}
      subtitle="Completa la información técnica del dispositivo"
      submitLabel={isEdit ? "Guardar cambios" : "Guardar Activo"}
      submitIcon={FilesSave}
      submitIconSize={30}
      onSubmit={handleSubmit}
    >
      <div className="form-modal__field">
        <Input
          label="Número de Serie*"
          labelClassName="form-modal__label"
          placeholder="SN-XXXX-XXXX-XXXX"
          value={form.numeroSerie}
          onChange={handleChange("numeroSerie")}
          className="form-modal__input"
          required
        />
      </div>

      <div className="form-modal__row">
        <div className="form-modal__field form-modal__field--flex">
          <Select
            label="Tipo de Activo"
            labelClassName="form-modal__label"
            value={form.tipoActivo}
            onChange={handleSelect("tipoActivo")}
            options={TIPO_ACTIVO_OPTIONS}
            placeholder="Seleccionar..."
            required
            variant="ghost"
          />
        </div>
        <div className="form-modal__field form-modal__field--flex nuevo-activo-modal__field--costo">
          <label className="form-modal__label">Costo</label>
          <div className="nuevo-activo-modal__input-costo">
            <span className="nuevo-activo-modal__costo-prefix">$</span>
            <Input
              type="number"
              step="0.01"
              min="0"
              placeholder="0.00"
              value={form.costo}
              onChange={handleChange("costo")}
              className="form-modal__input nuevo-activo-modal__input--costo"
            />
          </div>
        </div>
      </div>

      <div className="nuevo-activo-modal__section">
        <h3 className="nuevo-activo-modal__section-title">Ubicación</h3>
        <div className="form-modal__row nuevo-activo-modal__row--3">
          <div className="form-modal__field form-modal__field--flex">
            <Select
              label="Campus"
              labelClassName="form-modal__label"
              value={form.campus}
              onChange={handleSelect("campus")}
              options={CAMPUS_OPTIONS}
              placeholder="Seleccionar..."
              required
              variant="ghost"
            />
          </div>
          <div className="form-modal__field form-modal__field--flex">
            <Select
              label="Edificio"
              labelClassName="form-modal__label"
              value={form.edificio}
              onChange={handleSelect("edificio")}
              options={EDIFICIO_OPTIONS}
              placeholder="Seleccionar..."
              required
              variant="ghost"
            />
          </div>
          <div className="form-modal__field form-modal__field--flex">
            <Select
              label="Aula"
              labelClassName="form-modal__label"
              value={form.aula}
              onChange={handleSelect("aula")}
              options={AULA_OPTIONS}
              placeholder="Seleccionar..."
              required
              variant="ghost"
            />
          </div>
        </div>
      </div>

      <div className="form-modal__field">
        <label className="form-modal__label">Descripción Detallada</label>
        <textarea
          placeholder="Especificaciones técnicas, marca, modelo, etc."
          value={form.descripcion}
          onChange={handleChange("descripcion")}
          className="form-modal__textarea"
          rows={4}
        />
      </div>
    </FormModal>
  );
}
