import InputField from "./InputField";
import Button from "../../../components/Button/Button";
import { Link } from "react-router-dom";

export default function EmailForm({ handleValidate }) {
  return (
    <>
      <div className="container me-5 p-4">
        <InputField
          label="Email"
          type="email"
          placeholder="Ingresa tu email"
          labelClassName="form-label text-muted small fw-bolder"
        />
        <Button text="Recuperar contraseña" onClick={handleValidate} fullWidth />
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
