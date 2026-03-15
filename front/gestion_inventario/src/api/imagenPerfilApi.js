import { request } from "./base";

export const imagenPerfilApi = {
  upload: (id, file) => {
    const formData = new FormData();
    formData.append("file", file);

    return request(`/api/imagen-perfil/${id}`, {
      method: "POST",
      body: formData,
    });
  },

  getByCorreo: (id) => {
    return request(`/api/imagen-perfil/${id}`, {
      method: "GET",
    });
  },
};
