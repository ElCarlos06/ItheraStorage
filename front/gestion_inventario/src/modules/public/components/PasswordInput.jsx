import { useState } from "react";
import Input from "../../../components/Input/Input";

export default function PasswordInput({
  label = "Contraseña",
  placeholder = "Ingresa la contraseña",
  labelClassName,
  fullWidth,
  ...props
}) {
  const [showPassword, setShowPassword] = useState(false);

  return (
    <div className="mb-3">
      <Input
        label={label}
        type={showPassword ? "text" : "password"}
        placeholder={placeholder}
        labelClassName={labelClassName}
        fullWidth={fullWidth}
        endAdornment={
          <span
            onClick={() => setShowPassword((v) => !v)}
            onKeyDown={(e) => e.key === "Enter" && setShowPassword((v) => !v)}
            role="button"
            tabIndex={0}
            aria-label={showPassword ? "Ocultar contraseña" : "Mostrar contraseña"}
          >
            <i className={`bi ${showPassword ? "bi-eye" : "bi-eye-slash"} text-muted`} aria-hidden />
          </span>
        }
        {...props}
      />
    </div>
  );
}
