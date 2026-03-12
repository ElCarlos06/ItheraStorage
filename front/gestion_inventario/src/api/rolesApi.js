import { request } from "./base";

export const rolesApi = {
  /** GET /api/roles */
  getRoles: () => request("/api/roles"),

  /** GET /api/areas */
  getAreas: () => request("/api/areas"),
};
