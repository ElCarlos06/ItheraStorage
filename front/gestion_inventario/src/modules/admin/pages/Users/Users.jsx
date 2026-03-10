import { useState, useMemo } from "react";
// import NewUserModal from "./NewUserModal";
import PageHeader from "../../components/dashboard/PageHeader";
import StatCard from "../../components/dashboard/StatCard";
import Card from "../../../../components/Card/Card";
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
import "./Users.css";
import NewUserModal from "./NewUserModal";

const STAT_ICONS = [GenericUser, NotificationsBell, GenericSettings];

export default function Users({
  users: usersProp = [],
  stats: statsProp = [],
  loading: loadingProp = false,
  error: errorProp = null,
  onSearch,
  onNuevo,
  onEliminar,
  onEditar,
  onDetalles,
}) {

  const [search, setSearch] = useState("");
  const [modalNuevoOpen, setModalNuevoOpen] = useState(false);

  const users = Array.isArray(usersProp) ? usersProp : [];
  const stats = Array.isArray(statsProp) ? statsProp : [];
  const loading = loadingProp;
  const error = errorProp;

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

        {/* STATS */}
        <div className="users-view__stats row g-3 mb-4">
          {stats.map((stat, i) => (
            <div key={i} className="col-12 col-sm-6 col-xl-3">
              <StatCard icon={STAT_ICONS[i]} {...stat} />
            </div>
          ))}
        </div>

        {/* TOOLBAR */}
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
              filtered.map((user) => (

                <div key={user.id} className="users-view__card-wrap">

                  <Card padding="medium" className="users-view__card">

                    <div className="users-view__content">

                      <div className="users-view__row">

                        <div className="users-view__col">
                          <p className="users-view__label">Nombre</p>
                          <p className="users-view__value">{user.nombre ?? "—"}</p>
                        </div>

                        <div className="users-view__col">
                          <p className="users-view__label">Correo</p>
                          <p className="users-view__value">{user.correo ?? "—"}</p>
                        </div>

                        <div className="users-view__col">
                          <p className="users-view__label">Rol</p>
                          <p className="users-view__value">{user.rol ?? "—"}</p>
                        </div>

                        <div className="users-view__col">
                          <p className="users-view__label">Área</p>
                          <p className="users-view__value">{user.area ?? "—"}</p>
                        </div>

                      </div>

                    </div>

                    <div className="users-view__actions">

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

                  </Card>

                </div>

              ))
            )}

          </div>
        )}

      </section>

      <NewUserModal 
       open={modalNuevoOpen}
        onClose={() => setModalNuevoOpen(false)}
        onGuardar={(data) => {
          onNuevo?.(data);
          setModalNuevoOpen(false);
        }}
      />

    </div>
  );
}