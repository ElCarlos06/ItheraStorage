import { request } from "./base";

const parseJwt = (token) => {
  const base64 = token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/");
  return JSON.parse(atob(base64));
};

export const authApi = {
  /** POST /api/auth/login - Iniciar sesión */
  login: (body) =>
    request("/api/auth/login", {
      method: "POST",
      body: JSON.stringify(body),
    }),

  /** POST /api/register - Registrar nuevo usuario */
  register: (body) =>
    request("/api/register", {
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
      : {
          correo: body.correo,
          passwordActual: body.passwordActual,
          passwordNueva: body.passwordNueva,
        };
    return request("/api/auth/change-password", {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
};

/**
 * Obtiene el perfil del usuario actual desde el token JWT en sessionStorage.
 * @returns {object|null} objeto con nombreCompleto, correo, rol, area y numeroEmpleado o null si no hay token valido
 */
export const getProfileFromToken = () => {
  const token = sessionStorage.getItem("token");
  if (!token) return null;

  const payload = parseJwt(token);
  return {
    id: payload.id, // ID explicitly from the jwt claim
    nombreCompleto: payload.nombre,
    correo: payload.sub,
    rol: payload.role,
    area: payload.area,
    numeroEmpleado: payload.numeroEmpleado,
  };
};

/**
 * Obtiene el correo del usuario actual desde el token JWT en sessionStorage.
 * El subject (sub) del JWT contiene el correo del usuario logueado.
 * @returns {string|null} correo o null si no hay token valido
 */
export function getCurrentUserCorreo() {
  const token = sessionStorage.getItem("token");
  if (!token) return null;
  try {
    const decoded = parseJwt(token);
    return decoded.sub ?? null;
  } catch {
    return null;
  }
}

/** Cierra sesion y recarga para mostrar el login */
export function logout() {
  sessionStorage.removeItem("token");
  window.location.replace("/");
}
