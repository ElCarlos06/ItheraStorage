package mx.edu.utez.modules.imagen;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.util.CloudinaryService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

/**
 * Servicio base abstracto para la gestión de imágenes.
 * Proporciona funcionalidad común CRUD y manejo de Cloudinary.
 *
 * @param <T> Tipo de la entidad de imagen (debe extender BaseImagen).
 * @param <R> Tipo del repositorio de la imagen.
 */
@AllArgsConstructor
public abstract class BaseImagenService<T extends BaseImagen, R extends JpaRepository<T, Long>> {

    protected final R repository;
    protected final CloudinaryService cloudinaryService;

    /**
     * Elimina una imagen por su ID, borrándola de la BD y de Cloudinary.
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
            cloudinaryService.delete(found.get().getPublicIdCloudinary());
            repository.delete(found.get());
            return new ApiResponse("Imagen eliminada correctamente", HttpStatus.OK);
        } catch (IOException e) {
            return new ApiResponse("Error al eliminar imagen: " + e.getMessage(), true, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
