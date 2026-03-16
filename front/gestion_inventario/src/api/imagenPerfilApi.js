import { request } from "./base";

/**
 * API para manejar la carga y subida de imagenes de perfil.
 */
export const imagenPerfilApi = {
  /**
   * Sube la imagen de perfil del usuario actual desde el token JWT en sessionStorage.
   * @param {string} id - ID del usuario
   * @param {File} file - Archivo de imagen
   * @returns {object|null} objeto con nombreCompleto, correo, rol, area y numeroEmpleado o null si no hay token valido
   */
  upload: (id, file) => {
    const formData = new FormData();
    formData.append("file", file);

    return request(`/api/imagen-perfil/${id}`, {
      method: "POST",
      body: formData,
    });
  },

  /**
   * Obtiene la imagen de perfil del usuario actual desde el token JWT en sessionStorage.
   * @returns {object|null} objeto con nombreCompleto, correo, rol, area y numeroEmpleado o null si no hay token valido
   */
  getByCorreo: (id) => {
    return request(`/api/imagen-perfil/${id}`, {
      method: "GET",
    });
  },
};
