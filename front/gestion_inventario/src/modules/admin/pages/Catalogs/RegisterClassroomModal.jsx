/**
 * Modal para registrar/editar Aula (Espacio).
 */
import { useState, useEffect } from "react";
import FormModal from "../../../../components/FormModal/FormModal";
import Input from "../../../../components/Input/Input";
import Select from "../../../../components/Select/Select";
import { FilesSave } from "@heathmont/moon-icons";
import { toast } from "../../../../utils/toast.jsx";
import "./RegisterLocationModal.css";

export default function RegisterClassroomModal({
  open,
  onClose,
  onGuardar,
  edificios = [],
  initialData,
}) {
  const isEdit = !!initialData;
  const [form, setForm] = useState({ nombreEspacio: "", idEdificio: "" });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (open && initialData) {
      setForm({
        nombreEspacio: initialData.nombreEspacio ?? initialData.nombre ?? initialData.aula ?? "",
        idEdificio: String(initialData.idEdificio ?? initialData.edificio?.id ?? ""),
      });
    } else if (open) {
      setForm({ nombreEspacio: "", idEdificio: "" });
    }
  }, [open, initialData]);

  const handleChange = (field) => (e) => {
    setForm((prev) => ({ ...prev, [field]: e.target.value }));
  };

  const handleSelect = (field) => (value) => {
    setForm((prev) => ({ ...prev, [field]: value }));
  };

  const validarCampos = () => {
    const faltantes = [];
    if (!form.idEdificio) faltantes.push("Edificio");
    if (!form.nombreEspacio?.trim()) faltantes.push("Nombre del aula");
    if (faltantes.length > 0) {
      toast.error(`Complete los campos obligatorios: ${faltantes.join(", ")}`);
      return false;
    }
    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validarCampos()) return;
    const idEdificio = form.idEdificio ? Number(form.idEdificio) : null;
    setLoading(true);
    try {
      await onGuardar?.({ nombreEspacio: form.nombreEspacio.trim(), idEdificio });
      setForm({ nombreEspacio: "", idEdificio: "" });
      onClose?.();
    } finally {
      setLoading(false);
    }
  };

  const handleClose = () => {
    setForm({ nombreEspacio: "", idEdificio: "" });
    onClose?.();
  };

  return (
    <FormModal
      open={open}
      onClose={handleClose}
      className="register-location-modal"
      title={isEdit ? "Editar Aula" : "Registrar Aula"}
      subtitle="Define el aula o espacio dentro de un edificio"
      submitLabel={isEdit ? "Guardar cambios" : "Guardar Aula"}
      submitIcon={FilesSave}
      submitIconSize={30}
      loading={loading}
      onSubmit={handleSubmit}
    >
      <div className="form-modal__field">
        <Select
          label="Edificio*"
          labelClassName="form-modal__label"
          value={form.idEdificio}
          onChange={handleSelect("idEdificio")}
          options={edificios.map((e) => ({
            value: String(e.id),
            label: `${e.nombre ?? e.name} ${e.campus ? `(${e.campus.nombre})` : ""}`.trim(),
          }))}
          placeholder="Seleccionar edificio..."
          variant="ghost"
          required
        />
      </div>
      <div className="form-modal__field">
        <Input
          label="Nombre del aula / espacio*"
          labelClassName="form-modal__label"
          placeholder="Ej: Aula 101, Lab. Computación, Sala 2"
          value={form.nombreEspacio}
          onChange={handleChange("nombreEspacio")}
          className="form-modal__input"
          required
        />
      </div>
    </FormModal>
  );
}
