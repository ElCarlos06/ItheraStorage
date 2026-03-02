import { Link } from "react-router-dom";
import "./public.css";
import Button from "./components/Button";
import InputField from "./components/InputField";
import PasswordInput from "./components/PasswordInput";
import LogoHeader from "./components/LogoHeader";
import ProgressBar from "./components/ProgressBar";

export default function Login() {
  return (
    <>
      <main className="bg-custom-layout">
        <div className="card-container">
          <div className="row g-0 flex-grow-1">
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
            <div className="col-sm-5 p-5 bg-white">
              <div
                className="d-flex flex-column h-100 justify-content-center mt-4"
                style={{ width: "33vw", marginLeft: "1vw" }}
              >
                <LogoHeader containerClassName="container text-end mb-4" />
                <form>
                  <div className="container me-5 p-4">
                    <InputField
                      label="Email"
                      type="email"
                      placeholder="Ingresa tu email"
                      labelClassName="form-label text-muted small fw-bolder"
                    />
                    <PasswordInput
                      label="Contraseña"
                      placeholder="Ingresa la contraseña"
                      labelClassName="form-label text-muted small fw-bold"
                    />
                    <div className="text-end mb-3">
                      <Link
                        to="/password-recovery"
                        className="text-decoration-none small text-primary"
                      >
                        ¿Olvidaste tu contraseña?
                      </Link>
                    </div>
                    <Button text="Iniciar sesión" />
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
