import { request } from "./base";

export const ubicacionesApi = {
  /** Ubicaciones: Campus */
  getCampus: () => request("/api/campus"),
  createCampus: (body) => request("/api/campus", { method: "POST", body: JSON.stringify(body) }),
  updateCampus: (id, body) => request(`/api/campus/${id}`, { method: "PUT", body: JSON.stringify(body) }),
  toggleStatusCampus: (id) => request(`/api/campus/${id}/status`, { method: "PATCH" }),

  /** Ubicaciones: Edificios */
  getEdificios: () => request("/api/edificios"),
  getEdificiosByCampus: (campusId) => request(`/api/edificios/campus/${campusId}`),
  createEdificio: (body) => request("/api/edificios", { method: "POST", body: JSON.stringify(body) }),
  updateEdificio: (id, body) => request(`/api/edificios/${id}`, { method: "PUT", body: JSON.stringify(body) }),
  toggleStatusEdificio: (id) => request(`/api/edificios/${id}/status`, { method: "PATCH" }),

  /** Ubicaciones: Espacios (Aulas/Lab) */
  getEspacios: () => request("/api/espacios"),
  getEspaciosByEdificio: (edificioId) => request(`/api/espacios/edificio/${edificioId}`),
  createEspacio: (body) => request("/api/espacios", { method: "POST", body: JSON.stringify(body) }),
  updateEspacio: (id, body) => request(`/api/espacios/${id}`, { method: "PUT", body: JSON.stringify(body) }),
  toggleStatusEspacio: (id) => request(`/api/espacios/${id}/status`, { method: "PATCH" }),
};
