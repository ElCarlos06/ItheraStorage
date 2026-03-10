# Backup módulo Usuarios

por si acaso xd es q ya lo tenía avanzado cuand oel beto dijo xdd

## Restaurar

1. Copia el contenido de `src/modules/admin/pages/Users/` a `src/modules/admin/pages/Users/` en el proyecto principal.
2. En `src/routes/AdminRouter.jsx` añade:
   ```js
   const Users = lazy(() => import("../modules/admin/pages/Users/Users"));
   // y en las rutas:
   <Route path="/usuarios" element={<Users />} />
   ```
3. En `src/modules/admin/components/layout/Sidebar.jsx` asegúrate de tener el item de Usuarios:
   ```js
   { to: "/usuarios", label: "Usuarios", icon: GenericUsers },
   ```

## Archivos incluidos

- `Users.jsx` – Página principal con buscador, cards y modales
- `Users.css` – Estilos de la vista
- `NewUserModal.jsx` / `NewUserModal.css` – Modal registrar/editar usuario
- `UsersEmptyState.jsx` / `UsersEmptyState.css` – Estado vacío
