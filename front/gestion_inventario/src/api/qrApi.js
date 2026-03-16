import { request } from "./base";

export const qrApi = {
  /**
   * GET /api/qr/{id}
   * Obtiene la imagen QR de un activo en formato Base64.
   * Si no existe, lo genera automáticamente.
   * @param {number} id - ID del activo
   * @returns {Promise<{message, data, error, status}>} data = string Base64 de la imagen
   */
  getQrByActivo: (id) => request(`/api/qr/${id}`),

  /**
   * GET /api/qr/{id}/pdf
   * Descarga el PDF del QR de un activo como string Base64.
   * @param {number} id - ID del activo
   * @returns {Promise<{message, data, error, status}>} data = string Base64 del PDF
   */
  getPdfByActivo: (id) => request(`/api/qr/${id}/pdf`),

  /**
   * POST /api/qr/
   * Genera un QR a partir de un texto arbitrario.
   * @param {{ texto: string, alto?: number, ancho?: number }} body
   * @returns {Promise<{message, data, error, status}>} data = string Base64 de la imagen
   */
  generateCustomQr: (body) =>
    request(`/api/qr/`, {
      method: "POST",
      body: JSON.stringify(body),
    }),

  /**
   * GET /api/qr/?texto=...
   * Genera un PDF con un QR de texto arbitrario.
   * @param {string} texto
   * @returns {Promise<{message, data, error, status}>} data = string Base64 del PDF
   */
  downloadCustomPdf: (texto) =>
    request(`/api/qr/?texto=${encodeURIComponent(texto)}`),
};
