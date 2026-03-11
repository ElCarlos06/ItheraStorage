/**
 * Cliente API para el backend SIRMA.
 * Base URL configurable por variable de entorno.
 */
// En dev, Vite hace proxy de /api a localhost:8080; en prod usa VITE_API_URL
const API_BASE = import.meta.env.VITE_API_URL || "";

async function request(endpoint, options = {}) {
  const url = `${API_BASE}${endpoint}`;
  const token = typeof sessionStorage !== "undefined" ? sessionStorage.getItem("token") : null;
  const headers = {
    "Content-Type": "application/json",
    ...options.headers,
  };
  if (token) headers["Authorization"] = `Bearer ${token}`;

  let res;
  try {
    res = await fetch(url, {
      ...options,
      headers,
    });
  } catch (err) {
    throw new Error(
      err.message?.includes("Failed to fetch")
        ? `No se pudo conectar al servidor. ¿Está corriendo en ${API_BASE}?`
        : err.message || "Error de conexión",
    );
  }

  const data = await res.json().catch(() => ({}));
  if (!res.ok) {
    const msg =
      data.message ||
      data.error ||
      (Array.isArray(data.errors) && data.errors[0]?.defaultMessage) ||
      "Ocurrió un error. Intenta de nuevo.";
    // Se adjunta el cuerpo completo al error para que el llamador pueda
    // inspeccionar campos adicionales como requiresPasswordChange
    const err = new Error(msg);
    err.data = data;
    err.status = res.status;
    throw err;
  }
  return data;
}

export const api = {
  /** GET /api/roles */
  getRoles: () => request("/api/roles"),

  /** GET /api/areas */
  getAreas: () => request("/api/areas"),

  /** POST /api/register - Registrar nuevo usuario */
  register: (body) =>
    request("/api/register", {
      method: "POST",
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

  /** PUT /api/users/:id - Actualizar usuario */
  updateUser: (id, body) =>
    request(`/api/users/${id}`, {
      method: "PUT",
      body: JSON.stringify(body),
    }),

  /** POST /api/login - Iniciar sesión */
  login: (body) =>
    request("/api/auth/login", {
      method: "POST",
      body: JSON.stringify(body),
    }),

  /** POST /api/auth/request-password-reset - Olvidé mi contraseña: envía nueva temporal por correo */
  requestPasswordReset: (correo) =>
    request("/api/auth/request-password-reset", {
      method: "POST",
      body: JSON.stringify({ correo }),
    }),

  /** POST /api/auth/change-password - Cambiar contraseña (unificado: token o correo+passwordActual) */
  changePassword: (body) => {
    const payload = body.token
      ? { token: body.token, passwordNueva: body.passwordNueva }
      : { correo: body.correo, passwordActual: body.passwordActual, passwordNueva: body.passwordNueva };
    return request("/api/auth/change-password", {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },

  /** GET /api/users/correo/:correo - Obtener usuario por correo */
  getUserByCorreo: (correo) =>
    request(`/api/users/by-email?correo=${correo}`, {
      method: "GET",
    }),

  /** GET /api/users - Listar usuarios (requiere auth) */
  getUsers: () => request("/api/users"),

  /** Ubicaciones: Campus, Edificios, Espacios (Aulas) */
  getCampus: () => request("/api/campus"),
  getEdificios: () => request("/api/edificios"),
  getEdificiosByCampus: (campusId) => request(`/api/edificios/campus/${campusId}`),
  getEspacios: () => request("/api/espacios"),
  getEspaciosByEdificio: (edificioId) => request(`/api/espacios/edificio/${edificioId}`),
  createCampus: (body) => request("/api/campus", { method: "POST", body: JSON.stringify(body) }),
  updateCampus: (id, body) => request(`/api/campus/${id}`, { method: "PUT", body: JSON.stringify(body) }),
  toggleStatusCampus: (id) => request(`/api/campus/${id}/status`, { method: "PATCH" }),
  createEdificio: (body) => request("/api/edificios", { method: "POST", body: JSON.stringify(body) }),
  updateEdificio: (id, body) => request(`/api/edificios/${id}`, { method: "PUT", body: JSON.stringify(body) }),
  toggleStatusEdificio: (id) => request(`/api/edificios/${id}/status`, { method: "PATCH" }),
  createEspacio: (body) => request("/api/espacios", { method: "POST", body: JSON.stringify(body) }),
  updateEspacio: (id, body) => request(`/api/espacios/${id}`, { method: "PUT", body: JSON.stringify(body) }),
  toggleStatusEspacio: (id) => request(`/api/espacios/${id}/status`, { method: "PATCH" }),
};
