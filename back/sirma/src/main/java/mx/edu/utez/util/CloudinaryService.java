package mx.edu.utez.util;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Servicio utilitario de integración con Cloudinary.
 * Se encarga exclusivamente de subir y eliminar archivos en la nube.
 * Es reutilizado por los servicios de cada módulo (activos, mantenimientos, reportes).
 *
 * @author Ithera Team
 */
@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    /**
     * Sube un archivo a Cloudinary.
     *
     * @param file   Archivo recibido desde el controlador (multipart).
     * @param folder Carpeta destino dentro de Cloudinary (ej: "sirma/activos").
     * @return Mapa con campos de Cloudinary: secure_url, public_id, etc.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> upload(MultipartFile file, String folder) throws IOException {
        return cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folder,
                        "resource_type", "image",
                        "overwrite", false
                )
        );
    }

    /**
     * Sube un archivo en formato byte array a Cloudinary.
     * Útil para archivos generados en memoria (ej. PDFs, QRs).
     *
     * @param fileBytes Bytes del archivo.
     * @param folder    Carpeta destino.
     * @return Mapa con respuesta de Cloudinary.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> upload(byte[] fileBytes, String folder) throws IOException {
        return cloudinary.uploader().upload(
                fileBytes,
                ObjectUtils.asMap(
                        "folder", folder,
                        "asset_folder", folder,
                        "resource_type", "image",
                        "overwrite", false
                )
        );
    }

    /**
     * Elimina un archivo de Cloudinary usando su publicId.
     *
     * @param publicId Public ID del recurso en Cloudinary.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> delete(String publicId) throws IOException {
        return cloudinary.uploader().destroy(
                publicId,
                ObjectUtils.asMap("resource_type", "image")
        );
    }
}
