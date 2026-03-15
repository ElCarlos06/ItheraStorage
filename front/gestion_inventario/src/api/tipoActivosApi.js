import { request } from "./base";

/**
 * Cliente API para Tipos de Activos
 */
export const tipoActivosApi = {
  /**
   * Obtener todos los tipos de activos
   * GET /api/tipo-activos
   */
  getTipoActivos: (page = 0, size = 10) =>
    request(`/api/tipo-activos?page=${page}&size=${size}`),

  /**
   * Obtener un tipo de activo por ID
   * GET /api/tipo-activos/{id}
   * @param {number} id Identificador del tipo de activo
   */
  getTipoActivoById: (id) => request(`/api/tipo-activos/${id}`),

  /**
   * Crear un nuevo tipo de activo
   * POST /api/tipo-activos
   * @param {Object} data Datos del tipo de activo
   */
  crearTipoActivo: (data) =>
    request("/api/tipo-activos", {
      method: "POST",
      body: JSON.stringify(data),
    }),

  /**
   * Actualizar un tipo de activo existente
   * PUT /api/tipo-activos/{id}
   * @param {number} id Identificador
   * @param {Object} data Datos actualizados
   */
  actualizarTipoActivo: (id, data) =>
    request(`/api/tipo-activos/${id}`, {
      method: "PUT",
      body: JSON.stringify(data),
    }),

  /**
   * Activar o desactivar un tipo de activo
   * PATCH /api/tipo-activos/{id}/status
   * @param {number} id Identificador
   */
  toggleStatusTipoActivo: (id) =>
    request(`/api/tipo-activos/${id}/status`, {
      method: "PATCH",
    }),
};
