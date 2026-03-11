import { useState } from "react";
import InputField from "./InputField";
import Button from "../../../components/Button/Button";
import BackToLogin from "./BackToLogin";
import { api } from "../../../api/client";
import { validarCorreoLogin } from "../../../utils/validaciones";

/**
 * Flujo "¿Olvidaste tu contraseña?".
 * Envía una nueva contraseña temporal al correo. El usuario la usa para iniciar sesión
 * y luego se le pedirá cambiarla (primera vez).
 */
export default function EmailForm() {
  const [correo, setCorreo] = useState("");
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  const handleChange = (e) => {
    setCorreo(e.target.value);
    setError(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const errorFormato = validarCorreoLogin(correo);
    if (errorFormato) {
      setError(errorFormato);
      return;
    }

    setLoading(true);
    setError(null);
    try {
      await api.requestPasswordReset(correo.trim().toLowerCase());
      setSuccess(true);
    } catch (err) {
      setError(err.message || "No se encontro una cuenta con ese correo");
    } finally {
      setLoading(false);
    }
  };

  if (success) {
    return (
      <div className="container me-5 p-4">
        <div className="form-message form-message--success py-3 mb-3">
          <p className="mb-0">
            Revisa tu correo <strong>{correo}</strong>.
          </p>
          <p className="mb-0 mt-2 small">
            Te enviamos un enlace para restablecer tu contraseña. Haz clic en él y elige la contraseña que desees.
          </p>
        </div>
        <BackToLogin />
      </div>
    );
  }

  return (
    <>
      <form onSubmit={handleSubmit}>
        <div className="container me-5 p-4">
          {error && (
            <p className="form-message form-message--error mb-3 text-center">
              {error}
            </p>
          )}
          <InputField
            label="Email"
            type="email"
            placeholder="Ingresa tu email"
            labelClassName="form-label text-muted small fw-bolder"
            value={correo}
            onChange={handleChange}
          />
          <Button
            text={loading ? "Enviando..." : "Enviar enlace al correo"}
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
