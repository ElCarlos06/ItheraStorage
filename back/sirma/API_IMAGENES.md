# API de Imagenes — SIRMA

Documentacion de los endpoints REST para la gestion de imagenes vinculadas a Cloudinary.  
Aplica para los tres modulos: **Activos**, **Mantenimientos** y **Reportes**.

Fecha de redaccion: 10 de marzo de 2026  
Autor: Ithera Team

---

## Tabla de contenidos

1. [Informacion general](#informacion-general)
2. [Base URL](#base-url)
3. [Estructura de respuesta](#estructura-de-respuesta)
4. [Estructura del objeto de imagen](#estructura-del-objeto-de-imagen)
5. [Endpoints por modulo](#endpoints-por-modulo)
   - [Activos](#activos)
   - [Mantenimientos](#mantenimientos)
   - [Reportes](#reportes)
6. [Ejemplos de uso con JavaScript (fetch)](#ejemplos-de-uso-con-javascript-fetch)
   - [Subir imagen](#subir-imagen)
   - [Listar imagenes](#listar-imagenes)
   - [Eliminar imagen](#eliminar-imagen)
7. [Ejemplos de uso con Axios](#ejemplos-de-uso-con-axios)
8. [Mostrar imagen en el frontend](#mostrar-imagen-en-el-frontend)
9. [Codigos de respuesta](#codigos-de-respuesta)
10. [Notas importantes](#notas-importantes)

---

## Informacion general

- Las imagenes se almacenan en **Cloudinary**. El backend se encarga de la subida, persistencia de metadatos y eliminacion.
- Desde el frontend solo se envia el archivo mediante `multipart/form-data`. No se requiere interactuar directamente con Cloudinary.
- Cada imagen queda asociada a su entidad padre (activo, mantenimiento o reporte) mediante una clave foranea real en base de datos.

---

## Base URL

```
http://localhost:8080
```

Sustituir por la URL del servidor en produccion cuando corresponda.

---

## Estructura de respuesta

Todas las respuestas siguen el formato estandar `ApiResponse`:

```json
{
  "message": "string",
  "data": object | array | null,
  "error": boolean,
  "status": "HTTP_STATUS"
}
```

| Campo     | Tipo              | Descripcion                                      |
|-----------|-------------------|--------------------------------------------------|
| `message` | `string`          | Mensaje descriptivo del resultado de la operacion |
| `data`    | `object` / `array`| Contenido de la respuesta. `null` si no aplica    |
| `error`   | `boolean`         | `true` si ocurrio un error, `false` en caso contrario |
| `status`  | `string`          | Codigo de estado HTTP (ej: `CREATED`, `OK`, `NOT_FOUND`) |

---

## Estructura del objeto de imagen

Cuando el backend devuelve una imagen (al subir o al listar), el objeto dentro de `data` tiene la siguiente forma:

```json
{
  "id": 1,
  "urlCloudinary": "https://res.cloudinary.com/tu-cloud/image/upload/v1234/sirma/activos/abc123.jpg",
  "publicIdCloudinary": "sirma/activos/abc123",
  "nombreArchivo": "foto_equipo.jpg",
  "fechaSubida": "2026-03-10T14:30:00"
}
```

| Campo                 | Tipo       | Descripcion                                              |
|-----------------------|------------|----------------------------------------------------------|
| `id`                  | `number`   | Identificador unico de la imagen en base de datos        |
| `urlCloudinary`       | `string`   | URL publica de la imagen. Usar directamente en etiquetas `<img>` |
| `publicIdCloudinary`  | `string`   | Identificador interno de Cloudinary (uso exclusivo del backend) |
| `nombreArchivo`       | `string`   | Nombre original del archivo que subio el usuario         |
| `fechaSubida`         | `string`   | Fecha y hora de subida en formato ISO 8601               |

---

## Endpoints por modulo

### Activos

| Metodo   | Ruta                             | Descripcion                          |
|----------|----------------------------------|--------------------------------------|
| `POST`   | `/api/activos/{id}/imagenes`     | Sube una imagen para un activo       |
| `GET`    | `/api/activos/{id}/imagenes`     | Lista todas las imagenes de un activo|
| `DELETE` | `/api/activos/imagenes/{imagenId}` | Elimina una imagen de un activo    |

### Mantenimientos

| Metodo   | Ruta                                    | Descripcion                                  |
|----------|-----------------------------------------|----------------------------------------------|
| `POST`   | `/api/mantenimientos/{id}/imagenes`     | Sube una imagen para un mantenimiento        |
| `GET`    | `/api/mantenimientos/{id}/imagenes`     | Lista todas las imagenes de un mantenimiento |
| `DELETE` | `/api/mantenimientos/imagenes/{imagenId}` | Elimina una imagen de un mantenimiento     |

### Reportes

| Metodo   | Ruta                                | Descripcion                              |
|----------|-------------------------------------|------------------------------------------|
| `POST`   | `/api/reportes/{id}/imagenes`       | Sube una imagen para un reporte          |
| `GET`    | `/api/reportes/{id}/imagenes`       | Lista todas las imagenes de un reporte   |
| `DELETE` | `/api/reportes/imagenes/{imagenId}` | Elimina una imagen de un reporte         |

---

## Ejemplos de uso con JavaScript (fetch)

### Subir imagen

El archivo se envia dentro de un `FormData` con la clave `file`.

```javascript
const subirImagen = async (activoId, archivo) => {
  const formData = new FormData();
  formData.append("file", archivo);

  const response = await fetch(`http://localhost:8080/api/activos/${activoId}/imagenes`, {
    method: "POST",
    body: formData,
    // NO establecer Content-Type manualmente.
    // El navegador lo asigna automaticamente con el boundary correcto.
  });

  const result = await response.json();

  if (!response.ok) {
    console.error("Error:", result.message);
    return null;
  }

  console.log("URL de la imagen:", result.data.urlCloudinary);
  return result.data;
};
```

Para usarlo desde un `<input type="file">`:

```javascript
const inputFile = document.getElementById("miInput");

inputFile.addEventListener("change", async (event) => {
  const archivo = event.target.files[0];
  if (!archivo) return;

  const imagen = await subirImagen(5, archivo); // 5 = id del activo
});
```

### Listar imagenes

```javascript
const listarImagenes = async (activoId) => {
  const response = await fetch(`http://localhost:8080/api/activos/${activoId}/imagenes`);
  const result = await response.json();

  if (!response.ok) {
    console.error("Error:", result.message);
    return [];
  }

  // result.data es un array de objetos de imagen
  return result.data;
};
```

### Eliminar imagen

Se envia el `id` de la imagen (no el id del activo).

```javascript
const eliminarImagen = async (imagenId) => {
  const response = await fetch(`http://localhost:8080/api/activos/imagenes/${imagenId}`, {
    method: "DELETE",
  });

  const result = await response.json();

  if (!response.ok) {
    console.error("Error:", result.message);
    return false;
  }

  console.log(result.message); // "Imagen eliminada correctamente"
  return true;
};
```

---

## Ejemplos de uso con Axios

Si el proyecto frontend usa Axios en lugar de fetch nativo.

### Subir imagen

```javascript
import axios from "axios";

const subirImagen = async (activoId, archivo) => {
  const formData = new FormData();
  formData.append("file", archivo);

  try {
    const { data } = await axios.post(
      `http://localhost:8080/api/activos/${activoId}/imagenes`,
      formData,
      {
        headers: { "Content-Type": "multipart/form-data" },
      }
    );
    return data.data; // objeto de imagen
  } catch (error) {
    console.error("Error:", error.response?.data?.message);
    return null;
  }
};
```

### Listar imagenes

```javascript
const listarImagenes = async (activoId) => {
  try {
    const { data } = await axios.get(
      `http://localhost:8080/api/activos/${activoId}/imagenes`
    );
    return data.data; // array de objetos de imagen
  } catch (error) {
    console.error("Error:", error.response?.data?.message);
    return [];
  }
};
```

### Eliminar imagen

```javascript
const eliminarImagen = async (imagenId) => {
  try {
    const { data } = await axios.delete(
      `http://localhost:8080/api/activos/imagenes/${imagenId}`
    );
    console.log(data.message);
    return true;
  } catch (error) {
    console.error("Error:", error.response?.data?.message);
    return false;
  }
};
```

---

## Mostrar imagen en el frontend

La URL contenida en `urlCloudinary` es publica. Se puede usar directamente en cualquier etiqueta `<img>`.

### HTML

```html
<img src="https://res.cloudinary.com/tu-cloud/image/upload/v1234/sirma/activos/abc123.jpg" alt="Imagen del activo" />
```

### React (JSX)

```jsx
{imagenes.map((img) => (
  <img key={img.id} src={img.urlCloudinary} alt={img.nombreArchivo} />
))}
```

### Ejemplo completo: cargar y mostrar

```jsx
import { useEffect, useState } from "react";

function GaleriaActivo({ activoId }) {
  const [imagenes, setImagenes] = useState([]);

  useEffect(() => {
    const cargar = async () => {
      const response = await fetch(`http://localhost:8080/api/activos/${activoId}/imagenes`);
      const result = await response.json();
      if (response.ok) {
        setImagenes(result.data);
      }
    };
    cargar();
  }, [activoId]);

  return (
    <div>
      {imagenes.map((img) => (
        <img key={img.id} src={img.urlCloudinary} alt={img.nombreArchivo} width={200} />
      ))}
    </div>
  );
}
```

---

## Codigos de respuesta

| Codigo | Estado                  | Significado                                          |
|--------|-------------------------|------------------------------------------------------|
| `201`  | `CREATED`               | Imagen subida y registrada correctamente             |
| `200`  | `OK`                    | Consulta o eliminacion ejecutada correctamente       |
| `404`  | `NOT_FOUND`             | La entidad padre o la imagen no existe               |
| `500`  | `INTERNAL_SERVER_ERROR` | Error al comunicarse con Cloudinary o error interno  |

---

## Notas importantes

1. **No enviar `Content-Type` manualmente con fetch.** Al usar `FormData`, el navegador asigna automaticamente `multipart/form-data` con el boundary correcto. Establecerlo manualmente causa errores.

2. **Limite de tamanio.** El backend acepta archivos de hasta 10 MB por imagen y 15 MB por request total. Estos valores se configuran en `application.properties`.

3. **Carpetas en Cloudinary.** Las imagenes se organizan automaticamente:
   - Activos: `sirma/activos/`
   - Mantenimientos: `sirma/mantenimientos/`
   - Reportes: `sirma/reportes/`

4. **Eliminar imagen.** Al invocar el DELETE, el backend elimina el archivo de Cloudinary y el registro de la base de datos. La URL deja de funcionar inmediatamente.

5. **El parametro del POST siempre es `file`.** El nombre del campo en el `FormData` debe ser exactamente `file`. Cualquier otro nombre sera rechazado.

6. **Intercambiar modulo.** Los tres modulos funcionan de forma identica. Para subir una imagen de mantenimiento, basta con cambiar la ruta base:
   - Activos: `/api/activos/{id}/imagenes`
   - Mantenimientos: `/api/mantenimientos/{id}/imagenes`
   - Reportes: `/api/reportes/{id}/imagenes`

7. **El campo `id` en la ruta POST/GET es el id de la entidad padre** (activo, mantenimiento o reporte). El campo `imagenId` en la ruta DELETE es el id propio de la imagen.

