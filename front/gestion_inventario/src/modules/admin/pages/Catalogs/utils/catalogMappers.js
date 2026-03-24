/** Mapea respuestas del API de ubicaciones al formato de la UI */

export function mapCampusItems(campus = [], edificios = [], espacios = []) {
  const edifPorCampus = {};
  const aulasPorCampus = {};
  (edificios ?? []).forEach((e) => {
    const cid = e.campus?.id;
    if (cid) edifPorCampus[cid] = (edifPorCampus[cid] ?? 0) + 1;
  });
  (espacios ?? []).forEach((s) => {
    const cid = s.edificio?.campus?.id;
    if (cid) aulasPorCampus[cid] = (aulasPorCampus[cid] ?? 0) + 1;
  });
  return (campus ?? []).map((c) => ({
    id: c.id,
    nombre: c.nombre ?? c.name,
    descripcion: c.descripcion,
    edificios: edifPorCampus[c.id] ?? 0,
    aulas: aulasPorCampus[c.id] ?? 0,
  }));
}

export function mapEdificioItems(edificios = [], espacios = []) {
  const aulasPorEdificio = {};
  (espacios ?? []).forEach((s) => {
    const eid = s.edificio?.id;
    if (eid) aulasPorEdificio[eid] = (aulasPorEdificio[eid] ?? 0) + 1;
  });
  return (edificios ?? []).map((e) => ({
    id: e.id,
    nombre: e.nombre ?? e.name,
    campus: e.campus?.nombre,
    idCampus: e.campus?.id,
    edificios: 0,
    aulas: aulasPorEdificio[e.id] ?? 0,
  }));
}

export function mapAulaItems(espacios = []) {
  return (espacios ?? []).map((s) => ({
    id: s.id,
    nombre: s.nombreEspacio ?? s.nombre,
    edificio: s.edificio?.nombre,
    idEdificio: s.edificio?.id,
    campus: s.edificio?.campus?.nombre,
    aulas: 0,
  }));
}
