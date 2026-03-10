import { useState } from "react";
import InputField from "./InputField";
import Button from "../../../components/Button/Button";
import { Link } from "react-router-dom";
import { api } from "../../../api/client";
import { validarCorreoLogin } from "../../../utils/validaciones";

/**
 * Primer paso del flujo de recuperacion/cambio de contrasena.
 * Busca al usuario por correo en el backend y, si existe, avanza al siguiente paso.
 * @param {function} onVerificado - Callback que recibe el correo verificado
 */
export default function EmailForm({ onVerificado }) {
  const [correo, setCorreo] = useState("");
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setCorreo(e.target.value);
    setError(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Validacion del formato del correo antes de llamar al backend
    const errorFormato = validarCorreoLogin(correo);
    if (errorFormato) {
      setError(errorFormato);
      return;
    }

    setLoading(true);
    try {
      // Verifica que el correo pertenece a un usuario registrado
      await api.getUserByCorreo(correo.trim().toLowerCase());

      // Si el usuario existe, avanza al paso de nueva contrasena
      onVerificado(correo.trim().toLowerCase());
    } catch (err) {
      // El backend devuelve 404 si el correo no esta registrado
      setError(err.message || "No se encontro una cuenta con ese correo");
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <form onSubmit={handleSubmit}>
        <div className="container me-5 p-4">
          {/* Error devuelto por el backend (correo no encontrado u otro) */}
          {error && (
            <div className="alert alert-danger py-2 mb-3 text-center" role="alert">
              {error}
            </div>
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
            text={loading ? "Verificando..." : "Recuperar contraseña"}
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
