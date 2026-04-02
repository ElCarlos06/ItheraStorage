import { useState, useEffect } from "react";
import FormModal from "../../../../../components/FormModal/FormModal";
import FormModalTextarea from "../../../../../components/FormModal/FormModalTextarea";
import Select from "../../../../../components/Select/Select";
import { usersApi } from "../../../../../api/usersApi";
import { toast } from "../../../../../utils/toast.jsx";
import { GenericPlus } from "@heathmont/moon-icons";
import "./AssignResguardoModal.css";

function toOptions(items) {
  const list = Array.isArray(items) ? items : [];
  return list
    .map((item) => {
      const id = item.id ?? item.id_usuario;
      const label = item.nombre ?? item.nombreCompleto ?? item.correo ?? "—";
      const value = id != null && id !== "" ? String(id) : "";
      return { value, label };
    })
    .filter((o) => o.value !== "");
}

export default function AssignResguardoModal({ open, onClose, onGuardar }) {
  const [form, setForm] = useState({
    idEmpleado: "",
    observaciones: "",
  });

  const [usersOptions, setUsersOptions] = useState([]);
  const [loadingUsers, setLoadingUsers] = useState(false);

  // Cargar usuarios cuando se abre el modal
  useEffect(() => {
    if (!open) return;
    setLoadingUsers(true);

    // Ajusta la obtención según cómo esté configurado usersApi, usamos pageSize amplio para tener la lista completa
    usersApi
      .getUsers(0, 100)
      .then((res) => {
        const content = res?.data?.content ?? res?.content ?? res?.data ?? [];
        const esEmpleado = (u) => {
          const rol = (u.role?.nombre ?? u.rol?.nombre ?? "").toString().toLowerCase();
          return rol === "empleado";
        };
        const activos = content.filter(
          (u) => (u.esActivo ?? u.es_activo ?? true) && esEmpleado(u),
        );

        setUsersOptions(toOptions(activos));
      })
      .catch((err) => {
        console.error("Error cargando usuarios", err);
        setUsersOptions([]);
      })
      .finally(() => setLoadingUsers(false));
  }, [open]);

  // Limpiar el form cuando se abre/cierra
  useEffect(() => {
    if (open) {
      setForm({
        idEmpleado: "",
        observaciones: "",
      });
    }
  }, [open]);

  const handleChange = (field) => (e) => {
    setForm((prev) => ({ ...prev, [field]: e.target.value }));
  };

  const handleSelect = (field) => (value) => {
    setForm((prev) => ({ ...prev, [field]: value }));
  };

  const validarCampos = () => {
    if (!form.idEmpleado) {
      toast.error("Por favor, selecciona un empleado.");
      return false;
    }
    return true;
  };

  const handleSubmit = () => {
    if (!validarCampos()) return;

    const payload = {
      idEmpleado: Number(form.idEmpleado),
      observaciones: form.observaciones.trim() || null,
    };

    onGuardar?.(payload);
    // Nota: dejaremos que el padre maneje el cierre (onClose) si el guardado en la API es exitoso o limpiar el form si se desea.
  };

  const handleClose = () => {
    setForm({
      idEmpleado: "",
      observaciones: "",
    });
    onClose?.();
  };

  return (
    <FormModal
      open={open}
      onClose={handleClose}
      className="assign-resguardo-modal"
      title="Asignar Resguardo"
      subtitle="Asigna un activo a un empleado bajo resguardo"
      submitLabel="Asignar Resguardo"
      submitIcon={GenericPlus}
      submitIconSize={24}
      onSubmit={handleSubmit}
    >
      <div className="form-modal__field">
        <Select
          label="EMPLEADO"
          labelClassName="form-modal__label"
          value={form.idEmpleado}
          onChange={handleSelect("idEmpleado")}
          options={usersOptions}
          placeholder={
            loadingUsers ? "Cargando empleados..." : "Selecciona un empleado"
          }
          className="assign-resguardo-modal__select"
          required
          variant="outline"
        />
      </div>

      <FormModalTextarea
        label="OBSERVACIONES"
        placeholder="Describe el motivo de asignación, condiciones especiales del activo, etc."
        value={form.observaciones}
        onChange={handleChange("observaciones")}
        textareaClassName="assign-resguardo-modal__textarea"
        rows={5}
      />
    </FormModal>
  );
}
