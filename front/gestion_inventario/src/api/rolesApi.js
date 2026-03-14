import { request } from "./base";

export const rolesApi = {
  /** GET /api/roles */
  getRoles: (page = 0, size = 1000) => request(`/api/roles?page=${page}&size=${size}`),

  /** GET /api/areas */
  getAreas: (page = 0, size = 1000) => request(`/api/areas?page=${page}&size=${size}`),
};
