import { request } from "./base";

const reportes = {
  /** GET /api/reportes?page=:page&size=:size */
  getReportes: (page = 0, size = 10, direction = "DESC") =>
    request(`/api/reportes?page=${page}&size=${size}&direction=${direction}`),

  /** GET /api/reportes/:id */
  getReporteById: (id) => request(`/api/reportes/${id}`),

  /** GET /api/reportes/activo/:activoId */
  getReportesByActivo: (activoId) =>
    request(`/api/reportes/activo/${activoId}`),

  /** POST /api/reportes */
  createReporte: (body) =>
    request(`/api/reportes`, {
      method: "POST",
      body: JSON.stringify(body),
    }),

  /** PUT /api/reportes/:id */
  updateReporte: (id, body) =>
    request(`/api/reportes/${id}`, {
      method: "PUT",
      body: JSON.stringify(body),
    }),
};

const mantenimientos = {
  /** GET /api/reportes?page=:page&size=:size */
  getMantenimientos: (page = 0, size = 10, direction = "DESC") =>
    request(
      `/api/mantenimientos?page=${page}&size=${size}&direction=${direction}`,
    ),

  /** GET /api/reportes/:id */
  getMantenimientoById: (id) => request(`/api/mantenimientos/${id}`),

  /** GET /api/reportes/activo/:activoId */
  getMantenimientosByActivo: (activoId) =>
    request(`/api/mantenimientos/activo/${activoId}`),

  /** POST /api/reportes */
  createMantenimiento: (body) =>
    request(`/api/mantenimientos`, {
      method: "POST",
      body: JSON.stringify(body),
    }),

  /** PUT /api/reportes/:id */
  updateMantenimiento: (id, body) =>
    request(`/api/mantenimientos/${id}`, {
      method: "PUT",
      body: JSON.stringify(body),
    }),
};

export const solicitudesApi = {
  reportes,
  mantenimientos,
};
