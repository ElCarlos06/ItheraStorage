import { useState, useMemo, useEffect } from "react";
import PageHeader from "../../components/dashboard/PageHeader";
import StatCard from "../../components/dashboard/StatCard";
import Buscador from "../../../../components/Buscador/Buscador";
import UsersEmptyState from "./UsersEmptyState";
import LoadingState from "../../../../components/LoadingState/LoadingState";
import Button from "../../../../components/Button/Button";
import Pagination from "../../components/layout/Pagination";
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
import { Tooltip } from "../../../../components/Tooltip/Tooltip";
import { usersApi } from "../../../../api/usersApi";
import "./Users.css";
import NewUserModal from "./NewUserModal";
import UserInfoModal from "./UserInfoModal";
import { toast } from "../../../../utils/toast.jsx";
import { getCurrentUserCorreo, logout } from "../../../../utils/auth";
import ConfirmDeleteModal from "../../../../components/ConfirmDeleteModal/ConfirmDeleteModal";
import ErrorBanner from "../../../../components/ErrorBanner/ErrorBanner";

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
    nacimiento: u.fechaNacimiento ?? u.fecha_nacimiento,
    idRol: u.role?.id ?? u.idRol ?? u.id_rol,
    idArea: u.area?.id ?? u.idArea ?? u.id_area,
    rol: typeof roleName === "string" ? roleName : (roleName?.nombre ?? "-"),
    area: typeof areaName === "string" ? areaName : (areaName?.nombre ?? "-"),
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
  const [modalEditUser, setModalEditUser] = useState(null);
  const [modalUser, setModalUser] = useState(null);
  const [confirmDeleteUser, setConfirmDeleteUser] = useState(null);
  const [users, setUsers] = useState(Array.isArray(usersProp) ? usersProp : []);
  const [loading, setLoading] = useState(loadingProp ?? false);
  const [error, setError] = useState(errorProp ?? null);

  // Pagination state
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const pageSize = 10;
  const stats = Array.isArray(statsProp) ? statsProp : [];

  const currentUserCorreo = getCurrentUserCorreo();

  useEffect(() => {
    if (usersProp !== undefined) return;
    setLoading(true);
    usersApi
      .getUsers(currentPage - 1, pageSize)
      .then((res) => {
        const list = Array.isArray(res) ? res : (res?.data?.content || res?.content || res?.data || []);
        setUsers(
          list
            .filter((u) => u?.esActivo !== false)
            .map(mapUser)
            .filter(Boolean),
        );
        setTotalPages(res?.data?.totalPages ?? res?.totalPages ?? 1);
        setTotalElements(res?.data?.totalElements ?? res?.totalElements ?? list.length);
      })
      .catch((err) => {
        setError(err.message);
        setUsers([]);
      })
      .finally(() => setLoading(false));
    const interval = setInterval(() => {
      usersApi
        .getUsers(currentPage - 1, pageSize)
        .then((res) => {
          const list = Array.isArray(res) ? res : (res?.data?.content || res?.content || res?.data || []);
          setUsers(
            list
              .filter((u) => u?.esActivo !== false)
              .map(mapUser)
              .filter(Boolean),
          );
          setTotalPages(res?.data?.totalPages ?? res?.totalPages ?? 1);
          setTotalElements(res?.data?.totalElements ?? res?.totalElements ?? list.length);
        })
        .catch(() => {});
    }, 30000);
    return () => clearInterval(interval);
  }, [usersProp, currentPage]);

  const refreshUsers = () => {
    if (usersProp !== undefined) return;
    usersApi
      .getUsers(currentPage - 1, pageSize)
      .then((res) => {
        const list = Array.isArray(res) ? res : (res?.data?.content || res?.content || res?.data || []);
        setUsers(
          list
            .filter((u) => u?.esActivo !== false)
            .map(mapUser)
            .filter(Boolean),
        );
        setTotalPages(res?.data?.totalPages ?? res?.totalPages ?? 1);
        setTotalElements(res?.data?.totalElements ?? res?.totalElements ?? list.length);
      })
      .catch(() => {});
  };

  const filtered = useMemo(() => {
    let list = Array.isArray(users) ? users : [];
    if (currentUserCorreo) {
      list = list.filter(
        (u) =>
          (u.correo ?? "").toLowerCase() !== currentUserCorreo.toLowerCase(),
      );
    }
    const q = search.trim().toLowerCase();

    if (q) {
      list = list.filter(
        (u) =>
          (u.nombre ?? "").toLowerCase().includes(q) ||
          (u.correo ?? "").toLowerCase().includes(q),
      );
    }

    return list;
  }, [users, search, currentUserCorreo]);

  const handleSearchChange = (e) => {
    const value = e.target.value;
    setSearch(value);
    onSearch?.(value);
  };

  const showEmptyState = filtered.length === 0;

  return (
    <div
      className={`users-page ${showEmptyState ? "users-page--empty" : ""} ${loading ? "users-page--loading" : ""}`}
    >
      <PageHeader
        overline="PANEL DE CONTROL"
        title="Gestión de Usuarios"
        subtitle="Administra cuentas y permisos del sistema"
      />

      <section className="users-view" aria-label="Gestión de usuarios">
        <div className="users-view__stats row g-3">
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
              iconSize={30}
              onClick={() => setModalNuevoOpen(true)}
            >
              Nuevo
            </Button>
          </div>
        </div>

        {error && (
          <ErrorBanner message={error} onDismiss={() => setError(null)} />
        )}

        {loading ? (
          <div className="users-view__list users-view__list--loading">
            <LoadingState message="Cargando usuarios…" />
          </div>
        ) : (
          <div className="users-view__list">
            {showEmptyState ? (
              <UsersEmptyState hasSearch={!!search.trim()} />
            ) : (
              filtered.map((user, idx) => (
                <div
                  key={user?.id ?? `user-${idx}`}
                  className="users-view__card-wrap"
                >
                  <div className="users-view__card">
                    <Tooltip
                      content="Clic para ver información completa"
                      side="top"
                    >
                      <div className="users-view__card-inner">
                        <div
                          role="button"
                          tabIndex={0}
                          onClick={() => setModalUser(user)}
                          onKeyDown={(e) =>
                            e.key === "Enter" && setModalUser(user)
                          }
                          aria-label="Ver información completa del usuario"
                          className="users-view__card-body users-view__card-body--clickable"
                        >
                          <div className="users-view__card-body-inner">
                            <p className="users-view__numero">
                              {user.numeroEmpleado}
                            </p>
                            <div className="users-view__data-row">
                              <div className="users-view__data-col">
                                <p className="users-view__label">Rol</p>
                                <p className="users-view__value">
                                  {user.rol ?? "—"}
                                </p>
                              </div>
                              <div className="users-view__data-col">
                                <p className="users-view__label">
                                  Nombre Completo
                                </p>
                                <p className="users-view__value">
                                  {user.nombre ?? "—"}
                                </p>
                              </div>
                              <div className="users-view__data-col">
                                <p className="users-view__label">Curp</p>
                                <p className="users-view__value">
                                  {user.curp ?? "—"}
                                </p>
                              </div>
                              <div className="users-view__data-col">
                                <p className="users-view__label">Correo</p>
                                <p className="users-view__value">
                                  {user.correo ?? "—"}
                                </p>
                              </div>
                              <div className="users-view__data-col">
                                <p className="users-view__label">Área</p>
                                <p className="users-view__value">
                                  {user.area ?? "—"}
                                </p>
                              </div>
                            </div>
                          </div>
                        </div>
                        <div className="users-view__card-actions">
                          <button
                            type="button"
                            className="users-view__action-btn users-view__action-btn--delete"
                            title="Eliminar"
                            onClick={() => setConfirmDeleteUser(user)}
                          >
                            <Icon icon={GenericDelete} size={30} />
                          </button>
                          <button
                            type="button"
                            className="users-view__action-btn"
                            title="Editar"
                            onClick={(e) => {
                              e.stopPropagation();
                              setModalEditUser(user);
                            }}
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
                    </Tooltip>
                  </div>
                </div>
              ))
            )}

            {!showEmptyState && (
              <Pagination
                currentPage={currentPage}
                totalPages={totalPages}
                totalElements={totalElements}
                pageSize={pageSize}
                onPageChange={(page) => setCurrentPage(page)}
              />
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
          toast.success("Usuario guardado correctamente");
        }}
      />
      <NewUserModal
        open={!!modalEditUser}
        onClose={() => setModalEditUser(null)}
        initialData={modalEditUser}
        onGuardar={() => {
          refreshUsers();
          onEditar?.(modalEditUser);
          setModalEditUser(null);
          toast.success("Usuario actualizado correctamente");
        }}
      />

      <ConfirmDeleteModal
        open={!!confirmDeleteUser}
        onClose={() => setConfirmDeleteUser(null)}
        onConfirm={async () => {
          if (!confirmDeleteUser?.id) return;
          try {
            await usersApi.toggleStatusUser(confirmDeleteUser.id);
            setError(null);
            setConfirmDeleteUser(null);
            onEliminar?.(confirmDeleteUser);
            const deletedCorreo = (
              confirmDeleteUser.correo ?? ""
            ).toLowerCase();
            if (
              currentUserCorreo &&
              deletedCorreo === currentUserCorreo.toLowerCase()
            ) {
              toast.success("Tu cuenta fue eliminada");
              logout();
            } else {
              refreshUsers();
              toast.success("Usuario eliminado correctamente");
            }
          } catch (err) {
            setError(err.message);
            toast.error(err.message);
          }
        }}
        title="¿Confirmar eliminación?"
        message={`Se eliminará a ${confirmDeleteUser?.nombre ?? "este usuario"}. Esta acción no se puede deshacer.`}
      />

      <UserInfoModal
        open={!!modalUser}
        onClose={() => setModalUser(null)}
        user={modalUser}
      />
    </div>
  );
}
