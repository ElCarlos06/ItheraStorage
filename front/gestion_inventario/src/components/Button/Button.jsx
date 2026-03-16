import { isValidElement } from "react";
import Icon from "../Icon/Icon";
import "./Button.css";

const VARIANTS = {
  primary: "btn--primary",
  secondary: "btn--secondary",
  outline: "btn--outline",
  tertiary: "btn--tertiary",
  ghost: "btn--ghost",
};

export default function Button({
  text,
  children,
  onClick,
  type = "button",
  variant = "primary",
  size = "medium",
  iconLeft,
  iconRight,
  iconSize = 30,
  disabled,
  fullWidth,
  className = "",
}) {
  const content = children ?? text;
  const variantClass = VARIANTS[variant] ?? VARIANTS.primary;

  return (
    <button
      type={type}
      onClick={onClick}
      disabled={disabled}
      className={`btn btn--${size} ${variantClass} ${fullWidth ? "btn--full" : ""} ${className}`.trim()}
    >
      {iconLeft && (
        <span className="btn__icon btn__icon--left">
          {isValidElement(iconLeft) ? iconLeft : <Icon icon={iconLeft} size={iconSize} />}
        </span>
      )}
      {content}
      {iconRight && (
        <span className="btn__icon btn__icon--right">
          {isValidElement(iconRight) ? iconRight : <Icon icon={iconRight} size={iconSize} />}
        </span>
      )}
    </button>
  );
}
