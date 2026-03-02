import { useState } from "react";

export default function PasswordInput({
  label = "Contraseña",
  placeholder = "Ingresa la contraseña",
  labelClassName = "form-label text-muted small fw-bold",
}) {
  const [showPassword, setShowPassword] = useState(false);

  const togglePasswordVisibility = () => {
    setShowPassword(!showPassword);
  };

  return (
    <div className="mb-3">
      <label className={labelClassName}>{label}</label>
      <div className="position-relative">
        <input
          style={{ borderRadius: "12px" }}
          type={showPassword ? "text" : "password"}
          className="form-control fw-normal pe-5"
          placeholder={placeholder}
        />
        <span
          onClick={togglePasswordVisibility}
          className="position-absolute end-0 top-50 translate-middle-y me-3"
          style={{ cursor: "pointer", zIndex: 10 }}
        >
          <i
            className={`bi ${showPassword ? "bi-eye" : "bi-eye-slash"} text-muted`}
          ></i>
        </span>
      </div>
    </div>
  );
}
