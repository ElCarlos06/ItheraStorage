import { request } from "./base";

export const activosApi = {
  /**
   * Obtener todos los activos
   * GET /api/activos
   */
  getActivos: (page = 0, size = 10, direction = "DESC") =>
    request(`/api/activos?page=${page}&size=${size}&direction=${direction}`),

  /**
   * Crear un nuevo activo
   * POST /api/activos
   */
  save: (data) =>
    request("/api/activos", {
      method: "POST",
      body: JSON.stringify(data),
    }),

  /**
   * Actualizar un activo existente
   * PUT /api/activos/{id}
   */
  update: (id, data) =>
    request(`/api/activos/${id}`, {
      method: "PUT",
      body: JSON.stringify(data),
    }),

  /**
   * Desactivar/reactivar activo (toggle esActivo)
   * PATCH /api/activos/{id}/status
   */
  toggleStatus: (id) =>
    request(`/api/activos/${id}/status`, {
      method: "PATCH",
    }),
  /**
   * Obtener estadísticas de activos
   * GET /api/activos/stats
   */
  getStats: () => request("/api/activos/stats").then((r) => r.data),
};
