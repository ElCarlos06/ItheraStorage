import { request } from "./base";

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

  /** GET /api/auth/me - Obtener perfil del usuario */
  me: () =>
    request("/api/auth/me", {
      headers: {
        Authorization: `Bearer ${sessionStorage.getItem("token")}`,
      },
    }),
};
