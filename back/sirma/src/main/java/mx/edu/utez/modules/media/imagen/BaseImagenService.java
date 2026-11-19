package mx.edu.utez.modules.media.imagen;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.util.S3Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Servicio base abstracto para la gestión de imágenes.
 * Proporciona funcionalidad común CRUD y manejo de almacenamiento con AWS S3.
 *
 * @param <T> Tipo de la entidad de imagen (debe extender BaseImagen).
 * @param <R> Tipo del repositorio de la imagen.
 * @author Ithera Team
 */
@AllArgsConstructor
public abstract class BaseImagenService<T extends BaseImagen, R extends JpaRepository<T, Long>> {

    protected final R repository;
    protected final S3Service s3Service;

    /** Prepara la entidad para el front: URL firmada de S3 (bucket privado). */
    protected T conUrlFirmada(T imagen) {
        if (imagen != null && imagen.getStorageKey() != null) {
            imagen.aplicarUrlTemporal(s3Service.presignGetUrl(imagen.getStorageKey()));
        }
        return imagen;
    }

    /**
     * Elimina una imagen por su ID, borrándola de la BD y de AWS S3.
     *
     * @param id ID de la imagen a eliminar.
     * @return ApiResponse con el resultado.
     */
    @Transactional
    public ApiResponse delete(Long id) {
        Optional<T> found = repository.findById(id);
        if (found.isEmpty()) {
            return new ApiResponse("Imagen no encontrada", true, HttpStatus.NOT_FOUND);
        }
        try {
            s3Service.delete(found.get().getStorageKey());
            repository.delete(found.get());
            return new ApiResponse("Imagen eliminada correctamente", HttpStatus.OK);
        } catch (Exception e) {
            return new ApiResponse("Error al eliminar imagen: " + e.getMessage(), true, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
