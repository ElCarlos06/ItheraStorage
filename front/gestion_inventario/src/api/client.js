/**
 * Cliente API para el backend SIRMA.
 * Base URL configurable por variable de entorno.
 */
// En dev, Vite hace proxy de /api a localhost:8080; en prod usa VITE_API_URL
const API_BASE = import.meta.env.VITE_API_URL || "";

async function request(endpoint, options = {}) {
  const url = `${API_BASE}${endpoint}`;
  let res;
  try {
    res = await fetch(url, {
      ...options,
      headers: {
        "Content-Type": "application/json",
        ...options.headers,
      },
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
};
