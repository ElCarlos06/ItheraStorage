import { useState } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import Button from "../../../components/Button/Button";
import InputField from "../components/InputField";
import PasswordInput from "../components/PasswordInput";
import LogoHeader from "../components/LogoHeader";
import ProgressBar from "../components/ProgressBar";
import { api } from "../../../api/client";
import {
  validarCorreoLogin,
  validarContrasenaLogin,
} from "../../../utils/validaciones";
import "../styles/public.css";

export default function Login() {
  // Estado del formulario
  const [form, setForm] = useState({ correo: "", contrasena: "" });

  // Errores de validación por campo y error general del servidor
  const [errores, setErrores] = useState({});

  // Indica si la petición al backend está en progreso
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();
  const location = useLocation();
  const successMessage = location.state?.success;

  // Actualiza el valor del campo y limpia su error al escribir
  const handleChange = (field) => (e) => {
    const value = e.target.value;
    setForm((prev) => ({ ...prev, [field]: value }));
    setErrores((prev) => ({ ...prev, [field]: null, _form: null }));
  };

  // Ejecuta todas las validaciones del cliente y guarda los mensajes de error
  const validarTodo = () => {
    const e = {};
    e.correo = validarCorreoLogin(form.correo);
    e.contrasena = validarContrasenaLogin(form.contrasena);
    setErrores(e);
    return !Object.values(e).some(Boolean);
  };

  // Envía las credenciales al backend y, si son correctas, guarda el token
  // y redirige al área protegida forzando una recarga para que App.jsx reevalúe el token
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validarTodo()) return;

    setLoading(true);
    try {
      const data = await api.login({
        correo: form.correo.trim().toLowerCase(),
        password: form.contrasena,
      });

      // Guarda el token recibido del backend en sessionStorage
      sessionStorage.setItem("token", data.token ?? data.data?.token ?? "");

      // Recarga completa para que App.jsx detecte el token y monte AdminRouter
      window.location.replace("/");
    } catch (err) {
      // El backend responde 403 cuando el usuario existe pero debe cambiar
      // su contraseña antes de continuar; se redirige al flujo de cambio
      if (err.status === 403 && err.data?.data?.requiresPasswordChange) {
        const correoParaCambio =
          err.data.data.correo || form.correo.trim().toLowerCase();
        navigate("/password-recovery", {
          state: { correo: correoParaCambio, forzarCambio: true },
        });
        return;
      }

      // El backend puede devolver un error de validacion de Spring con el mensaje
      // "Validation failed for object='authDTO'" cuando el usuario aun no ha
      // establecido su contrasena definitiva; se reemplaza por un texto claro
      const mensaje = err.message?.toLowerCase().includes("validation failed")
        ? "Debes cambiar tu contraseña antes de iniciar sesión. Si es tu primera vez, utiliza la opción de '¿Olvidaste tu contraseña?'."
        : err.message;

      setErrores((prev) => ({ ...prev, _form: mensaje }));
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <main className="bg-custom-layout">
        <div className="card-container">
          <div className="row g-0 flex-grow-1">
            {/* Panel izquierdo decorativo */}
            <div className="col-sm-7 open-box d-md-flex align-items-center justify-content-center transition-all side-panel">
              <div className="text-center w-100">
                <img
                  src="/src/assets/logo_login.png"
                  alt="imagen_acá"
                  className="img-fluid illustration-img"
                />
                <div className="container mt-3">
                  <h1 className="fw-bolder">Control de Activos Inteligente</h1>
                  <p className="fw-medium">
                    Gestiona todos tus activos tecnológicos con códigos QR de
                    forma rápida y eficiente.
                  </p>
                  <ProgressBar step={1} containerClassName="progress" />
                </div>
              </div>
            </div>

            {/* Panel derecho con el formulario */}
            <div className="col-sm-5 p-5 bg-white">
              <div
                className="d-flex flex-column h-100 justify-content-center mt-4"
                style={{ width: "33vw", marginLeft: "1vw" }}
              >
                <LogoHeader containerClassName="container text-end mb-4" />

                <form onSubmit={handleSubmit}>
                  <div className="container me-5 p-4">
                    {successMessage && (
                      <p className="form-message form-message--success mb-3 text-center">
                        {successMessage}
                      </p>
                    )}
                    {errores._form && (
                      <p className="form-message form-message--error mb-3 text-center">
                        {errores._form}
                      </p>
                    )}

                    <InputField
                      label="Email"
                      type="email"
                      placeholder="Ej: usuario@ejemplo.com"
                      fullWidth
                      value={form.correo}
                      onChange={handleChange("correo")}
                      error={errores.correo}
                    />

                    <PasswordInput
                      label="Contraseña"
                      placeholder="Ingresa la contraseña"
                      fullWidth
                      value={form.contrasena}
                      onChange={handleChange("contrasena")}
                      error={errores.contrasena}
                    />

                    <div className="text-end mb-3">
                      <Link
                        to="/password-recovery"
                        className="text-decoration-none small text-primary"
                      >
                        ¿Olvidaste tu contraseña?
                      </Link>
                    </div>

                    <Button
                      text={loading ? "Ingresando..." : "Iniciar sesión"}
                      fullWidth
                      type="submit"
                      disabled={loading}
                    />
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </main>
    </>
  );
}
