import { request } from "./base";

export const bajas = {
  darDeBaja: (data) =>
    request(`/api/solicitudes-baja/${data.idMantenimiento}`, {
      method: "PUT",
      body: JSON.stringify(data),
    }),
};
