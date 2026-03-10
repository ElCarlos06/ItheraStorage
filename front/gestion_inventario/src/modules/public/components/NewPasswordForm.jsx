import InputField from "./InputField";
import Button from "../../../components/Button/Button";
import { Link } from "react-router-dom";

export default function NewPasswordForm() {
  return (
    <>
      <div className="container me-5 p-4">
        <InputField
          label="Nueva Contraseña"
          type="password"
          placeholder="Ingresa tu nueva contraseña"
          labelClassName="form-label text-muted small fw-bolder"
        />
        <InputField
          label="Confirmar Contraseña"
          type="password"
          placeholder="Confirma tu nueva contraseña"
          labelClassName="form-label text-muted small fw-bolder"
        />
        <InputField
          label="Código de verificación"
          type="text"
          placeholder="Ingresa el código"
          labelClassName="form-label text-muted small fw-bolder"
        />
        <Button text="Guardar contraseña" fullWidth />
      </div>
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
