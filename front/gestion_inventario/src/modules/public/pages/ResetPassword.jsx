import { useSearchParams } from "react-router-dom";
import LogoHeader from "../components/LogoHeader";
import BackToLogin from "../components/BackToLogin";
import ChangePasswordForm from "../components/ChangePasswordForm";
import "../styles/public.css";

/**
 * Página para restablecer contraseña mediante el token del enlace enviado por correo.
 * Usa el mismo ChangePasswordForm que el primer acceso, pero ocultando contraseña actual.
 */
export default function ResetPassword() {
  const [searchParams] = useSearchParams();
  const token = searchParams.get("token");

  if (!token) {
    return (
      <main className="bg-custom-layout">
        <div className="card-container">
          <div className="row g-0 flex-grow-1">
            <div className="col-sm-5 p-5 bg-white d-flex flex-column justify-content-center">
              <LogoHeader containerClassName="text-end mb-4" />
              <p className="form-message form-message--warning">
                Enlace inválido o expirado. Solicita uno nuevo desde "¿Olvidaste tu contraseña?".
              </p>
              <BackToLogin />
            </div>
            <div className="col-sm-7 open-box" />
          </div>
        </div>
      </main>
    );
  }

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
              <div className="text-center container mb-3">
                <h3 className="fw-bold">Elige tu nueva contraseña</h3>
                <p>
                  Ingresa la contraseña que deseas usar. Debe tener al menos 8 caracteres,
                  una mayúscula, un número y un carácter especial (@#$%&*!).
                </p>
              </div>
              <ChangePasswordForm token={token} />
            </div>
          </div>
          <div className="col-sm-7 open-box d-md-flex align-items-center justify-content-center">
            <div className="text-center w-100">
              <img
                src="/src/assets/logo_forgot_pswd.png"
                alt="Restablecer contraseña"
                className="img-fluid illustration-img"
              />
            </div>
          </div>
        </div>
      </div>
    </main>
  );
}
