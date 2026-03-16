import { request } from "./base";

export const marcasApi = {
  getMarcas: (page = 0, size = 200) =>
    request(`/api/marcas?page=${page}&size=${size}`),
};
