# Guía de componentes y diseño para tontos xd

## 1. Tema y colores (`src/styles/theme.css`)

El proyecto usa variables CSS inspiradas en **Moon Design System**. Importa `theme.css` en `main.jsx` para que las variables estén disponibles globalmente.

### Fondos

| Variable | Uso |
|----------|-----|
| `--moon-content-bg` | Fondo del área de contenido (#f5f6f8) |
| `--moon-goku` | Blanco (#ffffff) |
| `--moon-gohan` | Gris claro (#efefef) |

### Bordes

| Variable | Uso |
|----------|-----|
| `--moon-content-border` | Bordes generales |
| `--moon-beerus` | Bordes secundarios |

### Texto

| Variable | Uso |
|----------|-----|
| `--moon-content-text` / `--moon-bulma` | Texto principal |
| `--moon-content-text-secondary` / `--moon-trunks` | Texto secundario |

### Acento principal (piccolo)

| Variable | Uso |
|----------|-----|
| `--moon-content-focus` | Estados de foco |
| `--moon-piccolo` | Color primario (#4f46e5) |
| `--moon-piccolo-10` | Fondos suaves / hover secundario |
| `--moon-piccolo-60` | Hover de botones |
| `--moon-btn-primary` | Fondo base del botón primary |

### Estados (status)

| Variable | Uso |
|----------|-----|
| `--moon-dodoria` | Error / eliminar |
| `--moon-chichi` | Alerta / reportado |
| `--moon-teal` | Éxito / resguardado |
| `--moon-krillin` | Warning / mantenimiento |
| `--moon-roshi` | Verde / disponible |

### Radio y sombras

- `--moon-radius-s-sm`, `--moon-radius-s-md`, `--moon-radius-s-lg`
- `--moon-shadow-sm`, `--moon-shadow-md`

### Tipografía

- `--moon-font-sans`: "Inter", system fonts

### Uso en CSS

```css
.mi-componente {
  background: var(--moon-goku);
  color: var(--moon-content-text);
  border: 1px solid var(--moon-content-border);
  border-radius: var(--moon-radius-s-md);
}
```

---

## 2. Iconos

Los iconos vienen de **@heathmont/moon-icons**. Siempre se usan a través del componente `Icon` para controlar el tamaño.

### Importar iconos

```jsx
import {
  GenericPlus,
  GenericSearch,
  GenericDelete,
  GenericEdit,
  GenericHome,
  ShopBag,
  SoftwareLogOut,
  FilesImport,
  FilesSave,
  ControlsChevronDown,
  // ... más iconos desde @heathmont/moon-icons o desde el figma donde tenemos los iconos buscas y le picas y ahí dice el nombre xd
} from "@heathmont/moon-icons";
```

### Usar con el componente Icon

```jsx
import Icon from "../components/Icon/Icon";

<Icon icon={GenericPlus} size={30} />
<Icon icon={GenericSearch} size={30} className="mi-clase" />
```

| Prop | Tipo | Descripción |
|------|------|-------------|
| `icon` | Component | Componente de icono de moon-icons |
| `size` | number | Tamaño en píxeles (ej: 30) |
| `className` | string | Clases CSS adicionales |
| `style` | object | Estilos inline |

**Tamaño recomendado:** 30px para botones y acciones principales.

---

## 3. Componentes

### Button

Ubicación: `src/components/Button/Button.jsx`

```jsx
import Button from "../components/Button/Button";
import { GenericPlus, FilesImport } from "@heathmont/moon-icons";

<Button onClick={handleClick}>Guardar</Button>
<Button variant="primary" iconLeft={GenericPlus}>Nuevo</Button>
<Button variant="secondary" iconLeft={FilesImport} iconSize={30}>Importar Excel</Button>
<Button variant="secondary" fullWidth>Cancelar</Button>
```

| Prop | Tipo | Default | Descripción |
|------|------|---------|-------------|
| `variant` | `"primary"` \| `"secondary"` \| `"tertiary"` \| `"ghost"` | `"primary"` | Estilo del botón |
| `size` | `"small"` \| `"medium"` \| `"large"` | `"medium"` | Tamaño |
| `iconLeft` | Component | - | Icono a la izquierda |
| `iconRight` | Component | - | Icono a la derecha |
| `iconSize` | number | 30 | Tamaño de iconos en px |
| `fullWidth` | boolean | false | Ancho completo |
| `disabled` | boolean | false | Deshabilitado |
| `type` | string | `"button"` | type del button nativo |
| `onClick` | function | - | Handler de click |
| `children` / `text` | ReactNode / string | - | Contenido del botón |

**Variantes:**
- **primary**: fondo azul, texto blanco (acciones principales)
- **secondary**: borde azul, fondo transparente (Importar, Cancelar)
- **tertiary**: fondo gris suave
- **ghost**: sin fondo ni borde

---

### Input

Ubicación: `src/components/Input/Input.jsx`

```jsx
import Input from "../components/Input/Input";

<Input
  label="Email"
  placeholder="Ej: usuario@ejemplo.com"
  value={email}
  onChange={(e) => setEmail(e.target.value)}
  fullWidth
/>

<Input
  label="Contraseña"
  type="password"
  endAdornment={<MiIconoOjo />}
  fullWidth
/>
```

| Prop | Tipo | Descripción |
|------|------|-------------|
| `label` | string | Etiqueta arriba del input |
| `labelClassName` | string | Clases para el label |
| `endAdornment` | ReactNode | Elemento a la derecha (ej: ícono ojo) |
| `fullWidth` | boolean | Ancho completo |
| `id` | string | id del input (auto si hay label) |
| `...props` | - | Resto de props de `<input>` (type, placeholder, value, onChange, etc.) |

---

### Buscador

Campo de búsqueda con ícono de lupa. Ubicación: `src/components/Buscador/Buscador.jsx`

```jsx
import Buscador from "../components/Buscador/Buscador";

<Buscador
  placeholder="Buscar activo por nombre...."
  value={search}
  onChange={handleSearchChange}
  aria-label="Buscar activos"
/>
```

| Prop | Tipo | Descripción |
|------|------|-------------|
| `value` | string | Valor controlado |
| `onChange` | function | Recibe el evento (e) |
| `placeholder` | string | Placeholder del input |

---

### Card

Contenedor con borde y esquinas redondeadas. `src/components/Card/Card.jsx`

```jsx
import Card from "../components/Card/Card";

<Card padding="medium">
  <p>Contenido de la tarjeta</p>
</Card>
```

| Prop | Tipo | Descripción |
|------|------|-------------|
| `padding` | `"medium"` | Padding interior (20px 24px) |
| `className` | string | Clases adicionales |

---

### StatusBadge

Badge para estados de activos. `src/components/StatusBadge/StatusBadge.jsx`

```jsx
import StatusBadge from "../components/StatusBadge/StatusBadge";

<StatusBadge status="disponible" size="small">Disponible</StatusBadge>
<StatusBadge status="resguardado">Resguardado</StatusBadge>
```

| Prop | Tipo | Default | Descripción |
|------|------|---------|-------------|
| `status` | string | - | Estado (define el color) |
| `size` | `"small"` \| `"medium"` \| `"large"` | `"small"` | Tamaño |
| `children` | ReactNode | - | Texto a mostrar |

**Valores de `status`:**

- `disponible` – Verde
- `resguardado` – Teal
- `mantenimiento` – Amarillo
- `en proceso` – Azul
- `baja` – Rojo
- `reportado` – Rojo/Alerta
- `neutral` – Gris

---

### Modal

Overlay centrado con cierre al hacer clic fuera. `src/components/Modal/Modal.jsx`

```jsx
import Modal from "../components/Modal/Modal";

<Modal open={isOpen} onClose={() => setIsOpen(false)} className="mi-modal">
  <h2>Título</h2>
  <p>Contenido del modal</p>
</Modal>
```

| Prop | Tipo | Descripción |
|------|------|-------------|
| `open` | boolean | Si el modal está visible |
| `onClose` | function | Se ejecuta al cerrar (click en overlay o X) |
| `className` | string | Clases para el contenedor del contenido |
| `children` | ReactNode | Contenido del modal |

El overlay cierra el modal al hacer clic; el contenido debe llamar `e.stopPropagation()` o usar el `onClose` propio para el botón cerrar.

---

## 4. Ejemplo completo

```jsx
import { useState } from "react";
import Button from "../components/Button/Button";
import Input from "../components/Input/Input";
import Buscador from "../components/Buscador/Buscador";
import Card from "../components/Card/Card";
import Modal from "../components/Modal/Modal";
import StatusBadge from "../components/StatusBadge/StatusBadge";
import Icon from "../components/Icon/Icon";
import { GenericPlus, GenericSearch } from "@heathmont/moon-icons";

function MiVista() {
  const [search, setSearch] = useState("");
  const [modalOpen, setModalOpen] = useState(false);

  return (
    <div>
      <Buscador
        placeholder="Buscar..."
        value={search}
        onChange={(e) => setSearch(e.target.value)}
      />
      <div style={{ display: "flex", gap: 12, marginTop: 16 }}>
        <Button variant="secondary" onClick={() => {}}>Importar</Button>
        <Button variant="primary" iconLeft={GenericPlus} onClick={() => setModalOpen(true)}>
          Nuevo
        </Button>
      </div>
      <Card padding="medium" style={{ marginTop: 24 }}>
        <StatusBadge status="disponible" size="small">Disponible</StatusBadge>
      </Card>
      <Modal open={modalOpen} onClose={() => setModalOpen(false)}>
        <Input label="Nombre" placeholder="Ej: Laptop" fullWidth />
        <Button fullWidth>Guardar</Button>
      </Modal>
    </div>
  );
}
```

---

## 5. Estructura de carpetas

```
src/
├── components/          # Componentes globales
│   ├── Button/
│   ├── Buscador/
│   ├── Card/
│   ├── Icon/
│   ├── Input/
│   ├── Modal/
│   └── StatusBadge/
├── styles/
│   └── theme.css        # Tokens de diseño
└── modules/
    ├── admin/           # Módulo admin
    └── public/          # Login, recuperar contraseña
```
