import { useLocation, useNavigate } from "react-router-dom";
import LogoHeader from "../components/LogoHeader";
import ProgressBar from "../components/ProgressBar";
import EmailForm from "../components/EmailForm";
import ChangePasswordForm from "../components/ChangePasswordForm";
import "../styles/public.css";

export default function PasswordRecovery() {
  const location = useLocation();
  const navigate = useNavigate();

  // Cuando el login detecta un 403 con requiresPasswordChange, redirige aquí
  const correoInicial = location.state?.correo ?? null;
  const forzarCambio = location.state?.forzarCambio ?? false;

  const handleFirstTimeSuccess = () => {
    navigate("/login", {
      state: { success: "Contraseña creada. Ya puedes iniciar sesión." },
    });
  };

  return (
    <main className="bg-custom-layout">
      <div className="card-container">
        <div className="row g-0 flex-grow-1">
          <div className="col-sm-5 p-5 bg-white">
            <div
              className="d-flex flex-column h-100 justify-content-center"
              style={{ width: "33vw", marginRight: "1vw" }}
            >
              <LogoHeader containerClassName="text-end mb-4" />

              <div className="text-center container">
                <h3 className="fw-bold">
                  {forzarCambio
                    ? "Bienvenido. Configura tu contraseña"
                    : "¿Olvidaste tu contraseña?"}
                </h3>
                <p>
                  {forzarCambio
                    ? "Usa la contraseña temporal que recibiste por correo e ingresa tu nueva contraseña."
                    : "Ingresa tu correo y te enviaremos un enlace para restablecer tu contraseña."}
                </p>
              </div>

              {forzarCambio ? (
                <ChangePasswordForm
                  correo={correoInicial}
                  onSuccess={handleFirstTimeSuccess}
                />
              ) : (
                <EmailForm />
              )}
            </div>
          </div>

          <div className="col-sm-7 open-box d-md-flex align-items-center justify-content-center">
            <div className="text-center w-100">
              <img
                src="/src/assets/logo_forgot_pswd.png"
                alt="Recuperar contraseña"
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
  );
}
