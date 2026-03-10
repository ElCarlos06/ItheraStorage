import { useState } from "react";
import FormModal from "../../../../components/FormModal/FormModal";
import Input from "../../../../components/Input/Input";
import Icon from "../../../../components/Icon/Icon";
import { ControlsChevronDown, FilesSave } from "@heathmont/moon-icons";
import "./NewAssetModal.css";

export default function NewAssetModal({ open, onClose, onGuardar }) {
  const [form, setForm] = useState({
    numeroSerie: "",
    tipoActivo: "",
    costo: "",
    campus: "",
    edificio: "",
    aula: "",
    descripcion: "",
  });

  const handleChange = (field) => (e) => {
    setForm((prev) => ({ ...prev, [field]: e.target.value }));
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
      title="Registrar Nuevo Activo"
      subtitle="Completa la información técnica del dispositivo"
      submitLabel="Guardar Activo"
      submitIcon={FilesSave}
      submitIconSize={20}
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
          <label className="form-modal__label">Tipo de Activo*</label>
          <div className="nuevo-activo-modal__select-wrap">
            <select
              value={form.tipoActivo}
              onChange={handleChange("tipoActivo")}
              className="nuevo-activo-modal__select"
              required
            >
              <option value="">Seleccionar...</option>
              <option value="Laptop">Laptop</option>
              <option value="Periférico">Periférico</option>
              <option value="Equipo de cómputo">Equipo de cómputo</option>
              <option value="Audiovisual">Audiovisual</option>
            </select>
            <Icon icon={ControlsChevronDown} size={30} className="nuevo-activo-modal__select-icon" aria-hidden />
          </div>
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
            <label className="form-modal__label">Campus*</label>
            <div className="nuevo-activo-modal__select-wrap">
              <select
                value={form.campus}
                onChange={handleChange("campus")}
                className="nuevo-activo-modal__select"
                required
              >
                <option value="">Seleccionar...</option>
                <option value="Universidad Tecnológica Emiliano Zapata">Universidad Tecnológica Emiliano Zapata</option>
                <option value="Campus Norte">Campus Norte</option>
                <option value="Campus Centro">Campus Centro</option>
              </select>
              <Icon icon={ControlsChevronDown} size={30} className="nuevo-activo-modal__select-icon" aria-hidden />
            </div>
          </div>
          <div className="form-modal__field form-modal__field--flex">
            <label className="form-modal__label">Edificio*</label>
            <div className="nuevo-activo-modal__select-wrap">
              <select
                value={form.edificio}
                onChange={handleChange("edificio")}
                className="nuevo-activo-modal__select"
                required
              >
                <option value="">Seleccionar...</option>
                <option value="D1">D1</option>
                <option value="A2">A2</option>
                <option value="C1">C1</option>
              </select>
              <Icon icon={ControlsChevronDown} size={30} className="nuevo-activo-modal__select-icon" aria-hidden />
            </div>
          </div>
          <div className="form-modal__field form-modal__field--flex">
            <label className="form-modal__label">Aula*</label>
            <div className="nuevo-activo-modal__select-wrap">
              <select
                value={form.aula}
                onChange={handleChange("aula")}
                className="nuevo-activo-modal__select"
                required
              >
                <option value="">Seleccionar...</option>
                <option value="A1">A1</option>
                <option value="B3">B3</option>
                <option value="Sala 2">Sala 2</option>
              </select>
              <Icon icon={ControlsChevronDown} size={30} className="nuevo-activo-modal__select-icon" aria-hidden />
            </div>
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
