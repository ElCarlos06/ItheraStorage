package mx.edu.utez.modules.imagen_mantenimiento;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.mantenimientos.Mantenimiento;
import mx.edu.utez.modules.mantenimientos.MantenimientoRepository;
import mx.edu.utez.util.CloudinaryService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio de negocio para gestionar imágenes de Mantenimientos.
 * Coordina la persistencia de datos y el almacenamiento de archivos en la nube.
 *
 * @author Ithera Team
 */
@Service
@AllArgsConstructor
public class ImagenMantenimientoService {

    private static final String CARPETA_CLOUDINARY = "sirma/mantenimientos";

    private final ImagenMantenimientoRepository imagenMantenimientoRepository;
    private final MantenimientoRepository mantenimientoRepository;
    private final CloudinaryService cloudinaryService;

    /**
     * Sube una imagen relacionada con un mantenimiento.
     *
     * @param mantenimientoId ID del mantenimiento.
     * @param file            Archivo de imagen a subir.
     * @return ApiResponse con la imagen persistida si tuvo éxito.
     */
    @Transactional
    public ApiResponse subirImagen(Long mantenimientoId, MultipartFile file) {
        Optional<Mantenimiento> found = mantenimientoRepository.findById(mantenimientoId);
        if (found.isEmpty())
            return new ApiResponse("Mantenimiento no encontrado", true, HttpStatus.NOT_FOUND);
        try {
            Map<String, Object> resultado = cloudinaryService.upload(file, CARPETA_CLOUDINARY);

            ImagenMantenimiento img = new ImagenMantenimiento();
            img.setMantenimiento(found.get());
            img.setUrlCloudinary((String) resultado.get("secure_url"));
            img.setPublicIdCloudinary((String) resultado.get("public_id"));
            img.setNombreArchivo(file.getOriginalFilename());
            imagenMantenimientoRepository.save(img);

            return new ApiResponse("Imagen subida correctamente", img, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ApiResponse("Error al subir imagen: " + e.getMessage(), true, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Lista las imágenes de un mantenimiento específico.
     *
     * @param mantenimientoId ID del mantenimiento.
     * @return ApiResponse conteniendo la lista de imágenes.
     */
    @Transactional(readOnly = true)
    public ApiResponse listarImagenes(Long mantenimientoId) {
        List<ImagenMantenimiento> lista = imagenMantenimientoRepository.findByMantenimientoId(mantenimientoId);
        return new ApiResponse("OK", lista, HttpStatus.OK);
    }

    /**
     * Elimina física y lógicamente una imagen de mantenimiento.
     *
     * @param imagenId ID de la imagen a borrar.
     * @return ApiResponse con el estatus de la operación.
     */
    @Transactional
    public ApiResponse eliminarImagen(Long imagenId) {
        Optional<ImagenMantenimiento> found = imagenMantenimientoRepository.findById(imagenId);
        if (found.isEmpty())
            return new ApiResponse("Imagen no encontrada", true, HttpStatus.NOT_FOUND);
        try {
            cloudinaryService.delete(found.get().getPublicIdCloudinary());
            imagenMantenimientoRepository.delete(found.get());
            return new ApiResponse("Imagen eliminada correctamente", HttpStatus.OK);
        } catch (IOException e) {
            return new ApiResponse("Error al eliminar imagen: " + e.getMessage(), true, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
