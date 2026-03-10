import { Routes, Route, Navigate } from "react-router-dom";

import Login from "../modules/public/pages/Login";
import PasswordRecovery from "../modules/public/pages/PasswordRecovery";

/**
 * Rutas publicas de la aplicación en las que se definen las rutas que no requieren autenticación
 * @returns {JSX.Element}
 */
export default function PublicRouter() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" />} />
      <Route path="/login" element={<Login />} />
      <Route path="/password-recovery" element={<PasswordRecovery />} />

      {/** Ruta por defecto */}
      <Route path="*" element={<Navigate to="/login" />} />
    </Routes>
  );
}
