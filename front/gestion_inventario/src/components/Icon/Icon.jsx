import "./Icon.css";

export default function Icon({ icon: IconComponent, size, className, style, ...rest }) {
  if (!IconComponent) return null;
  const dimension = typeof size === "number" ? `${size}px` : size;
  const wrapperStyle = dimension
    ? { width: dimension, height: dimension, minWidth: dimension, minHeight: dimension, ...style }
    : style;
  const classes = ["icon-wrap", className].filter(Boolean).join(" ");
  const iconProps = dimension ? { size: typeof size === "number" ? size : 24, ...rest } : rest;
  return (
    <span className={classes} style={wrapperStyle} aria-hidden>
      <IconComponent {...iconProps} />
    </span>
  );
}
