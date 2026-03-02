export default function InputField({
  label,
  type = "text",
  placeholder,
  labelClassName = "form-label text-muted small fw-bolder",
}) {
  return (
    <div className="mb-3">
      <label className={labelClassName}>{label}</label>
      <input
        style={{ borderRadius: "12px" }}
        type={type}
        className="form-control fw-normal"
        placeholder={placeholder}
      />
    </div>
  );
}
