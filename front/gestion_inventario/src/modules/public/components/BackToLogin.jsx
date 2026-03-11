import { Link } from "react-router-dom";
import "../styles/public.css";

/**
 * Enlace estilizado para regresar al login.
 * Diseño integrado con el layout de las vistas públicas.
 */
export default function BackToLogin() {
  return (
    <div className="back-to-login">
      <Link to="/login" className="back-to-login__link">
        <span className="back-to-login__icon" aria-hidden="true">
          ←
        </span>
        <span>Volver al inicio de sesión</span>
      </Link>
    </div>
  );
}
