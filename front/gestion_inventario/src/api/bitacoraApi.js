import { request } from "./base";

export const bitacoraApi = {
  findByActivo: (activoId) => request(`/api/bitacora/activo/${activoId}`),
};
