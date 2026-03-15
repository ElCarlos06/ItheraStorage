# Guía de Migración a Paginación (Backend & Frontend)

Se ha implementado paginación en la mayoría de los endpoints de listado (`findAll`) del backend para mejorar el rendimiento y la experiencia de usuario. A continuación se detallan los cambios y cómo adaptar el frontend.

**Nota importante:** Los catálogos pequeños como **Roles** y **Prioridades** NO están paginados y devuelven una lista simple `[...]`.

## 1. Cambios en el Backend

Los endpoints `GET` que devuelven listas grandes (ej. `/api/tipo-activos`, `/api/users`, etc.) ahora aceptan parámetros de paginación opcionales y devuelven un objeto `Page` en lugar de una `List` simple.

### Parámetros de Petición (Query Params)
Ahora puedes enviar los siguientes parámetros en la URL:
- `page`: Número de página (inicia en **0**). *Default: 0*.
- `size`: Cantidad de elementos por página. *Default: 10*.
- `sort`: Campo para ordenar (ej. `id,desc` o `nombre,asc`). *Default: id*.

**Ejemplo de URL:**
`GET /api/tipo-activos?page=0&size=5&sort=nombre,asc`

### Nueva Estructura de Respuesta JSON
Anteriormente, la propiedad `data` contenía directamente el array `[...]`.
Ahora, `data` contiene un objeto **Page** con metadatos y el contenido real en la propiedad `content`.

**Respuesta Anterior (Lista simple):**
```json
{
  "message": "OK",
  "error": false,
  "status": "OK",
  "data": [ 
    { "id": 1, "nombre": "Laptop" }, 
    { "id": 2, "nombre": "Proyector" } 
  ]
}
```

**Nueva Respuesta (Paginada):**
```json
{
  "message": "OK",
  "error": false,
  "status": "OK",
  "data": {
    "content": [  <-- AQUÍ ESTÁ AHORA TU ARRAY DE DATOS
      { "id": 1, "nombre": "Laptop" },
      { "id": 2, "nombre": "Proyector" }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 5,
      ...
    },
    "totalPages": 10,       <-- Útil para saber cuántas páginas dibujar
    "totalElements": 50,    <-- Total de registros en la BD
    "last": false,
    "size": 5,
    "number": 0,
    ...
  }
}
```

---

## 2. Cambios Requeridos en el Frontend

Dado que tu función de petición era:
```javascript
getTipoActivos: () => request("/api/tipo-activos")
```

Debes actualizarla para soportar los parámetros y procesar la nueva estructura.

### Paso 1: Actualizar la llamada a la API
Modifica tus servicios para aceptar un objeto de parámetros (o argumentos individuales).

**Antes:**
```javascript
const getAll = () => request("/api/tipo-activos");
```

**Ahora (Sugerencia):**
```javascript
// Opción A: Usando query strings manuales (o template strings)
const getAll = (page = 0, size = 10) => 
  request(`/api/tipo-activos?page=${page}&size=${size}`);

// Opción B: Si tu utilería 'request' o 'axios' soporta params (Recomendado)
const getAll = (params) => request("/api/tipo-activos", { params }); 
// Uso: getAll({ page: 0, size: 10 })
```

### Paso 2: Actualizar el consumo de datos
Donde antes hacías un `.map` directamente sobre `response.data`, ahora debes hacerlo sobre `response.data.content`.

**Antes:**
```javascript
service.getAll().then(response => {
  const lista = response.data; // Era un array
  setItems(lista);
});
```

**Ahora:**
```javascript
service.getAll(0, 10).then(response => {
  const dataPaginada = response.data; // Es el objeto Page
  
  const lista = dataPaginada.content; // <-- Aquí está el array
  const totalPaginas = dataPaginada.totalPages; // Para tu componente de paginación
  
  setItems(lista);
  setTotalPages(totalPaginas);
});
``` 

### Endpoints Afectados
La paginación se aplicó a:
- `/api/tipo-activos`
- `/api/marcas`
- `/api/modelos`
- `/api/prioridades`
- `/api/reportes`
- `/api/users`
- `/api/tipo-fallas`
- `/api/solicitudes-baja`
- `/api/roles`
- `/api/resguardos`
- `/api/activos` (Assets)
- `/api/mantenimientos`
- `/api/edificios`
- `/api/espacios`
- `/api/campus`
- `/api/areas`

