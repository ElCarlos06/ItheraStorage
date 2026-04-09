/**
 * Cliente API base para el backend SIRMA.
 */
const API_BASE = import.meta.env.VITE_API_URL || "";

// Código de estado HTTP que indican que la sesión ha expirado o es inválida
const UNAUTHORIZED = 401;

// Código de estado HTTP que indican que el usuario no tiene permisos para realizar la acción
const FORBIDDEN = 403;

/**
 * Evita mostrar en UI (toasts) mensajes que parezcan técnicos o de base de datos.
 */
function sanitizeApiErrorMessage(message, status) {
  if (message == null || typeof message !== "string") {
    return "Ocurrió un error. Intenta de nuevo.";
  }
  const m = message.trim();
  if (!m) return "Ocurrió un error. Intenta de nuevo.";
  const lower = m.toLowerCase();
  if (
    lower.includes("sql") ||
    lower.includes("jdbc") ||
    lower.includes("mysql") ||
    lower.includes("tidb") ||
    lower.includes("hibernate") ||
    lower.includes("constraint") ||
    lower.includes("deadlock") ||
    lower.includes("foreign key") ||
    lower.includes("duplicate entry")
  ) {
    return status >= 500
      ? "Error en el servidor. Intenta más tarde."
      : "No se pudo completar la operación.";
  }
  if (m.length > 280) return `${m.slice(0, 277)}…`;
  return m;
}

/**
 * Función principal para peticiones HTTP
 * @param {string} endpoint Cadena relativa, ej. '/api/users'
 * @param {RequestInit} options Opciones de fetch (method, body, headers, etc)
 * @returns {Promise<any>}
 */
export async function request(endpoint, options = {}) {
  const url = `${API_BASE}${endpoint}`;
  const token =
    typeof sessionStorage !== "undefined"
      ? sessionStorage.getItem("token")
      : null;

  const headers = {
    ...options.headers,
  };

  // Solo agregar Content-Type: application/json si no es FormData
  if (!(options.body instanceof FormData) && !headers["Content-Type"]) {
    headers["Content-Type"] = "application/json";
  }

  // Si enviamos un FormData, dejamos que el navegador asigne el Content-Type
  // con el boundary correcto automáticamente.

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

  const AUTH_ENPOINTS = ["/api/auth/login", "/api/auth/register"];
  const isAuthEndpoint = AUTH_ENPOINTS.includes(endpoint);

  // Manejo de sesión, en cuanto le llegue un 401 o 403, se cierra sesión
  if (
    (res.status === UNAUTHORIZED || res.status === FORBIDDEN) &&
    !isAuthEndpoint
  ) {
    sessionStorage.removeItem("token");
    // Redirigir a login (raíz) si no estamos ya ahí
    if (window.location.pathname !== "/") {
      window.location.replace("/");
    }
  }

  const data = await res.json().catch(() => ({}));
  if (!res.ok) {
    const msg =
      data.message ||
      data.error ||
      (Array.isArray(data.errors) && data.errors[0]?.defaultMessage) ||
      "Ocurrió un error. Intenta de nuevo.";

    const err = new Error(sanitizeApiErrorMessage(msg, res.status));
    err.data = data;
    err.status = res.status;
    throw err;
  }
  return data;
}
