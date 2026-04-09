import { request } from "./base";

export const bitacoraApi = {
  findByActivo: (activoId) => request(`/api/bitacora/activo/${activoId}`),
  getImagenesActivo: (activoId) => request(`/api/imagen-activo/${activoId}`),
};
