import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { imagenPerfilApi } from "../api/imagenPerfilApi";
import { DEFAULT_STALE_TIME } from "../utils/queryUtils";

export function useProfileImage(correo) {
  return useQuery({
    queryKey: ["profileImage", correo],
    queryFn: async () => {
      if (!correo) return null;
      try {
        const res = await imagenPerfilApi.getByCorreo(correo);
        if (res?.error) return null;
        return res.data?.urlCloudinary || null;
      } catch (error) {
        return null;
      }
    },
    enabled: !!correo,
    staleTime: DEFAULT_STALE_TIME,
  });
}

export function useUploadProfileImage() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async ({ correo, file }) => {
      const res = await imagenPerfilApi.upload(correo, file);
      if (res?.error) throw new Error(res.message ?? "Error al subir la foto");
      return res;
    },
    onSuccess: (_, { correo }) => {
      // Después del callback onSuccess de mutate (toast): evita refetch pesado mientras el toast monta.
      queueMicrotask(() => {
        queryClient.invalidateQueries({ queryKey: ["profileImage", correo] });
      });
    },
  });
}
