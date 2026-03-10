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
        : err.message || "Error de conexión"
    );
  }

  const data = await res.json().catch(() => ({}));
  if (!res.ok) {
    const msg =
      data.message ||
      data.error ||
      (Array.isArray(data.errors) && data.errors[0]?.defaultMessage) ||
      `Error ${res.status}: ${res.statusText}`;
    throw new Error(msg);
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

  /** GET /api/users - Listar usuarios (requiere auth) */
  getUsers: () => request("/api/users"),
};
