import "./Input.css";

export default function Input({
  label,
  labelClassName,
  endAdornment,
  fullWidth,
  error,
  className = "",
  id,
  ...props
}) {
  const inputId = id || (label ? `input-${label.replace(/\s/g, "-").toLowerCase()}` : undefined);
  const hasEnd = !!endAdornment;

  return (
    <div
      className={`input-wrap d-flex flex-column gap-2 w-100 ${fullWidth ? "input-wrap--fullWidth" : ""} ${error ? "input-wrap--error" : ""}`}
    >
      {label && (
        <label htmlFor={inputId} className={`input-wrap__label ${labelClassName || ""}`.trim()}>
          {label}
        </label>
      )}
      <div className="input-wrap__field position-relative d-flex align-items-center w-100">
        <input
          id={inputId}
          className={`input-wrap__input ${hasEnd ? "input-wrap__input--with-end" : ""} ${className}`.trim()}
          {...props}
        />
        {hasEnd && (
          <span className="input-wrap__end position-absolute top-50 end-0 translate-middle-y d-flex align-items-center justify-content-center">
            {endAdornment}
          </span>
        )}
      </div>
      {error && <span className="input-wrap__error">{error}</span>}
    </div>
  );
}
