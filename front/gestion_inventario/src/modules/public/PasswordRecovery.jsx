import { useState } from "react";

import LogoHeader from "./components/LogoHeader";
import ProgressBar from "./components/ProgressBar";
import EmailForm from "./components/ui/EmailForm";
import NewPasswordForm from "./components/ui/NewPasswordForm";

import "./public.css";

export default function PasswordRecovery() {
  const [validate, setValidate] = useState(false);

  const handleValidate = () => {
    setValidate(!validate);
  };

  return (
    <>
      <main className="bg-custom-layout">
        <div className="card-container">
          <div className="row g-0 flex-grow-1">
            <div className="col-sm-5 p-5 bg-white">
              <div
                className="d-flex flex-column h-100 justify-content-center"
                style={{ width: "33vw", marginRight: "1vw" }}
              >
                <LogoHeader containerClassName="text-end mb-4" />
                <form>
                  <div className="text-center container">
                    <h3 className="fw-bold">
                      {validate
                        ? "Por tu seguridad, vamos a crear una contraseña que solo tú conozcas"
                        : "¿Olvidaste tu contraseña?"}
                    </h3>
                    <p>
                      {validate
                        ? "Por favor, ingresa tu contraseña actual y tu nueva contraseña."
                        : "Por favor, ingresa tu correo electrónico."}
                    </p>
                  </div>
                  {validate ? (
                    <NewPasswordForm />
                  ) : (
                    <EmailForm handleValidate={handleValidate} />
                  )}
                </form>
              </div>
            </div>
            <div className="col-sm-7 open-box d-md-flex align-items-center justify-content-center">
              <div className="text-center w-100">
                <img
                  src="/src/assets/logo_forgot_pswd.png"
                  alt="imagen_acá"
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
