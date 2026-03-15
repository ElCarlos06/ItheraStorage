import { request } from "./base";

export const imagenPerfilApi = {
  upload: (file) => {
    const formData = new FormData();
    formData.append("file", file);

    return request("/api/imagen-perfil", {
      method: "POST",
      body: formData,
    });
  },
};
