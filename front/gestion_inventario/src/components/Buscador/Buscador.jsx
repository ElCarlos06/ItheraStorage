import { GenericSearch } from "@heathmont/moon-icons";
import Icon from "../Icon/Icon";
import "./Buscador.css";

export default function Buscador({ value, onChange, placeholder = "Buscar...", ...props }) {
  return (
    <div className="buscador position-relative d-flex align-items-center w-100">
      <span
        className="buscador__icon position-absolute top-50 start-0 translate-middle-y ms-3 d-flex align-items-center justify-content-center"
        aria-hidden
      >
        <Icon icon={GenericSearch} size={30} />
      </span>
      <input
        type="search"
        value={value}
        onChange={(e) => onChange?.(e)}
        placeholder={placeholder}
        className="buscador__input"
        {...props}
      />
    </div>
  );
}
