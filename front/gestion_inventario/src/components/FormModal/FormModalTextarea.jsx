import { useId } from "react";
import { FORM_MODAL_TEXTAREA_MAX } from "../../constants/formLimits";
import "./FormModal.css";

/**
 * Textarea de modal con contador `actual / máximo` y mismo comportamiento de redimensionado.
 */
export default function FormModalTextarea({
  label,
  value,
  onChange,
  placeholder,
  maxLength = FORM_MODAL_TEXTAREA_MAX,
  rows = 4,
  error,
  className = "",
  textareaClassName = "",
  labelClassName = "form-modal__label",
  id: idProp,
  ...rest
}) {
  const uid = useId();
  const fieldId = idProp || `fmta-${uid.replace(/:/g, "")}`;
  const len = (value ?? "").length;

  return (
    <div className={`form-modal__field ${className}`.trim()}>
      {label != null && label !== "" && (
        <label htmlFor={fieldId} className={labelClassName}>
          {label}
        </label>
      )}
      <textarea
        id={fieldId}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        maxLength={maxLength}
        rows={rows}
        className={`form-modal__textarea ${error ? "form-modal__textarea--error" : ""} ${textareaClassName}`.trim()}
        aria-invalid={!!error}
        aria-describedby={`${fieldId}-meta`}
        {...rest}
      />
      <div id={`${fieldId}-meta`} className="form-modal__textarea-meta">
        <span className="form-modal__textarea-error-wrap">{error}</span>
        <span className="form-modal__textarea-counter">
          {len} / {maxLength}
        </span>
      </div>
    </div>
  );
}
