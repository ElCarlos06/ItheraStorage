package mx.edu.utez.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Servicio utilitario de integración con Amazon S3.
 * Se encarga de la subida, consulta y eliminación de archivos en AWS S3.
 *
 * @author Ithera Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.region:us-east-1}")
    private String region;

    /**
     * Sube un archivo recibido como MultipartFile a un bucket de S3.
     *
     * @param file   Archivo recibido en la petición.
     * @param folder Prefijo de carpeta destino dentro del bucket (ej: "sirma/activos").
     * @return Mapa con 'url', 'key', y equivalentes para compatibilidad ('secure_url', 'public_id').
     */
    public Map<String, Object> upload(MultipartFile file, String folder) throws IOException {
        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "archivo";
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalFilename.substring(dotIndex);
        }

        String key = sanitizeFolder(folder) + "/" + UUID.randomUUID() + extension;
        String contentType = file.getContentType() != null && !file.getContentType().isBlank()
                ? file.getContentType()
                : "application/octet-stream";

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        String publicUrl = buildPublicUrl(key);

        Map<String, Object> result = new HashMap<>();
        result.put("url", publicUrl);
        result.put("secure_url", publicUrl);
        result.put("key", key);
        result.put("public_id", key);
        result.put("nombreArchivo", originalFilename);

        log.info("Archivo subido a S3: {} (tamaño: {} bytes)", key, file.getSize());
        return result;
    }

    /**
     * Sube un arreglo de bytes a S3 (útil para QR, reportes generados o PDFs en memoria).
     *
     * @param fileBytes   Bytes del archivo a subir.
     * @param folder      Prefijo de destino dentro del bucket.
     * @param filename    Nombre asignado al archivo.
     * @param contentType Tipo de contenido MIME.
     * @return Mapa con campos de URL y llave del objeto.
     */
    public Map<String, Object> upload(byte[] fileBytes, String folder, String filename, String contentType) {
        String key = sanitizeFolder(folder) + "/" + filename;

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType != null ? contentType : "image/png")
                .build();

        s3Client.putObject(putRequest, RequestBody.fromBytes(fileBytes));

        String publicUrl = buildPublicUrl(key);

        Map<String, Object> result = new HashMap<>();
        result.put("url", publicUrl);
        result.put("secure_url", publicUrl);
        result.put("key", key);
        result.put("public_id", key);
        result.put("nombreArchivo", filename);

        log.info("Bytes subidos a S3: {} (tamaño: {} bytes)", key, fileBytes.length);
        return result;
    }

    /**
     * Sobrecarga de conveniencia para subir bytes con nombre autogenerado.
     */
    public Map<String, Object> upload(byte[] fileBytes, String folder) {
        return upload(fileBytes, folder, UUID.randomUUID() + ".png", "image/png");
    }

    /**
     * Elimina un objeto de S3 a partir de su clave/identificador.
     *
     * @param key Identificador del objeto en el bucket de S3.
     * @return Mapa con el estado de la operación.
     */
    public Map<String, Object> delete(String key) {
        if (key == null || key.isBlank()) {
            return Map.of("result", "skipped");
        }

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteRequest);
        log.info("Objeto eliminado de S3: {}", key);

        return Map.of("result", "ok");
    }

    private String sanitizeFolder(String folder) {
        if (folder == null || folder.isBlank()) {
            return "uploads";
        }
        String clean = folder.trim();
        while (clean.startsWith("/")) clean = clean.substring(1);
        while (clean.endsWith("/")) clean = clean.substring(0, clean.length() - 1);
        return clean;
    }

    private String buildPublicUrl(String key) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);
    }
}
