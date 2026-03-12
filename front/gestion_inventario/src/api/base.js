/**
 * Cliente API base para el backend SIRMA.
 * Contiene la función `request` configurada con la Base URL.
 */
const API_BASE = import.meta.env.VITE_API_URL || "";

/**
 * Función principal para peticiones HTTP
 * @param {string} endpoint Cadena relativa, ej. '/api/users'
 * @param {RequestInit} options Opciones de fetch (method, body, headers, etc)
 * @returns {Promise<any>}
 */
export async function request(endpoint, options = {}) {
  const url = `${API_BASE}${endpoint}`;
  const token = typeof sessionStorage !== "undefined" ? sessionStorage.getItem("token") : null;
  
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

  const data = await res.json().catch(() => ({}));
  if (!res.ok) {
    const msg =
      data.message ||
      data.error ||
      (Array.isArray(data.errors) && data.errors[0]?.defaultMessage) ||
      "Ocurrió un error. Intenta de nuevo.";
    
    const err = new Error(msg);
    err.data = data;
    err.status = res.status;
    throw err;
  }
  return data;
}
