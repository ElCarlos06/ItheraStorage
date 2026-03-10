import Input from "../../../components/Input/Input";

export default function InputField({
  label,
  type = "text",
  placeholder,
  labelClassName,
  fullWidth,
  ...props
}) {
  return (
    <div className="mb-3">
      <Input
        label={label}
        type={type}
        placeholder={placeholder}
        labelClassName={labelClassName}
        fullWidth={fullWidth}
        {...props}
      />
    </div>
  );
}
