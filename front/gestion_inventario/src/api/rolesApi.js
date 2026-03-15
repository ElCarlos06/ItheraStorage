import { request } from "./base";

export const rolesApi = {
  /** GET /api/roles */
  getRoles: () => request("/api/roles"),

  /** GET /api/areas */
  getAreas: (page = 0, size = 10) =>
    request(`/api/areas?page=${page}&size=${size}`),
};
