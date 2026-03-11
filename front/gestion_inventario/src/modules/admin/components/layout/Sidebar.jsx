import { NavLink, Link } from "react-router-dom";
import "./Sidebar.css";
import {
  GenericHome,
  ShopBag,
  GenericUsers,
  GenericSettings,
  FilesMagazine,
  SoftwareLogOut,
} from "@heathmont/moon-icons";
import Icon from "../../../../components/Icon/Icon";
import logoActivos360 from "../../../../assets/activos360_logo.png";

const navItems = [
  { to: "/dashboard", label: "Dashboard", icon: GenericHome },
  { to: "/activos", label: "Activos", icon: ShopBag },
  { to: "/usuarios", label: "Usuarios", icon: GenericUsers },
  { to: "/solicitudes", label: "Solicitudes", icon: GenericSettings },
  { to: "/catalogos", label: "Catálogos", icon: FilesMagazine },
];

export default function Sidebar() {
  // Elimina el token de sesión y recarga la página para que App.jsx
  // detecte la ausencia del token y monte PublicRouter con la pantalla de login
  const handleLogout = () => {
    sessionStorage.removeItem("token");
    window.location.replace("/");
  };

  return (
    <aside className="admin-sidebar">
      <div className="admin-sidebar__header">
        <div className="admin-sidebar__logo">
          <img
            src={logoActivos360}
            alt="Activos 360"
            className="admin-sidebar__logo-img"
          />
        </div>
      </div>
      <nav className="admin-sidebar__nav">
        {navItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            className={({ isActive }) =>
              `admin-sidebar__item ${isActive ? "admin-sidebar__item--active" : ""}`
            }
          >
            <Icon
              icon={item.icon}
              size={30}
              className="admin-sidebar__item-icon"
            />
            <span>{item.label}</span>
          </NavLink>
        ))}
      </nav>
      <div className="admin-sidebar__footer">
        <Link
          to="/ajustes"
          className="admin-sidebar__user"
          title="Ir a Ajustes"
        >
          <div className="admin-sidebar__avatar">A</div>
          <div className="admin-sidebar__user-info">
            <span className="admin-sidebar__user-name">Administrador</span>
            <span className="admin-sidebar__user-role">Admin</span>
          </div>
        </Link>
        <button
          onClick={handleLogout}
          type="button"
          className="admin-sidebar__logout"
        >
          <Icon icon={SoftwareLogOut} size={30} />
          <span>Cerrar Sesión</span>
        </button>
      </div>
    </aside>
  );
}
