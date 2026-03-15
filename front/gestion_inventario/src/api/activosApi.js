import { request } from "./base";

export const activosApi = {
  /**
   * Obtener todos los activos
   * GET /api/activos
   */
  getActivos: (page = 0, size = 10) =>
    request(`/api/activos?page=${page}&size=${size}`),
};
