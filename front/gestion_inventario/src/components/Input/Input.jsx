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
    <div className={`input-wrap ${fullWidth ? "input-wrap--fullWidth" : ""} ${error ? "input-wrap--error" : ""}`}>
      {label && (
        <label htmlFor={inputId} className={`input-wrap__label ${labelClassName || ""}`.trim()}>
          {label}
        </label>
      )}
      <div className="input-wrap__field">
        <input
          id={inputId}
          className={`input-wrap__input ${hasEnd ? "input-wrap__input--with-end" : ""} ${className}`.trim()}
          {...props}
        />
        {hasEnd && <span className="input-wrap__end">{endAdornment}</span>}
      </div>
      {error && <span className="input-wrap__error">{error}</span>}
    </div>
  );
}
