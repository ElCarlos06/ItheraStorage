import { useState, useEffect, useCallback } from "react";
import FormModal from "../../../../components/FormModal/FormModal";
import Select from "../../../../components/Select/Select";
import { usersApi } from "../../../../api/usersApi";
import { solicitudesApi } from "../../../../api/solicitudesApi";
import { getProfileFromToken } from "../../../../api/authApi";
import { toast } from "../../../../utils/toast.jsx";
import FormModalTextarea from "../../../../components/FormModal/FormModalTextarea";
import { FORM_MODAL_TEXTAREA_MAX } from "../../../../constants/formLimits";
import "./AssignTechnicianModal.css";

function toOptions(items) {
  const list = Array.isArray(items) ? items : [];
  return list
    .map((item) => {
      const id = item.id ?? item.id_usuario;
      const label = item.nombreCompleto ?? item.nombre ?? item.correo ?? "—";
      const value = id != null && id !== "" ? String(id) : "";
      return { value, label };
    })
    .filter((o) => o.value !== "");
}

function esTecnico(u) {
  const rol = (u.role?.nombre ?? u.rol?.nombre ?? u.rol ?? "")
    .toString()
    .toLowerCase();
  return rol.includes("tecnico");
}

function isValidPositiveInt(value) {
  const n = Number(value);
  return Number.isInteger(n) && n > 0;
}

function validateAssignForm({ idUsuarioTecnico, tecnicosLoaded, tecnicosCount }) {
  const errors = { tecnico: "" };

  if (!tecnicosLoaded) {
    errors.tecnico = "Espera a que carguen los técnicos.";
    return errors;
  }

  if (tecnicosCount === 0) {
    errors.tecnico = "No hay técnicos disponibles. Contacta al administrador.";
    return errors;
  }

  if (!idUsuarioTecnico || String(idUsuarioTecnico).trim() === "") {
    errors.tecnico = "Selecciona un técnico.";
  }

  return errors;
}

export default function AssignTechnicianModal({
  open,
  onClose,
  reporte,
  onAssigned,
}) {
  const [form, setForm] = useState({
    idUsuarioTecnico: "",
    tipoAsignado: "Correctivo",
    observaciones: "",
  });
  const [errors, setErrors] = useState({ tecnico: "" });
  const [tecnicos, setTecnicos] = useState([]);
  const [loadingUsers, setLoadingUsers] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const loadTecnicos = useCallback(() => {
    setLoadingUsers(true);
    usersApi
      .getUsers(0, 200)
      .then((res) => {
        const content =
          res?.data?.content ?? res?.content ?? res?.data?.content ?? res?.data ?? [];
        const list = Array.isArray(content) ? content : [];
        const filtrados = list.filter(
          (u) => (u.esActivo ?? u.es_activo ?? true) && esTecnico(u),
        );
        setTecnicos(toOptions(filtrados));
      })
      .catch(() => setTecnicos([]))
      .finally(() => setLoadingUsers(false));
  }, []);

  useEffect(() => {
    if (!open) return;
    loadTecnicos();
  }, [open, loadTecnicos]);

  useEffect(() => {
    if (open) {
      setForm({
        idUsuarioTecnico: "",
        tipoAsignado: "Correctivo",
        observaciones: "",
      });
      setErrors({ tecnico: "" });
    }
  }, [open]);

  const handleClose = () => {
    onClose?.();
  };

  const handleSubmit = async (e) => {
    e?.preventDefault?.();

    const tecnicosLoaded = !loadingUsers;
    const nextErrors = validateAssignForm({
      idUsuarioTecnico: form.idUsuarioTecnico,
      tecnicosLoaded,
      tecnicosCount: tecnicos.length,
    });

    setErrors(nextErrors);

    if (nextErrors.tecnico) {
      return;
    }

    if (
      !reporte?.idReporte ||
      !reporte?.idActivo ||
      reporte.idPrioridad == null ||
      reporte.idPrioridad === ""
    ) {
      toast.error("Faltan datos del reporte para asignar.");
      return;
    }

    if (
      !isValidPositiveInt(reporte.idReporte) ||
      !isValidPositiveInt(reporte.idActivo) ||
      !isValidPositiveInt(reporte.idPrioridad)
    ) {
      toast.error("Los identificadores del reporte no son válidos.");
      return;
    }

    if (!isValidPositiveInt(form.idUsuarioTecnico)) {
      setErrors((prev) => ({
        ...prev,
        tecnico: "Selecciona un técnico válido.",
      }));
      return;
    }

    const profile = getProfileFromToken();
    if (!profile?.id || !isValidPositiveInt(profile.id)) {
      toast.error("No se pudo obtener el usuario actual.");
      return;
    }

    const body = {
      idReporte: Number(reporte.idReporte),
      idActivo: Number(reporte.idActivo),
      idUsuarioTecnico: Number(form.idUsuarioTecnico),
      idUsuarioAdmin: Number(profile.id),
      idPrioridad: Number(reporte.idPrioridad),
      tipoAsignado: form.tipoAsignado,
      observaciones: form.observaciones.trim() || null,
    };

    setSubmitting(true);
    try {
      await solicitudesApi.mantenimientos.createMantenimiento(body);
      toast.success("Mantenimiento asignado correctamente");
      onAssigned?.();
      handleClose();
    } catch (err) {
      toast.error(err.message || "No se pudo asignar el técnico");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <FormModal
      open={open}
      onClose={handleClose}
      className="assign-tecnico-modal"
      title="Asignación de técnico"
      subtitle="Asigna un técnico a un reporte para mantenimiento"
      submitLabel="Asignar"
      submitIconSize={24}
      loading={submitting}
      submitDisabled={loadingUsers || (!loadingUsers && tecnicos.length === 0)}
      onSubmit={handleSubmit}
    >
      <div className="form-modal__field">
        <Select
          label="Nombre"
          labelClassName="form-modal__label"
          value={form.idUsuarioTecnico}
          onChange={(v) => {
            setForm((prev) => ({ ...prev, idUsuarioTecnico: v }));
            setErrors((prev) => ({ ...prev, tecnico: "" }));
          }}
          options={tecnicos}
          placeholder={
            loadingUsers ? "Cargando técnicos…" : "Selecciona un técnico"
          }
          variant="outline"
          required
          disabled={loadingUsers}
          error={errors.tecnico}
          ariaInvalid={!!errors.tecnico}
        />
        {!loadingUsers && tecnicos.length === 0 && (
          <p className="assign-tecnico-modal__hint" role="status">
            No hay técnicos disponibles. Contacta al administrador.
          </p>
        )}
      </div>

      <div className="form-modal__field">
        <Select
          label="Tipo de mantenimiento"
          labelClassName="form-modal__label"
          value={form.tipoAsignado}
          onChange={(v) =>
            setForm((prev) => ({ ...prev, tipoAsignado: v }))
          }
          options={[
            { value: "Correctivo", label: "Correctivo" },
            { value: "Preventivo", label: "Preventivo" },
          ]}
          variant="outline"
        />
      </div>

      <FormModalTextarea
        id="assign-tecnico-notas"
        label="Notas"
        placeholder="Describe el motivo de asignación, condiciones especiales del activo, etc."
        value={form.observaciones}
        onChange={(e) => {
          setForm((prev) => ({ ...prev, observaciones: e.target.value }));
        }}
        maxLength={FORM_MODAL_TEXTAREA_MAX}
        rows={5}
        textareaClassName="assign-tecnico-modal__textarea-tall"
      />
    </FormModal>
  );
}
