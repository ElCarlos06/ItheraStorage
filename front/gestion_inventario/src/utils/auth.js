/**
 * Obtiene el correo del usuario actual desde el token JWT en sessionStorage.
 * El subject (sub) del JWT contiene el correo del usuario logueado.
 * @returns {string|null} correo o null si no hay token válido
 */
export function getCurrentUserCorreo() {
  const token = sessionStorage.getItem("token");
  if (!token) return null;

  try {
    const payload = token.split(".")[1];
    if (!payload) return null;
    const decoded = JSON.parse(atob(payload.replace(/-/g, "+").replace(/_/g, "/")));
    return decoded.sub ?? null;
  } catch {
    return null;
  }
}

/** Cierra sesión y recarga para mostrar el login */
export function logout() {
  sessionStorage.removeItem("token");
  window.location.replace("/");
}
