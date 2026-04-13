import { request } from "./base";

export const bajas = {
  darDeBaja: (data) =>
    request("/api/solicitudes-baja", {
      method: "POST",
      body: JSON.stringify(data),
    }),
};
