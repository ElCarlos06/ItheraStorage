/**
 * Utilidades para consultas paginadas (React Query / API).
 * Usado en ActivosPage, Users, Catalogs, Requests, etc.
 */

/** Tiempo por defecto que los datos se consideran frescos (5 min) */
export const DEFAULT_STALE_TIME = 5 * 60 * 1000;

/** Tamaño de página por defecto */
export const DEFAULT_PAGE_SIZE = 10;

/**
 * Extrae content, totalPages y totalElements de la respuesta paginada del backend.
 * Soporta múltiples formatos: { data }, { data: { content } }, { content }.
 *
 * @param {object} res - Respuesta cruda del API
 * @returns {{ content: any[], totalPages: number, totalElements: number }}
 */
export function parsePageResponse(res) {
  const raw = res?.data ?? res ?? {};
  const content = Array.isArray(raw)
    ? raw
    : raw?.content ?? raw?.data?.content ?? raw?.data ?? [];
  const totalPages = raw?.totalPages ?? raw?.data?.totalPages ?? 1;
  const totalElements = raw?.totalElements ?? raw?.data?.totalElements ?? content.length;

  return { content, totalPages, totalElements };
}

/**
 * Wrapper para queryFn: lanza error si res.error es true.
 * @param {Promise} apiCall - Llamada al API (ej: activosApi.getActivos(...))
 * @param {string} errorMessage - Mensaje si falla
 * @returns {Promise}
 */
export async function fetchWithErrorCheck(apiCall, errorMessage = "Error al cargar datos") {
  const res = await apiCall;
  if (res?.error) {
    throw new Error(res.message ?? errorMessage);
  }
  return res;
}
