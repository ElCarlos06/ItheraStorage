import { useState, useEffect } from "react";
import FormModal from "../../../../../components/FormModal/FormModal";
import FormModalTextarea from "../../../../../components/FormModal/FormModalTextarea";
import { resguardosApi } from "../../../../../api/resguardosApi";
import { toast } from "../../../../../utils/toast.jsx";
import { GenericDelete, SecurityPassport } from "@heathmont/moon-icons";
import Icon from "../../../../../components/Icon/Icon";

export default function ReleaseResguardoModal({
  open,
  onClose,
  asset,
  onLiberar,
}) {
  const [resguardo, setResguardo] = useState(null);
  const [loading, setLoading] = useState(false);
  const [observaciones, setObservaciones] = useState("");

  useEffect(() => {
    if (!open || !asset?.id) return;
    setLoading(true);
    resguardosApi
      .getByActivo(asset.id)
      .then((res) => {
        const list = res.data ?? res.content ?? res ?? [];
        // Encontrar el resguardo activo (Pendiente o Confirmado)
        const active = list.find((r) =>
          ["Pendiente", "Confirmado"].includes(r.estadoResguardo),
        );
        setResguardo(active);
      })
      .catch((err) => {
        console.error("Error al obtener el resguardo:", err);
      })
      .finally(() => {
        setLoading(false);
      });
  }, [open, asset]);

  useEffect(() => {
    if (open) {
      setObservaciones("");
    }
  }, [open]);

  const handleSubmit = () => {
    if (!resguardo?.id) {
      toast.error("No se encontró un resguardo activo para este elemento.");
      return;
    }
    onLiberar?.(resguardo, { observaciones });
  };

  const hasActiveResguardo = !!resguardo;

  return (
    <FormModal
      open={open}
      onClose={onClose}
      className="release-resguardo-modal"
      title="Liberar Resguardo"
      subtitle={`Activo: ${asset?.nombre ?? asset?.codigo ?? ""}`}
      submitLabel="Liberar Resguardo"
      submitIcon={GenericDelete}
      submitIconSize={24}
      onSubmit={handleSubmit}
      submitDisabled={loading || !hasActiveResguardo}
    >
      <div className="form-modal__field mb-4">
        <label className="form-modal__label">
          EMPLEADO ASIGNADO ACTUALMENTE
        </label>
        {loading ? (
          <div className="text-muted mt-2">Cargando información...</div>
        ) : hasActiveResguardo ? (
          <div
            className="d-flex align-items-center gap-3 mt-2 p-3 bg-light rounded text-dark"
            style={{
              border: "1px solid var(--border-color)",
              fontWeight: "500",
            }}
          >
            <Icon icon={SecurityPassport} size={28} className="text-primary" />
            <span>
              {resguardo.usuarioEmpleado?.nombreCompleto ??
                resguardo.usuarioEmpleado?.correo ??
                "Desconocido"}{" "}
              <span className="badge bg-secondary ms-2">
                {resguardo.estadoResguardo}
              </span>
            </span>
          </div>
        ) : (
          <div className="text-danger mt-2">
            No se encontró un resguardo activo para este activo. Puede que ya
            haya sido liberado o cancelado.
          </div>
        )}
      </div>

      <FormModalTextarea
        label="OBSERVACIONES DE DEVOLUCIÓN"
        placeholder="Anota en qué estado regresa el activo o el motivo de la liberación..."
        value={observaciones}
        onChange={(e) => setObservaciones(e.target.value)}
        rows={4}
      />
    </FormModal>
  );
}
