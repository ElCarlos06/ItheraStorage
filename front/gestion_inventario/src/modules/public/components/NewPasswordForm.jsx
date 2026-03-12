import { useState } from "react";
import PasswordInput from "./PasswordInput";
import Button from "../../../components/Button/Button";
import { useNavigate } from "react-router-dom";
import { authApi } from "../../../api/authApi";
import BackToLogin from "./BackToLogin";

/**
 * Formulario para el cambio de contraseña cuando el usuario ya tiene una contraseña establecida.
 * Usado en el flujo "¿Olvidaste tu contraseña?" cuando el usuario recuerda su contraseña actual.
 * @param {string} correo - Correo verificado en el paso anterior
 */
export default function NewPasswordForm({ correo }) {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    contrasenaActual: "",
    nueva: "",
    confirmar: "",
  });
  const [errores, setErrores] = useState({});
  const [loading, setLoading] = useState(false);

  const handleChange = (field) => (e) => {
    setForm((prev) => ({ ...prev, [field]: e.target.value }));
    setErrores((prev) => ({ ...prev, [field]: null, _form: null }));
  };

  const validarTodo = () => {
    const e = {};
    if (!form.contrasenaActual.trim()) {
      e.contrasenaActual = "La contraseña actual es obligatoria";
    }
    if (!form.nueva.trim()) {
      e.nueva = "La nueva contraseña es obligatoria";
    } else if (form.nueva.length < 8) {
      e.nueva = "La contraseña debe tener al menos 8 caracteres";
    } else {
      const hasUpper = /[A-Z]/.test(form.nueva);
      const hasDigit = /\d/.test(form.nueva);
      const hasSpecial = /[@#$%&*!]/.test(form.nueva);
      if (!hasUpper || !hasDigit || !hasSpecial) {
        e.nueva = "Debe incluir mayúscula, número y carácter especial (@#$%&*!)";
      }
    }
    if (!form.confirmar.trim()) {
      e.confirmar = "Debes confirmar la nueva contraseña";
    } else if (form.nueva !== form.confirmar) {
      e.confirmar = "Las contraseñas no coinciden";
    }
    setErrores(e);
    return Object.keys(e).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validarTodo()) return;

    setLoading(true);
    try {
      await authApi.changePassword({
        correo,
        passwordActual: form.contrasenaActual,
        passwordNueva: form.nueva,
      });
      navigate("/login", { state: { success: "Contraseña actualizada. Ya puedes iniciar sesión." } });
    } catch (err) {
      setErrores((prev) => ({ ...prev, _form: err.message }));
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <form onSubmit={handleSubmit}>
        <div className="container me-5 p-4">
          {errores._form && (
            <p className="form-message form-message--error mb-3" role="alert">
              {errores._form}
            </p>
          )}

          <PasswordInput
            label="Contraseña actual"
            placeholder="Ingresa tu contraseña actual"
            labelClassName="form-label text-muted small fw-bolder"
            value={form.contrasenaActual}
            onChange={handleChange("contrasenaActual")}
            error={errores.contrasenaActual}
          />

          <PasswordInput
            label="Nueva contraseña"
            placeholder="Ingresa tu nueva contraseña"
            labelClassName="form-label text-muted small fw-bolder"
            value={form.nueva}
            onChange={handleChange("nueva")}
            error={errores.nueva}
          />

          <PasswordInput
            label="Confirmar nueva contraseña"
            placeholder="Repite la nueva contraseña"
            labelClassName="form-label text-muted small fw-bolder"
            value={form.confirmar}
            onChange={handleChange("confirmar")}
            error={errores.confirmar}
          />

          <Button
            text={loading ? "Guardando..." : "Guardar contraseña"}
            fullWidth
            type="submit"
            disabled={loading}
          />
        </div>
      </form>
      <BackToLogin />
    </>
  );
}
