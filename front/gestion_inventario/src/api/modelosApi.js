import { request } from "./base";

export const modelosApi = {
  getModelos: (page = 0, size = 200) =>
    request(`/api/modelos?page=${page}&size=${size}`),
  getModelosByMarca: (marcaId) =>
    request(`/api/modelos/marca/${marcaId}`),
};
