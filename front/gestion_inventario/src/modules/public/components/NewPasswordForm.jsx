import { useState } from "react";
import PasswordInput from "./PasswordInput";
import Button from "../../../components/Button/Button";
import { Link, useNavigate } from "react-router-dom";
import { api } from "../../../api/client";

/**
 * Segundo paso del flujo de recuperacion/cambio de contrasena.
 * Recibe el correo ya verificado del paso anterior y llama a passwordRecovery.
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

  // Valida los campos del formulario en el cliente antes de llamar al backend
  const validarTodo = () => {
    const e = {};

    if (!form.contrasenaActual.trim()) {
      e.contrasenaActual = "La contrasena actual es obligatoria";
    }
    if (!form.nueva.trim()) {
      e.nueva = "La nueva contrasena es obligatoria";
    } else if (form.nueva.length < 8) {
      e.nueva = "La contrasena debe tener al menos 8 caracteres";
    }
    if (!form.confirmar.trim()) {
      e.confirmar = "Debes confirmar la nueva contrasena";
    } else if (form.nueva !== form.confirmar) {
      e.confirmar = "Las contrasenas no coinciden";
    }

    setErrores(e);
    return Object.keys(e).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validarTodo()) return;

    setLoading(true);
    try {
      // Llama al endpoint de cambio de contrasena con el correo verificado
      await api.passwordRecovery({
        correo,
        passwordActual: form.contrasenaActual,
        passwordNueva: form.nueva,
      });

      // En caso de exito redirige al login para que el usuario inicie sesion
      navigate("/login");
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
          {/* Error general devuelto por el servidor */}
          {errores._form && (
            <div className="alert alert-danger py-2 mb-3" role="alert">
              {errores._form}
            </div>
          )}

          <PasswordInput
            label="Contraseña Actual"
            placeholder="Ingresa tu contraseña actual"
            labelClassName="form-label text-muted small fw-bolder"
            value={form.contrasenaActual}
            onChange={handleChange("contrasenaActual")}
            error={errores.contrasenaActual}
          />

          <PasswordInput
            label="Nueva Contraseña"
            placeholder="Ingresa tu nueva contraseña"
            labelClassName="form-label text-muted small fw-bolder"
            value={form.nueva}
            onChange={handleChange("nueva")}
            error={errores.nueva}
          />

          <PasswordInput
            label="Confirmar Nueva Contraseña"
            placeholder="Repite la nueva contraseña"
            labelClassName="form-label text-muted small fw-bolder"
            value={form.confirmar}
            onChange={handleChange("confirmar")}
            error={errores.confirmar}
          />

          <Button
            text={loading ? "Guardando..." : "Guardar contrasena"}
            fullWidth
            type="submit"
            disabled={loading}
          />
        </div>
      </form>
      <div className="text-start">
        <Link
          to="/login"
          className="btn btn-link text-decoration-none small text-muted"
        >
          <i className="bi bi-arrow-left"></i> Regresar al Login
        </Link>
      </div>
    </>
  );
}
