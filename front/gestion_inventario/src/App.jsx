import AdminRouter from "./routes/AdminRouter";
import PublicRouter from "./routes/PublicRouter";
import { useLiveUpdates } from "./hooks/useLiveUpdates";

/**
 * Wrapper que activa las actualizaciones en tiempo real vía SSE
 * mientras el usuario está autenticado.
 */
function AuthenticatedApp() {
  useLiveUpdates();
  return <AdminRouter />;
}

/**
 * Punto de entrada de la aplicación.
 * Selecciona el router a montar en función de si existe un token
 * de sesión guardado en sessionStorage.
 */
export default function App() {
  const token = sessionStorage.getItem("token");

  // Si hay token activo se renderiza el área protegida con SSE activo
  if (token) {
    return <AuthenticatedApp />;
  }

  // Sin token se muestran únicamente las rutas públicas
  return <PublicRouter />;
}
