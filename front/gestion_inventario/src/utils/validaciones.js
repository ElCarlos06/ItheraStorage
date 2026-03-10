/**
 * Validaciones para el formulario de registro de usuarios.
 * Alineadas con el backend (RegisterService).
 */

/** Regex CURP oficial mexicano: 4 letras + 6 dígitos fecha + H/M + 2 entidad + 3 consonantes + 1 alfanum + 1 dígito */
const CURP_REGEX = /^[A-Z]{4}\d{6}[HM][A-Z]{2}[B-DF-HJ-NP-TV-Z]{3}[A-Z0-9]\d$/;

/** Nombre: letras, acentos, espacios. No inicia con punto, coma o especial. */
const NOMBRE_REGEX = /^[A-ZÁÉÍÓÚÜÑa-záéíóúüñ][A-ZÁÉÍÓÚÜÑa-záéíóúüñ ']*$/;

/**
 * Valida formato CURP mexicano (18 caracteres).
 * @returns {string|null} Mensaje de error o null si es válido
 */
export function validarCurp(curp) {
  if (!curp || curp.trim().length === 0) {
    return "La CURP es obligatoria";
  }
  const c = curp.trim().toUpperCase();
  if (c.length !== 18) {
    return "La CURP debe tener exactamente 18 caracteres";
  }
  if (!CURP_REGEX.test(c)) {
    return "La CURP no cumple el formato oficial mexicano";
  }
  return null;
}

/**
 * Valida nombre completo.
 * @returns {string|null} Mensaje de error o null si es válido
 */
export function validarNombre(nombre) {
  if (!nombre || nombre.trim().length === 0) {
    return "El nombre completo es obligatorio";
  }
  if (nombre.trim().length < 3) {
    return "El nombre debe tener al menos 3 caracteres";
  }
  if (!NOMBRE_REGEX.test(nombre.trim())) {
    return "El nombre no debe iniciar con puntos, comas ni caracteres especiales";
  }
  return null;
}

/**
 * Valida correo electrónico.
 */
export function validarCorreo(correo) {
  if (!correo || correo.trim().length === 0) {
    return "El correo es obligatorio";
  }
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(correo.trim())) {
    return "Formato de correo no válido";
  }
  return null;
}

/**
 * Valida fecha de nacimiento y mayoría de edad (18+).
 * @param fechaStr Formato yyyy-MM-dd
 */
export function validarFechaNacimiento(fechaStr) {
  if (!fechaStr || fechaStr.trim().length === 0) {
    return "La fecha de nacimiento es obligatoria";
  }
  try {
    const fecha = new Date(fechaStr);
    if (isNaN(fecha.getTime())) {
      return "Fecha inválida (use yyyy-MM-dd)";
    }
    const hoy = new Date();
    let edad = hoy.getFullYear() - fecha.getFullYear();
    const m = hoy.getMonth() - fecha.getMonth();
    if (m < 0 || (m === 0 && hoy.getDate() < fecha.getDate())) edad--;
    if (edad < 18) {
      return "El usuario debe ser mayor de 18 años";
    }
    return null;
  } catch {
    return "Formato de fecha inválido";
  }
}

/**
 * Valida rol seleccionado (idRol).
 */
export function validarRol(idRol) {
  if (idRol === "" || idRol == null || idRol === undefined) {
    return "El rol es obligatorio";
  }
  return null;
}

/**
 * Valida área seleccionada (idArea).
 */
export function validarArea(idArea) {
  if (idArea === "" || idArea == null || idArea === undefined) {
    return "El área es obligatoria";
  }
  return null;
}
