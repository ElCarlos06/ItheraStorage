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
import { getProfileFromToken } from "../../../../api/authApi";
import { useEffect, useMemo, useState } from "react";
import { imagenPerfilApi } from "../../../../api/imagenPerfilApi";
import Modal from "../../../../components/Modal/Modal";
import { UserRound } from "lucide-react";

const navItems = [
  { to: "/dashboard", label: "Dashboard", icon: GenericHome },
  { to: "/activos", label: "Activos", icon: ShopBag },
  { to: "/usuarios", label: "Usuarios", icon: GenericUsers },
  { to: "/solicitudes", label: "Solicitudes", icon: GenericSettings },
  { to: "/catalogos", label: "Catálogos", icon: FilesMagazine },
];

export default function Sidebar() {
  const profile = useMemo(() => getProfileFromToken(), []);
  const [avatarUrl, setAvatarUrl] = useState(null);
  const [showAvatarPreview, setShowAvatarPreview] = useState(false);

  const handleLogout = () => {
    sessionStorage.removeItem("token");
    window.location.replace("/");
  };

  const cargarAvatar = () => {
    if (profile?.correo) {
      imagenPerfilApi
        .getByCorreo(profile.correo)
        .then((res) => setAvatarUrl(res.data?.urlCloudinary))
        .catch(() => setAvatarUrl(null));
    }
  };

  useEffect(() => {
    cargarAvatar();
  }, [profile?.correo]);

  useEffect(() => {
    const handler = () => cargarAvatar();
    window.addEventListener("profile-photo-updated", handler);
    return () => window.removeEventListener("profile-photo-updated", handler);
  }, [profile?.correo]);

  const imgSrc = avatarUrl || "/public/default-avatar.png";

  return (
    <>
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
          <div className="admin-sidebar__user">
            <div
              className="admin-sidebar__avatar"
              onClick={(e) => {
                e.preventDefault();
                e.stopPropagation();
                setShowAvatarPreview(true);
              }}
              role="button"
              tabIndex={0}
              title="Ver foto de perfil"
            >
              <img
                src={imgSrc || <UserRound />}
                alt="imagen de perfil"
                className="admin-sidebar__avatar-img"
              />
            </div>
            <Link to="/ajustes" className="admin-sidebar__user-info-link">
              <div className="admin-sidebar__user-info">
                <span className="admin-sidebar__user-name">
                  {profile?.nombreCompleto.split(" ")[0]}
                </span>
                <span className="admin-sidebar__user-role">{profile?.rol}</span>
              </div>
            </Link>
          </div>
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

      <Modal
        open={showAvatarPreview}
        onClose={() => setShowAvatarPreview(false)}
        className="avatar-preview-modal"
      >
        <div
          className="avatar-preview-card"
          onClick={(e) => e.stopPropagation()}
        >
          <img
            src={imgSrc}
            alt="Foto de perfil"
            className="avatar-preview-card__img"
          />
          <div className="avatar-preview-card__info">
            <span className="avatar-preview-card__name">
              {profile?.nombreCompleto ?? "—"}
            </span>
            <span className="avatar-preview-card__role">
              {profile?.rol ?? "—"}
            </span>
          </div>
        </div>
      </Modal>
    </>
  );
}
