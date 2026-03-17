# Guía de Integración de Escaneo QR para Apps Móviles

## 1. ¿Necesitamos usar la misma librería (ZXing) que el backend?

**Respuesta corta: NO.**

El backend utiliza **ZXing** (Zebra Crossing) solamente para **generar** las imágenes de los códigos QR. 
Para **leer/escanear** los códigos desde la aplicación móvil, **puedes utilizar cualquier librería de escaneo estándar** disponible para tu framework o lenguaje nativo. Los códigos QR generados siguen el estándar ISO/IEC 18004 y son legibles por cualquier lector universal.

### Librerías recomendadas por plataforma:
- **React Native:** `react-native-vision-camera` (con plugin de códigos), `react-native-camera`.
- **Flutter:** `mobile_scanner`, `qr_code_scanner`.
- **Android Nativo:** Google ML Kit (Barcode Scanning API), ZXing Android Embedded.
- **iOS Nativo:** AVFoundation (nativo del sistema), ML Kit.

---

## 2. Estructura de Datos en el QR

El contenido codificado dentro del QR es un **JSON simple en texto plano**.

**Formato:**
```json
{"id": 12345}
```

*Donde `12345` es el ID único del activo en la base de datos.*

> **Nota importante:** El scanner devolverá este string literal. La app móvil debe encargarse de parsearlo.

---

## 3. Flujo de Trabajo en la App Móvil

1.  **Escanear:** El usuario apunta la cámara al código QR.
2.  **Obtener String:** La librería de escaneo devuelve el string descifrado (ej: `'{"id": 45}'`).
3.  **Parsear y Validar:**
    *   Intentar convertir el string a JSON (`JSON.parse()`).
    *   Verificar si existe la propiedad `id`.
    *   *Si falla:* Mostrar error "Código QR no reconocido" o "Este código no pertenece a un activo válido".
4.  **Consultar API:**
    *   Tomar el `id` obtenido (ej: `45`).
    *   Realizar una petición `GET` al endpoint de detalles del activo.

---

## 4. Endpoints de la API para Móvil

### Consultar detalle de Activo (por ID escaneado)
- **Método:** `GET`
- **URL:** `/api/activos/{id}`
- **Ejemplo:** `/api/activos/45`
- **Respuesta:** Devuelve toda la información del activo (nombre, serie, ubicación, imágenes, estado).

### Obtener imagen del QR (si se necesita mostrar en pantalla)
- **Método:** `GET`
- **URL:** `/api/qr/{id}`
- **Respuesta:** Imagen PNG del código QR.

---

## 5. Notas Adicionales

*   **Offline:** Si la app soporta modo offline, el ID escaneado puede usarse para buscar en la base de datos local (SQLite/Realm) sincronizada previamente.
*   **Formato antiguo:** Si existen activos antiguos con códigos QR que no siguen este formato JSON, la app debería tener un fallback para tratar el string como un código simple o número de serie si es necesario.

