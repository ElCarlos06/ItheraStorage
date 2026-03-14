import { request } from "./base";

export const activosApi = {
  /**
   * Obtener todos los activos
   * GET /api/activos
   */
  getActivos: (page = 0, size = 1000) => request(`/api/activos?page=${page}&size=${size}`),

  /**
   * Subir una imagen al activo especificado, guardando en Cloudinary.
   * POST /api/activos/{id}/imagenes
   * @param {number} id IDEntificador del activo
   * @param {File} file Archivo de imagen desde un input type="file"
   */
  subirImagen: (id, file) => {
    const formData = new FormData();
    formData.append("file", file);
    // El 'base.js' manejará la remoción del Content-Type para FormData automáticamente
    return request(`/api/activos/${id}/imagenes`, {
      method: "POST",
      body: formData,
    });
  },

  /**
   * Listar todas las imágenes de un activo
   * GET /api/activos/{id}/imagenes
   * @param {number} id Identificador del activo
   */
  listarImagenes: (id) => request(`/api/activos/${id}/imagenes`),

  /**
   * Eliminar una imagen de un activo usando el ID de la imagen
   * DELETE /api/activos/imagenes/{imagenId}
   * @param {number} imagenId ID de la imagen en BD (no del activo)
   */
  eliminarImagen: (imagenId) =>
    request(`/api/activos/imagenes/${imagenId}`, {
      method: "DELETE",
    }),
};
