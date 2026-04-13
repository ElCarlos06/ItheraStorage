import { useState } from "react";
import FormModal from "../../../../components/FormModal/FormModal";
import { SecurityPassport } from "@heathmont/moon-icons";
import Icon from "../../../../components/Icon/Icon";

export default function ReleaseTechnicianModal({
  open,
  onClose,
  mantenimiento,
  onLiberar,
}) {
  const [submitting, setSubmitting] = useState(false);

  const tecnicoNombre =
    mantenimiento?.tecnicoNombre ??
    mantenimiento?.tecnicoAsignado ??
    mantenimiento?.usuarioTecnico?.nombreCompleto ??
    mantenimiento?.usuarioTecnico?.nombre ??
    mantenimiento?.usuarioTecnico?.correo ??
    "Desconocido";

  const activoLabel =
    mantenimiento?.activoNombre ??
    mantenimiento?.activo?.nombre ??
    mantenimiento?.activo?.codigoActivo ??
    mantenimiento?.activo?.codigo ??
    mantenimiento?.codigo ??
    "";

  const estadoActual = mantenimiento?.estatus ?? mantenimiento?.estadoMantenimiento ?? "Asignado";

  const handleSubmit = async () => {
    setSubmitting(true);
    try {
      await onLiberar?.();
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <FormModal
      open={open}
      onClose={onClose}
      title="Liberar técnico"
      subtitle={activoLabel ? `Activo: ${activoLabel}` : "Desasignar el técnico actual"}
      submitLabel="Liberar técnico"
      submitIcon={SecurityPassport}
      submitIconSize={24}
      onSubmit={handleSubmit}
      loading={submitting}
    >
      <div className="form-modal__field mb-2">
        <label className="form-modal__label">TÉCNICO ASIGNADO ACTUALMENTE</label>
        <div
          className="d-flex align-items-center gap-3 mt-2 p-3 bg-light rounded text-dark"
          style={{ border: "1px solid var(--border-color)", fontWeight: "500" }}
        >
          <Icon icon={SecurityPassport} size={28} className="text-primary" />
          <span>
            {tecnicoNombre}
            <span className="badge bg-secondary ms-2">{estadoActual}</span>
          </span>
        </div>
      </div>

      <p className="m-0" style={{ fontSize: "14px", color: "var(--moon-content-text-secondary, #6b7280)", lineHeight: "1.5" }}>
        Al liberar el técnico, el reporte regresará a la bandeja de pendientes y podrás asignar otro técnico.
      </p>
    </FormModal>
  );
}
