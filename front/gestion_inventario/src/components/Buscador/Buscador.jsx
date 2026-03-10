import { GenericSearch } from "@heathmont/moon-icons";
import Icon from "../Icon/Icon";
import "./Buscador.css";

export default function Buscador({ value, onChange, placeholder = "Buscar...", ...props }) {
  return (
    <div className="buscador">
      <span className="buscador__icon" aria-hidden>
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
