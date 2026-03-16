# Guía de integración API QR (Frontend)

Este documento detalla los endpoints disponibles en el módulo de códigos QR y cómo consumirlos desde tu servicio `qrApi.js`.

## Base URL
Todas las peticiones deben dirigirse a: `/api/qr`

> **Nota:** Dependiendo de la configuración del servidor, algunos endpoints pueden requerir la barra final `/`.

## 1. Generar QR de Activo (Imagen)

Recupera la imagen del código QR asociado a un activo. Si no existe, lo genera automáticamente.

- **Método:** `GET`
- **Endpoint:** `/api/qr/{id}`
- **Parámetros:**
  - `id` (Path Variable): ID numérico del activo.

### Ejemplo de Consumo (Axios)

```javascript
/**
 * Obtiene la imagen QR de un activo en formato Base64.
 * @param {number} id - ID del activo (ej. 15)
 * @returns {Promise<string>} - Cadena Base64 de la imagen (data:image/png;base64,...)
 */
const getQrImage = async (id) => {
  try {
    const response = await axios.get(`/api/qr/${id}`);
    // La API devuelve un objeto ApiResponse estándar
    if (!response.data.error) {
      // response.data.data contiene los bytes de la imagen
      // Para mostrarla en un <img>: `data:image/png;base64,${response.data.data}`
      return `data:image/png;base64,${response.data.data}`;
    } else {
      console.error("Error del servidor:", response.data.message);
      return null;
    }
  } catch (error) {
    console.error("Error de red:", error);
    throw error;
  }
};
```

---

## 2. Descargar QR de Activo (PDF)

Genera y descarga un archivo PDF que contiene el código QR del activo, listo para imprimir.

- **Método:** `GET`
- **Endpoint:** `/api/qr/{id}/pdf`
- **Parámetros:**
  - `id` (Path Variable): ID numérico del activo.

### Ejemplo de Consumo (Axios)

```javascript
/**
 * Descarga el PDF del QR de un activo.
 * Nota: La API devuelve el PDF en Base64 dentro de ApiResponse.
 * @param {number} id - ID del activo
 */
const downloadQrPdf = async (id) => {
  try {
    const response = await axios.get(`/api/qr/${id}/pdf`);
    
    if (!response.data.error) {
      const base64Pdf = response.data.data;
      const fileName = `activo_${id}.pdf`;
      
      // Convertir Base64 a Blob para descargar
      const linkSource = `data:application/pdf;base64,${base64Pdf}`;
      const downloadLink = document.createElement("a");
      downloadLink.href = linkSource;
      downloadLink.download = fileName;
      downloadLink.click();
    }
  } catch (error) {
    console.error("Error al descargar PDF:", error);
  }
};
```

---

## 3. Generar QR Personalizado (Genérico)

Genera un QR a partir de un texto arbitrario proporcionado en el cuerpo de la petición.

- **Método:** `POST`
- **Endpoint:** `/api/qr/`  *(Nota la barra al final)*
- **Cuerpo (JSON):**
  ```json
  {
    "texto": "Texto a codificar",
    "alto": 300,  // Opcional, default: 250
    "ancho": 300  // Opcional, default: 250
  }
  ```

### Ejemplo de Consumo

```javascript
const generateCustomQr = async (text, width=300, height=300) => {
  const payload = {
    texto: text,
    ancho: width,
    alto: height
  };
  
  const response = await axios.post('/api/qr/', payload);
  return response.data.data; // Base64 image bytes
};
```

---

## 4. Generar PDF Personalizado (Genérico)

Genera un PDF con un QR de texto arbitrario.

- **Método:** `GET`
- **Endpoint:** `/api/qr/` *(Nota: Mismo path que POST, pero método GET)*
- **Parámetros (Query):**
  - `texto`: Texto a codificar.

### Ejemplo de Consumo

```javascript
const downloadCustomPdf = async (text) => {
  const response = await axios.get('/api/qr/', {
    params: { texto: text }
  });
  // Manejar descarga igual que en el punto 2
};
```

## Estructura de Respuesta (ApiResponse)

Todas las respuestas del servidor tienen este formato JSON uniforme:

```json
{
  "message": "Mensaje descriptivo (ej. 'QR generado correctamente')",
  "data": "...", // Payload principal (String Base64, Objeto, etc.)
  "error": false, // Booleano indicando error
  "status": "OK"  // Código de estado HTTP en texto
}
```

