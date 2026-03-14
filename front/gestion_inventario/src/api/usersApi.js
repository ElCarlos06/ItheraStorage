import { request } from "./base";

export const usersApi = {
  /** GET /api/users - Listar usuarios (requiere auth) */
  getUsers: (page = 0, size = 1000) => request(`/api/users?page=${page}&size=${size}`),

  /** GET /api/users/by-email?correo=:correo - Obtener usuario por correo */
  getUserByCorreo: (correo) =>
    request(`/api/users/by-email?correo=${correo}`, {
      method: "GET",
    }),

  /** PUT /api/users/:id - Actualizar usuario */
  updateUser: (id, body) =>
    request(`/api/users/${id}`, {
      method: "PUT",
      body: JSON.stringify(body),
    }),

  /** DELETE /api/users/:id - Eliminar usuario (usa toggle status en backend) */
  deleteUser: (id) =>
    request(`/api/users/${id}`, {
      method: "DELETE",
    }),

  /** PATCH /api/users/:id/status - Cambiar estado activo/inactivo (soft delete) */
  toggleStatusUser: (id) =>
    request(`/api/users/${id}/status`, {
      method: "PATCH",
    }),
};
