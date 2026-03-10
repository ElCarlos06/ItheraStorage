import { useState, useMemo, useEffect, useRef } from "react";
import PageHeader from "../../components/dashboard/PageHeader";
import StatCard from "../../components/dashboard/StatCard";
import Buscador from "../../../../components/Buscador/Buscador";
import UsersEmptyState from "./UsersEmptyState";
import Button from "../../../../components/Button/Button";
import {
  GenericUser,
  GenericSettings,
  NotificationsBell,
  GenericDelete,
  GenericEdit,
  SecurityPassport,
  GenericPlus,
} from "@heathmont/moon-icons";
import Icon from "../../../../components/Icon/Icon";
import { api } from "../../../../api/client";
import "./Users.css";
import NewUserModal from "./NewUserModal";

const STAT_ICONS = [GenericUser, NotificationsBell, GenericSettings];

function mapUser(u) {
  if (!u || typeof u !== "object") return null;
  const roleName = u.role?.nombre ?? u.rol ?? u.role;
  const areaName = u.area?.nombre ?? u.area ?? u.areaNombre;
  return {
    id: u.id ?? u.id_usuario,
    numeroEmpleado: u.numeroEmpleado ?? u.numero_empleado ?? "-",
    nombre: u.nombreCompleto ?? u.nombre ?? u.nombre_completo ?? "-",
    correo: u.correo ?? "-",
    curp: u.curp ?? "-",
    rol: typeof roleName === "string" ? roleName : "-",
    area: typeof areaName === "string" ? areaName : "-",
  };
}

export default function Users({
  users: usersProp,
  stats: statsProp = [],
  loading: loadingProp,
  error: errorProp,
  onSearch,
  onNuevo,
  onEliminar,
  onEditar,
  onDetalles,
}) {
  const [search, setSearch] = useState("");
  const [modalNuevoOpen, setModalNuevoOpen] = useState(false);
  const [tooltipUser, setTooltipUser] = useState(null);
  const tooltipRef = useRef(null);
  const [users, setUsers] = useState(Array.isArray(usersProp) ? usersProp : []);
  const [loading, setLoading] = useState(loadingProp ?? false);
  const [error, setError] = useState(errorProp ?? null);
  const stats = Array.isArray(statsProp) ? statsProp : [];

  useEffect(() => {
    if (!tooltipUser) return;
    const handleClickOutside = (e) => {
      if (tooltipRef.current && !tooltipRef.current.contains(e.target)) {
        setTooltipUser(null);
      }
    };
    const handleEscape = (e) => {
      if (e.key === "Escape") setTooltipUser(null);
    };
    document.addEventListener("click", handleClickOutside);
    document.addEventListener("keydown", handleEscape);
    return () => {
      document.removeEventListener("click", handleClickOutside);
      document.removeEventListener("keydown", handleEscape);
    };
  }, [tooltipUser]);

  useEffect(() => {
    if (usersProp !== undefined) return;
    setLoading(true);
    api
      .getUsers()
      .then((res) => setUsers((res.data ?? []).map(mapUser).filter(Boolean)))
      .catch((err) => {
        setError(err.message);
        setUsers([]);
      })
      .finally(() => setLoading(false));
  }, [usersProp]);

  const refreshUsers = () => {
    if (usersProp !== undefined) return;
    api
      .getUsers()
      .then((res) => setUsers((res.data ?? []).map(mapUser).filter(Boolean)))
      .catch(() => {});
  };

  const filtered = useMemo(() => {
    let list = Array.isArray(users) ? users : [];
    const q = search.trim().toLowerCase();

    if (q) {
      list = list.filter(
        (u) =>
          (u.nombre ?? "").toLowerCase().includes(q) ||
          (u.correo ?? "").toLowerCase().includes(q)
      );
    }

    return list;
  }, [users, search]);

  const handleSearchChange = (e) => {
    const value = e.target.value;
    setSearch(value);
    onSearch?.(value);
  };

  const showEmptyState = filtered.length === 0;

  return (
    <div className={`users-page ${showEmptyState ? "users-page--empty" : ""}`}>
      <PageHeader
        overline="PANEL DE CONTROL"
        title="Gestión de Usuarios"
        subtitle="Administra cuentas y permisos del sistema"
      />

      <section className="users-view" aria-label="Gestión de usuarios">
        <div className="users-view__stats row g-3 mb-4">
          {stats.map((stat, i) => (
            <div key={i} className="col-12 col-sm-6 col-xl-3">
              <StatCard icon={STAT_ICONS[i]} {...stat} />
            </div>
          ))}
        </div>

        <div className="users-view__toolbar">
          <div className="users-view__buscador">
            <Buscador
              placeholder="Buscar usuario por nombre..."
              value={search}
              onChange={handleSearchChange}
              aria-label="Buscar usuarios"
            />
          </div>
          <div className="users-view__actions">
            <Button
              variant="primary"
              iconLeft={GenericPlus}
              onClick={() => setModalNuevoOpen(true)}
            >
              Nuevo Usuario
            </Button>
          </div>
        </div>

        {error && (
          <div className="users-view__error" role="alert">
            {error}
          </div>
        )}

        {loading ? (
          <div className="users-view__loading">
            Cargando usuarios…
          </div>
        ) : (
          <div className="users-view__list">
            {showEmptyState ? (
              <UsersEmptyState hasSearch={!!search.trim()} />
            ) : (
              filtered.map((user, idx) => (
                <div key={user?.id ?? `user-${idx}`} className="users-view__card-wrap">
                  <div className="users-view__card">
                    <div className="users-view__card-inner">
                      <div className="users-view__card-body">
                        <p className="users-view__numero">{user.numeroEmpleado}</p>
                        <div className="users-view__data-row">
                          <div className="users-view__data-col">
                            <p className="users-view__label">Rol</p>
                            <p className="users-view__value">{user.rol ?? "—"}</p>
                          </div>
                          <div className="users-view__data-col">
                            <p className="users-view__label">Nombre Completo</p>
                            <p className="users-view__value">{user.nombre ?? "—"}</p>
                          </div>
                          <div className="users-view__data-col">
                            <p className="users-view__label">Curp</p>
                            <p className="users-view__value" title={user.curp ?? undefined}>{user.curp ?? "—"}</p>
                          </div>
                          <div className="users-view__data-col">
                            <p className="users-view__label">Correo</p>
                            <p className="users-view__value" title={user.correo ?? undefined}>{user.correo ?? "—"}</p>
                          </div>
                          <div className="users-view__data-col">
                            <p className="users-view__label">Área</p>
                            <p className="users-view__value">{user.area ?? "—"}</p>
                          </div>
                        </div>
                      </div>
                      <div className="users-view__card-actions">
                        <button
                          type="button"
                          className="users-view__action-btn users-view__action-btn--delete"
                          title="Eliminar"
                          onClick={() => onEliminar?.(user)}
                        >
                          <Icon icon={GenericDelete} size={30} />
                        </button>
                        <button
                          type="button"
                          className="users-view__action-btn"
                          title="Editar"
                          onClick={() => onEditar?.(user)}
                        >
                          <Icon icon={GenericEdit} size={30} />
                        </button>
                        <button
                          type="button"
                          className="users-view__action-btn"
                          title="Detalles"
                          onClick={() => onDetalles?.(user)}
                        >
                          <Icon icon={SecurityPassport} size={30} />
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              ))
            )}
          </div>
        )}
      </section>

      <NewUserModal
        open={modalNuevoOpen}
        onClose={() => setModalNuevoOpen(false)}
        onGuardar={() => {
          refreshUsers();
          onNuevo?.();
        }}
      />

    </div>
  );
}