import Activos from "./Activos";
import { activosApi } from "../../../../api/activosApi";
import { resguardosApi } from "../../../../api/resguardosApi";
import { getProfileFromToken } from "../../../../api/authApi";
import { toast } from "../../../../utils/toast.jsx";
import { usePaginatedQuery } from "../../../../hooks/usePaginatedQuery";

function mapActivoToDisplay(item) {
  const espacio = item.espacio ?? {};
  const edificio = espacio.edificio ?? {};
  const campus = edificio.campus ?? {};
  const tipoActivo = item.tipoActivo ?? {};
  return {
    ...item,
    codigo: item.etiqueta ?? item.codigo,
    descripcionCorta: item.descripcion ?? item.descripcionCorta,
    nombre: tipoActivo.nombre ?? item.nombre,
    tipoActivo:
      typeof tipoActivo === "string" ? tipoActivo : (tipoActivo.nombre ?? "—"),
    idTipoActivo: item.tipoActivo?.id,
    idCampus: campus?.id,
    idEdificio: edificio?.id,
    idEspacio: espacio?.id,
    status:
      item.estadoOperativo === "Reportado"
        ? "Reportado"
        : (item.estadoCustodia ?? "disponible"),
    campus: campus.nombre ?? "—",
    edificio: edificio.nombre ?? "—",
    aula: espacio.nombreEspacio ?? "—",
  };
}

const PAGE_SIZE = 10;

export default function ActivosPage() {
  const {
    isLoading,
    isFetching,
    error: errorMessage,
    invalidate,
    currentPage,
    setCurrentPage,
    pageSize: PAGE_SIZE_USED,
    content,
    totalPages,
    totalElements,
  } = usePaginatedQuery({
    queryKey: "activos",
    queryFn: (page, size) => activosApi.getActivos(page, size),
    errorMessage: "Error al cargar activos",
    pageSize: PAGE_SIZE,
  });

  const activos = (content ?? [])
    .filter((a) => a.esActivo !== false)
    .map(mapActivoToDisplay);

  const handleNuevo = async (formData) => {
    try {
      const res = await activosApi.save(formData);
      if (res?.error) throw new Error(res.message ?? "Error al guardar");
      invalidate();
    } catch (err) {
      toast.error(err.message ?? "Error al guardar el activo");
      throw err;
    }
  };

  const handleEditar = async (asset, formData) => {
    if (!asset?.id) return;
    try {
      const res = await activosApi.update(asset.id, formData);
      if (res?.error) throw new Error(res.message ?? "Error al actualizar");
      invalidate();
    } catch (err) {
      toast.error(err.message ?? "Error al actualizar el activo");
      throw err;
    }
  };

  const handleEliminar = async (asset) => {
    if (!asset?.id) return;
    try {
      const res = await activosApi.toggleStatus(asset.id);
      if (res?.error) throw new Error(res.message ?? "Error al eliminar");
      invalidate();
    } catch (err) {
      toast.error(err.message ?? "Error al eliminar");
      invalidate();
      throw err;
    }
  };

  const handleAsignarResguardo = async (asset, formData) => {
    const adminId = getProfileFromToken()?.id;
    if (!adminId) {
      toast.error("No se pudo identificar tu usuario administrador.");
      return;
    }
    try {
      const res = await resguardosApi.save({
        idActivo: asset.id,
        idUsuarioEmpleado: formData.idEmpleado,
        idUsuarioAdmin: adminId,
        observacionesAsig: formData.observaciones || null,
      });
      if (res?.error)
        throw new Error(res.message ?? "Error al asignar resguardo");
      invalidate();
      toast.success("Resguardo asignado correctamente");
    } catch (err) {
      toast.error(err.message ?? "Error al asignar resguardo");
      throw err;
    }
  };

  const handleLiberarResguardo = async (resguardo, formData) => {
    try {
      const payload = {
        idActivo: resguardo.activo?.id ?? resguardo.idActivo,
        idUsuarioEmpleado: resguardo.usuarioEmpleado?.id ?? resguardo.idUsuarioEmpleado,
        idUsuarioAdmin: resguardo.usuarioAdmin?.id ?? resguardo.idUsuarioAdmin,
        estadoResguardo: "Devuelto",
        observacionesDev: formData.observaciones || null,
      };
      
      const res = await resguardosApi.update(resguardo.id, payload);
      if (res?.error)
        throw new Error(res.message ?? "Error al liberar resguardo");
      invalidate();
      toast.success("Resguardo liberado correctamente");
    } catch (err) {
      toast.error(err.message ?? "Error al liberar resguardo");
      throw err;
    }
  };

  return (
    <Activos
      activos={activos}
      loading={isLoading}
      fetching={isFetching}
      error={errorMessage}
      currentPage={currentPage}
      totalPages={totalPages ?? 1}
      totalElements={totalElements ?? 0}
      pageSize={PAGE_SIZE_USED}
      onPageChange={setCurrentPage}
      onNuevo={handleNuevo}
      onEditar={handleEditar}
      onEliminar={handleEliminar}
      onDetalles={handleAsignarResguardo}
      onLiberar={handleLiberarResguardo}
      onRefresh={invalidate}
    />
  );
}
