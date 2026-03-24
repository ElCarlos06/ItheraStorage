import { useMemo } from "react";
import { useQuery } from "@tanstack/react-query";
import { ubicacionesApi } from "../../../../../api/ubicacionesApi";
import { tipoActivosApi } from "../../../../../api/tipoActivosApi";
import { usePaginatedQuery } from "../../../../../hooks/usePaginatedQuery";
import { fetchWithErrorCheck } from "../../../../../utils/queryUtils";
import { mapCampusItems, mapEdificioItems, mapAulaItems } from "../utils/catalogMappers";

const PAGE_SIZE = 10;

export function useCatalogData(mainTab, subTab) {
  const catalogQueryFn = async (page, size) => {
    if (mainTab === "tipos-activos") {
      return fetchWithErrorCheck(
        tipoActivosApi.getTipoActivos(page, size, "DESC"),
        "Error al cargar tipos de activos",
      );
    }
    if (mainTab === "ubicaciones") {
      if (subTab === "campus") {
        const [campusRes, edificiosRes, espaciosRes] = await Promise.all([
          ubicacionesApi.getCampus(page, size),
          ubicacionesApi.getEdificios(0, 500),
          ubicacionesApi.getEspacios(0, 500),
        ]);
        if (campusRes?.error) throw new Error(campusRes.message ?? "Error al cargar campus");
        const edificios = edificiosRes?.data?.content ?? edificiosRes?.data ?? [];
        const espacios = espaciosRes?.data?.content ?? espaciosRes?.data ?? [];
        const mapped = mapCampusItems(campusRes?.data?.content ?? [], edificios, espacios);
        return { data: { content: mapped, totalPages: campusRes?.data?.totalPages ?? 0, totalElements: campusRes?.data?.totalElements ?? 0 } };
      }
      if (subTab === "edificios") {
        const [edificiosRes, espaciosRes] = await Promise.all([
          ubicacionesApi.getEdificios(page, size),
          ubicacionesApi.getEspacios(0, 500),
        ]);
        if (edificiosRes?.error) throw new Error(edificiosRes.message ?? "Error al cargar edificios");
        const espacios = espaciosRes?.data?.content ?? espaciosRes?.data ?? [];
        const mapped = mapEdificioItems(edificiosRes?.data?.content ?? [], espacios);
        return { data: { content: mapped, totalPages: edificiosRes?.data?.totalPages ?? 0, totalElements: edificiosRes?.data?.totalElements ?? 0 } };
      }
      if (subTab === "aulas") {
        const res = await ubicacionesApi.getEspacios(page, size);
        if (res?.error) throw new Error(res.message ?? "Error al cargar aulas");
        const mapped = mapAulaItems(res?.data?.content ?? []);
        return { data: { content: mapped, totalPages: res?.data?.totalPages ?? 0, totalElements: res?.data?.totalElements ?? 0 } };
      }
    }
    return { data: { content: [], totalPages: 0, totalElements: 0 } };
  };

  const paginated = usePaginatedQuery({
    queryKey: ["catalogs", mainTab, subTab],
    queryFn: catalogQueryFn,
    errorMessage: "Error al cargar catálogos",
    pageSize: PAGE_SIZE,
  });

  const { data: campusList = [] } = useQuery({
    queryKey: ["campus-list"],
    queryFn: async () => {
      const r = await ubicacionesApi.getCampus(0, 200);
      if (r?.error) throw new Error(r.message);
      return r?.data?.content ?? r?.data ?? [];
    },
    enabled: mainTab === "ubicaciones",
  });

  const { data: edificiosList = [] } = useQuery({
    queryKey: ["edificios-list"],
    queryFn: async () => {
      const r = await ubicacionesApi.getEdificios(0, 500);
      if (r?.error) throw new Error(r.message);
      return r?.data?.content ?? r?.data ?? [];
    },
    enabled: mainTab === "ubicaciones",
  });

  const items = useMemo(() => {
    const c = paginated.content;
    return Array.isArray(c) ? c : c?.content ?? c?.data ?? [];
  }, [paginated.content]);

  const tiposActivosItems = useMemo(() => {
    const activos = items.filter((t) => t.esActivo !== false);
    if (subTab === "muebles") return activos.filter((t) => t.tipoBien === "Mueble");
    if (subTab === "vehiculos") return activos.filter((t) => t.tipoBien === "Inmueble");
    return [];
  }, [items, subTab]);

  return {
    ...paginated,
    items,
    tiposActivosItems,
    campusList,
    edificiosList,
    hasLocations: items.length > 0,
    pageSize: PAGE_SIZE,
  };
}
