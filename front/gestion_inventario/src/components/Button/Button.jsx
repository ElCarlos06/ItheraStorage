import { isValidElement } from "react";
import { motion } from "motion/react";
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
    <motion.button
      type={type}
      onClick={onClick}
      disabled={disabled}
      className={`btn btn--${size} ${variantClass} ${fullWidth ? "btn--full" : ""} ${className}`.trim()}
      whileTap={disabled ? undefined : { scale: 0.96 }}
      transition={{ type: "spring", stiffness: 400, damping: 17 }}
    >
      {iconLeft && (
        <span className="btn__icon btn__icon--left">
          {isValidElement(iconLeft) ? (
            iconLeft
          ) : (
            <Icon icon={iconLeft} size={iconSize} />
          )}
        </span>
      )}
      {content}
      {iconRight && (
        <span className="btn__icon btn__icon--right">
          {isValidElement(iconRight) ? (
            iconRight
          ) : (
            <Icon icon={iconRight} size={iconSize} />
          )}
        </span>
      )}
    </motion.button>
  );
}
