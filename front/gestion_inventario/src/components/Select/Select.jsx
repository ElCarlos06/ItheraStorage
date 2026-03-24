import { useState, useRef, useEffect, useLayoutEffect } from "react";
import { createPortal } from "react-dom";
import Icon from "../Icon/Icon";
import { ControlsChevronDown } from "@heathmont/moon-icons";
import "./Select.css";

/**
 * Select reutilizable con dropdown custom.
 * @param {Object} props
 * @param {string} props.value - Valor seleccionado
 * @param {function} props.onChange - (value) => void
 * @param {Array<{value: string, label: string}>} props.options - Opciones
 * @param {string} [props.placeholder] - Texto cuando no hay selección
 * @param {string} [props.label] - Etiqueta
 * @param {string} [props.labelClassName] - Clase para la etiqueta
 * @param {boolean} [props.disabled]
 * @param {boolean} [props.required]
 * @param {boolean} [props.ariaInvalid]
 * @param {string} [props.className]
 * @param {string} [props.size] - "sm" | "md" (default: md)
 * @param {string} [props.variant] - "default" | "ghost" (ghost para fondos oscuros)
 */
export default function Select({
  value,
  onChange,
  options = [],
  placeholder = "Seleccionar...",
  label,
  labelClassName,
  disabled = false,
  required,
  ariaInvalid,
  error,
  className = "",
  size = "md",
  variant = "default",
}) {
  const [open, setOpen] = useState(false);
  const [dropdownStyle, setDropdownStyle] = useState(null);
  const containerRef = useRef(null);
  const triggerRef = useRef(null);

  const selectedOption = options.find((o) => String(o.value) === String(value));
  const displayText = selectedOption ? selectedOption.label : placeholder;

  useLayoutEffect(() => {
    if (!open) {
      setDropdownStyle(null);
      return;
    }
    const updatePosition = () => {
      if (!triggerRef.current) return;
      const rect = triggerRef.current.getBoundingClientRect();
      const spaceBelow = window.innerHeight - rect.bottom;
      const dropdownHeight = Math.min(280, Math.max(options.length * 48 + 12, 80));
      const openDown = spaceBelow >= dropdownHeight || spaceBelow >= rect.top;

      setDropdownStyle({
        position: "fixed",
        left: rect.left,
        width: Math.max(rect.width, 180),
        top: openDown ? rect.bottom + 6 : undefined,
        bottom: openDown ? undefined : window.innerHeight - rect.top + 6,
        zIndex: 10050,
      });
    };
    updatePosition();
    window.addEventListener("scroll", updatePosition, true);
    window.addEventListener("resize", updatePosition);
    return () => {
      window.removeEventListener("scroll", updatePosition, true);
      window.removeEventListener("resize", updatePosition);
    };
  }, [open, options.length]);

  useEffect(() => {
    if (!open) return;
    const handleClickOutside = (e) => {
      if (containerRef.current?.contains(e.target)) return;
      const dropdowns = document.querySelectorAll(".select-wrap__dropdown--portal");
      for (const dd of dropdowns) {
        if (dd.contains(e.target)) return;
      }
      setOpen(false);
    };
    const handleEscape = (e) => {
      if (e.key === "Escape") setOpen(false);
    };
    document.addEventListener("mousedown", handleClickOutside);
    document.addEventListener("keydown", handleEscape);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
      document.removeEventListener("keydown", handleEscape);
    };
  }, [open]);

  const handleSelect = (opt) => {
    onChange?.(String(opt.value));
    setOpen(false);
  };

  const showDropdown = open && dropdownStyle;
  const dropdownContent = showDropdown ? (
    <div
      className="select-wrap__dropdown select-wrap__dropdown--portal"
      style={dropdownStyle}
      role="listbox"
    >
      {options.length === 0 ? (
        <div className="select-wrap__option select-wrap__option--empty">Sin opciones</div>
      ) : (
        options.map((opt) => (
          <button
            key={opt.value}
            type="button"
            role="option"
            aria-selected={String(opt.value) === String(value)}
            className={`select-wrap__option ${String(opt.value) === String(value) ? "select-wrap__option--selected" : ""}`}
            onClick={() => handleSelect(opt)}
          >
            <span className="select-wrap__option-text">{opt.label}</span>
          </button>
        ))
      )}
    </div>
  ) : null;

  return (
    <div
      ref={containerRef}
      className={`select-wrap ${size === "sm" ? "select-wrap--sm" : ""} ${variant === "ghost" ? "select-wrap--ghost" : ""} ${error ? "select-wrap--error" : ""} ${className}`.trim()}
    >
      {label && (
        <label className={`select-wrap__label ${labelClassName || ""}`.trim()}>
          {label}
          {required && <span aria-hidden> *</span>}
        </label>
      )}
      <button
        ref={triggerRef}
        type="button"
        className={`select-wrap__trigger ${open ? "select-wrap__trigger--open" : ""} ${!selectedOption ? "select-wrap__trigger--placeholder" : ""}`}
        onClick={() => !disabled && setOpen((o) => !o)}
        disabled={disabled}
        aria-haspopup="listbox"
        aria-expanded={open}
        aria-invalid={ariaInvalid || !!error}
        aria-label={label || "Seleccionar opción"}
      >
        <span className="select-wrap__value">{displayText}</span>
        <Icon
          icon={ControlsChevronDown}
          size={size === "sm" ? 18 : 20}
          className={`select-wrap__icon ${open ? "select-wrap__icon--open" : ""}`}
          aria-hidden
        />
      </button>
      {showDropdown && createPortal(dropdownContent, document.body)}
      {error && <span className="select-wrap__error">{error}</span>}
    </div>
  );
}
