import { request } from "./base";

export const importApi = {
  /**
   * Sube un archivo Excel para importar activos.
   * @param {File} file Archivo Excel
   * @returns {Promise<any>}
   */
  upload: (file) => {
    const formData = new FormData();
    formData.append("file", file);

    return request(`/api/imports/`, {
      method: "POST",
      body: formData,
    });
  },
};
