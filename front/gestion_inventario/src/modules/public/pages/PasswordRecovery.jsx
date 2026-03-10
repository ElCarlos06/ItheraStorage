import { useState } from "react";
import { useLocation } from "react-router-dom";
import LogoHeader from "../components/LogoHeader";
import ProgressBar from "../components/ProgressBar";
import EmailForm from "../components/EmailForm";
import NewPasswordForm from "../components/NewPasswordForm";
import "../styles/public.css";

export default function PasswordRecovery() {
  const location = useLocation();

  // Cuando el login detecta un 403 con requiresPasswordChange, redirige aqui
  // pasando el correo y forzarCambio en el state de navegacion
  const correoInicial = location.state?.correo ?? null;
  const forzarCambio = location.state?.forzarCambio ?? false;

  // Si viene del flujo de cambio forzado se salta directo al paso 2
  const [correoVerificado, setCorreoVerificado] = useState(
    forzarCambio ? correoInicial : null,
  );

  // El paso actual se determina por si ya se verifico el correo
  const enPasoContrasena = correoVerificado !== null;

  return (
    <>
      <main className="bg-custom-layout">
        <div className="card-container">
          <div className="row g-0 flex-grow-1">
            {/* Panel izquierdo con el formulario correspondiente al paso actual */}
            <div className="col-sm-5 p-5 bg-white">
              <div
                className="d-flex flex-column h-100 justify-content-center"
                style={{ width: "33vw", marginRight: "1vw" }}
              >
                <LogoHeader containerClassName="text-end mb-4" />

                <div className="text-center container">
                  <h3 className="fw-bold">
                    {enPasoContrasena
                      ? "Por tu seguridad, crea una nueva contraseña"
                      : "¿Olvidaste tu contraseña?"}
                  </h3>
                  <p>
                    {enPasoContrasena
                      ? "Ingresa tu contraseña actual y la nueva contraseña."
                      : "Ingresa tu correo electrónico para continuar."}
                  </p>
                </div>

                {enPasoContrasena ? (
                  // Paso 2: cambio de contrasena con el correo ya verificado
                  <NewPasswordForm correo={correoVerificado} />
                ) : (
                  // Paso 1: verificacion del correo
                  <EmailForm onVerificado={setCorreoVerificado} />
                )}
              </div>
            </div>

            {/* Panel derecho decorativo */}
            <div className="col-sm-7 open-box d-md-flex align-items-center justify-content-center">
              <div className="text-center w-100">
                <img
                  src="/src/assets/logo_forgot_pswd.png"
                  alt="imagen_aca"
                  className="img-fluid illustration-img"
                />
                <div className="d-flex align-items-center justify-content-center">
                  <ProgressBar step={2} containerClassName="progress mt-5" />
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </>
  );
}
