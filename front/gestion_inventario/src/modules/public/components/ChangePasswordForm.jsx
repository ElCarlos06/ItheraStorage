import { useState } from "react";
import { useNavigate } from "react-router-dom";
import PasswordInput from "./PasswordInput";
import Button from "../../../components/Button/Button";
import BackToLogin from "./BackToLogin";
import { api } from "../../../api/client";
import "../styles/public.css";

/**
 * Formulario unificado de cambio de contraseña.
 * Oculta el input de contraseña actual cuando viene del enlace (token).
 * @param {string} [correo] - Correo (primer acceso, requiere contraseña temporal)
 * @param {string} [token] - Token del enlace (olvidé contraseña, no requiere actual)
 * @param {function} [onSuccess] - Callback al completar
 */
export default function ChangePasswordForm({ correo, token, onSuccess }) {
  const navigate = useNavigate();
  const requiereActual = !!correo && !token;

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
    if (requiereActual && !form.contrasenaActual.trim()) {
      e.contrasenaActual = "Ingresa la contraseña temporal que recibiste por correo";
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
      e.confirmar = "Confirma tu nueva contraseña";
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
      const body = token
        ? { token, passwordNueva: form.nueva }
        : { correo, passwordActual: form.contrasenaActual, passwordNueva: form.nueva };
      await api.changePassword(body);

      if (onSuccess) {
        onSuccess();
      } else {
        navigate("/login", {
          state: { success: "Contraseña actualizada. Ya puedes iniciar sesión." },
        });
      }
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
            <p className="form-message form-message--error mb-3">{errores._form}</p>
          )}

          {requiereActual && (
            <>
              <div className="mb-3">
                <p className="text-muted small mb-2">
                  Revisa tu correo <strong>{correo}</strong> para obtener la contraseña temporal.
                </p>
              </div>
              <PasswordInput
                label="Contraseña temporal"
                placeholder="Pega la contraseña que recibiste por correo"
                labelClassName="form-label text-muted small fw-bolder"
                value={form.contrasenaActual}
                onChange={handleChange("contrasenaActual")}
                error={errores.contrasenaActual}
              />
            </>
          )}

          <PasswordInput
            label="Nueva contraseña"
            placeholder="Crea una contraseña segura (mín. 8 caracteres, mayúscula, número y @#$%&*!)"
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
            text={loading ? "Guardando..." : requiereActual ? "Crear contraseña" : "Guardar contraseña"}
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
