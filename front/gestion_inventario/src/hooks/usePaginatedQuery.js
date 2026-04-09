import { useState } from "react";
import {
  useQuery,
  useQueryClient,
  keepPreviousData,
} from "@tanstack/react-query";
import { DEFAULT_STALE_TIME, DEFAULT_PAGE_SIZE, fetchWithErrorCheck } from "../utils/queryUtils";

/**
 * Hook reutilizable para consultas paginadas con React Query.
 * Usado en ActivosPage, Users, Catalogs, Requests, etc.
 *
 * @param {object} options
 * @param {string} options.queryKey - Clave base para la query (ej: "activos", "users")
 * @param {function} options.queryFn - Función que recibe (page, size) y retorna Promise
 * @param {string} [options.errorMessage] - Mensaje si la API retorna error
 * @param {number} [options.staleTime] - Tiempo en ms que los datos son frescos (default 5 min)
 * @param {number} [options.pageSize] - Tamaño de página
 * @param {number} [options.retry] - Intentos en caso de error
 * @returns {{
 *   data,
 *   isLoading,
 *   isFetching,
 *   error,
 *   invalidate,
 *   currentPage,
 *   setCurrentPage,
 *   pageSize,
 *   body,
 *   content,
 *   totalPages,
 *   totalElements,
 * }}
 */
export function usePaginatedQuery({
  queryKey,
  queryFn,
  errorMessage = "Error al cargar datos",
  staleTime = DEFAULT_STALE_TIME,
  pageSize = DEFAULT_PAGE_SIZE,
  retry = 1,
}) {
  const queryClient = useQueryClient();
  const [currentPage, setCurrentPage] = useState(0);

  const baseKey = Array.isArray(queryKey) ? queryKey : [queryKey];
  const fullQueryKey = [...baseKey, currentPage];

  const { data, isLoading, isFetching, isPlaceholderData, error } = useQuery({
    queryKey: fullQueryKey,
    queryFn: async () => {
      const res = await fetchWithErrorCheck(
        queryFn(currentPage, pageSize),
        errorMessage,
      );
      return res;
    },
    staleTime,
    placeholderData: keepPreviousData,
    retry,
  });

  const body = data?.data ?? data ?? {};
  const rawContent = body.content ?? body.data ?? body ?? [];
  const content = Array.isArray(rawContent) ? rawContent : [];
  const totalPages = body.totalPages ?? 1;
  const totalElements = body.totalElements ?? 0;

  const invalidate = () =>
    queryClient.invalidateQueries({ queryKey: baseKey });

  return {
    data,
    isLoading,
    isFetching,
    isPlaceholderData,
    error: error?.message ?? null,
    invalidate,
    currentPage,
    setCurrentPage,
    pageSize,
    body,
    content,
    totalPages,
    totalElements,
  };
}
