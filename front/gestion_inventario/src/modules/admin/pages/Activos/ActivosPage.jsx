import { useState, useEffect, useCallback } from "react";
import Activos from "./Activos";
import { activosApi } from "../../../../api/activosApi";
import { toast } from "../../../../utils/toast.jsx";
import { getCached, setCache, clearCache } from "../../../../utils/apiCache";
import { resguardosApi } from "../../../../api/resguardosApi";
import { getProfileFromToken } from "../../../../api/authApi";

const CACHE_KEY = "activos";

function mapActivoToDisplay(item) {
  const espacio = item.espacio ?? {};
  const edificio = espacio.edificio ?? {};
  const campus = edificio.campus ?? {};
  const tipoActivo = item.tipoActivo ?? {};
  const modelo = item.modelo ?? {};
  return {
    ...item,
    codigo: item.etiqueta ?? item.codigo,
    descripcionCorta: item.descripcion ?? item.descripcionCorta,
    nombre: modelo.nombre ?? tipoActivo.nombre ?? item.nombre,
    tipoActivo: typeof tipoActivo === "string" ? tipoActivo : (tipoActivo.nombre ?? "—"),
    idTipoActivo: item.tipoActivo?.id,
    idCampus: campus?.id,
    idEdificio: edificio?.id,
    idEspacio: espacio?.id,
    // DFR: Reportado = custodia Resguardado + operativo Reportado
    status: item.estadoOperativo === "Reportado" ? "Reportado" : (item.estadoCustodia ?? item.status ?? "disponible"),
    campus: typeof campus === "string" ? campus : (campus.nombre ?? "—"),
    edificio: typeof edificio === "string" ? edificio : (edificio.nombre ?? "—"),
    aula: espacio.nombreEspacio ?? item.aula ?? "—",
  };
}

export default function ActivosPage() {
  const cached = getCached(CACHE_KEY);
  const [activos, setActivos] = useState(cached ?? []);
  const [loading, setLoading] = useState(!cached);
  const [error, setError] = useState(null);

  const fetchActivos = useCallback(async (silent = false) => {
    if (!silent) {
      setLoading(true);
      setError(null);
    }
    try {
      const res = await activosApi.getActivos(0, 500);
      const content = res?.data?.content ?? res?.content ?? res?.data ?? [];
      const list = Array.isArray(content) ? content : [];
      const soloActivos = list.filter((a) => a.esActivo !== false);
      const mapped = soloActivos.map(mapActivoToDisplay);
      setActivos(mapped);
      setCache(CACHE_KEY, mapped);
    } catch (err) {
      if (!silent) {
        setError(err.message ?? "Error al cargar activos");
        setActivos([]);
      }
    } finally {
      if (!silent) setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (cached) {
      fetchActivos(true); // Refrescar en background si hay caché
    } else {
      fetchActivos();
    }
  }, [fetchActivos]);

  const handleNuevo = async (data) => {
    try {
      await activosApi.save(data);
      clearCache(CACHE_KEY);
      await fetchActivos();
    } catch (err) {
      toast.error(err.message ?? "Error al guardar el activo");
      throw err;
    }
  };

  const handleEditar = async (asset, data) => {
    const id = asset?.id;
    if (!id) return;
    try {
      await activosApi.update(id, data);
      clearCache(CACHE_KEY);
      await fetchActivos();
    } catch (err) {
      toast.error(err.message ?? "Error al actualizar el activo");
      throw err;
    }
  };

  const handleEliminar = async (asset) => {
    const id = asset?.id;
    if (!id) return;
    setActivos((prev) => prev.filter((a) => a.id !== id));
    try {
      await activosApi.toggleStatus(id);
      clearCache(CACHE_KEY);
    } catch (err) {
      clearCache(CACHE_KEY);
      await fetchActivos();
      toast.error(err.message ?? "Error al eliminar");
      throw err;
    }
  };

  const handleAsignarResguardo = async (asset, data) => {
    const adminId = getProfileFromToken()?.id;
    if (!adminId) {
      toast.error("No se pudo identificar tu usuario administrador.");
      return;
    }

    const payload = {
      idActivo: asset.id,
      idUsuarioEmpleado: data.idEmpleado,
      idUsuarioAdmin: adminId,
      observacionesAsig: data.observaciones || null,
    };

    try {
      await resguardosApi.save(payload);
      clearCache(CACHE_KEY);
      await fetchActivos(true); // Refrescar sin spinner para ver el nuevo estatus
      toast.success("Resguardo asignado correctamente");
    } catch (err) {
      throw err;
    }
  };

  return (
    <Activos
      activos={activos}
      loading={loading}
      error={error}
      onNuevo={handleNuevo}
      onEditar={handleEditar}
      onEliminar={handleEliminar}
      onDetalles={handleAsignarResguardo}
      onRefresh={async () => {
        clearCache(CACHE_KEY);
        await fetchActivos();
      }}
    />
  );
}
