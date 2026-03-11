import AdminRouter from "./routes/AdminRouter";
import PublicRouter from "./routes/PublicRouter";

/**
 * Punto de entrada de la aplicación.
 * Selecciona el router a montar en función de si existe un token
 * de sesión guardado en sessionStorage.
 */
export default function App() {
  const token = sessionStorage.getItem("token");

  // Si hay token activo se renderiza el área protegida
  if (token) {
    return <AdminRouter />;
  }

  // Sin token se muestran únicamente las rutas públicas
  return <AdminRouter />;
}
