import { request } from "./base";

export const resguardosApi = {
  getResguardos: (page = 0, size = 10, direction = "DESC") =>
    request(`/api/resguardos?page=${page}&size=${size}&direction=${direction}`),

  findById: (id) => request(`/api/resguardos/${id}`),

  getByActivo: (activoId) => request(`/api/resguardos/activo/${activoId}`),

  findByEmpleado: (userId) => request(`/api/resguardos/empleado/${userId}`),

  save: (data) =>
    request("/api/resguardos", {
      method: "POST",
      body: JSON.stringify(data),
    }),

  update: (id, data) =>
    request(`/api/resguardos/${id}`, {
      method: "PUT",
      body: JSON.stringify(data),
    }),
};
