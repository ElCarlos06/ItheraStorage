# Guía de Implementación de Escaneo de QR para Móvil

Este documento describe la estructura de los códigos QR generados por el backend y cómo la aplicación móvil debe procesarlos.

## 1. ¿Necesitamos la mimsa librería (ZXing)?

**No necesariamente.**

El backend utiliza **ZXing (Zebra Crossing)** para *generar* los códigos, pero los códigos QR son un estándar internacional (ISO/IEC 18004). Cualquier librería de escaneo de QR moderna en móvil podrá leerlos sin problemas.

Sin embargo, si desean consistencia o están usando tecnologías nativas, ZXing tiene puertos para casi todas las plataformas:
- **Android**: `zxing-android-embedded` o la app "Barcode Scanner".
- **iOS**: AVFoundation (nativo) o librerías basadas en ZXing.
- **Flutter**: `mobile_scanner`, `qr_code_scanner`.
- **React Native**: `react-native-camera`, `react-native-qrcode-scanner`.

---

## 2. Estructura de los Datos en el QR

El backend genera los códigos QR conteniendo una cadena de texto en formato **JSON**.

### Formato
```json
{"id": "<ID_DEL_ACTIVO>"}
```

### Ejemplo
Si el activo tiene el ID `45`, el contenido crudo del QR será:
```text
{"id":45}
```

> **Nota:** Si un activo ya tenía un código QR asignado manualmente en la base de datos (campo `qrCodigo`), el QR contendrá ese valor específico. Si no, se genera automáticamente el JSON con el ID como se muestra arriba.

---

## 3. Flujo de Trabajo Sugerido para la App Móvil

1. **Escanear**: El usuario abre la cámara en la app y escanea el código.
2. **Decodificar**: La librería móvil devuelve el string crudo (ej. `{"id":45}`).
3. **Parsear**:
   - Intentar parsear el string como JSON.
   - Extraer la propiedad `id`.
   - *Manejo de errores:* Si no es un JSON válido o no tiene `id`, mostrar mensaje "Código QR no válido para Activos360".
4. **Consultar API**: Usar el ID obtenido para consultar los detalles del activo al backend.

### Endpoint para consultar detalle
Una vez obtenido el `id` (ej. `45`), llamar a:

**GET** `/api/activos/45`

**Respuesta Exitosa (200 OK):**
```json
{
  "message": "Activo recuperado", // o similar
  "data": {
    "id": 45,
    "nombre": "Laptop Dell Latitude",
    "serie": "ABC-123",
    "etiqueta": "ACT-2024-001",
    ...
  },
  "error": false,
  "status": "OK"
}
```

---

## 4. Endpoints Relacionados con QR (Referencia)

Si la aplicación móvil necesita mostrar el QR o generarlo bajo demanda:

| Método | URL | Descripción |
| :--- | :--- | :--- |
| `POST` | `/api/qr/` | Genera un QR arbitrario (Body: `{ "texto": "...", "ancho": 300, "alto": 300 }`). Retorna bytes de imagen. |
| `GET` | `/api/qr/{id}` | Retorna la **imagen PNG** del QR para el activo `{id}`. |
| `GET` | `/api/qr/{id}/pdf` | Descarga un **PDF** con el QR del activo `{id}` listo para imprimir. |

---

## 5. Resumen Técnico para Desarrolladores Móviles

- **Encoding**: UTF-8 (Estándar para JSON).
- **Error Correction Level**: Por defecto de la librería (suele ser L o M).
- **Acción**: Al detectar el QR, no abrir URL automáticamente (no es una URL web), sino parsear el JSON y navegar internamente a la pantalla de "Detalle de Activo".

