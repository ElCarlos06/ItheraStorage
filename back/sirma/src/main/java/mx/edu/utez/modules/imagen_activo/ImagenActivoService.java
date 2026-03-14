package mx.edu.utez.modules.imagen_activo;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.assets.Assets;
import mx.edu.utez.modules.assets.AssetsRepository;
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
 * Servicio encargado de la lógica de negocio para las imágenes de Activos.
 * Gestiona la interacción con la base de datos (ImagenActivoRepository) y el servicio de almacenamiento (CloudinaryService).
 *
 * @author Ithera Team
 */
@Service
@AllArgsConstructor
public class ImagenActivoService {

    private static final String CARPETA_CLOUDINARY = "sirma/activos";

    private final ImagenActivoRepository imagenActivoRepository;
    private final AssetsRepository assetsRepository;
    private final CloudinaryService cloudinaryService;

    /**
     * Guarda una imagen asociada a un activo.
     * Sube el archivo a Cloudinary y registra los metadatos en la base de datos.
     *
     * @param activoId ID del activo al que pertenece la imagen.
     * @param file     Archivo MultipartFile con la imagen.
     * @return ApiResponse con el resultado de la operación (éxito o error).
     */
    @Transactional
    public ApiResponse save(Long activoId, MultipartFile file) {
        Optional<Assets> found = assetsRepository.findById(activoId);
        if (found.isEmpty())
            return new ApiResponse("Activo no encontrado", true, HttpStatus.NOT_FOUND);
        try {
            Map<String, Object> resultado = cloudinaryService.upload(file, CARPETA_CLOUDINARY);

            ImagenActivo img = new ImagenActivo();
            img.setActivo(found.get());
            img.setUrlCloudinary((String) resultado.get("secure_url"));
            img.setPublicIdCloudinary((String) resultado.get("public_id"));
            img.setNombreArchivo(file.getOriginalFilename());
            imagenActivoRepository.save(img);

            return new ApiResponse("Imagen subida correctamente", img, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ApiResponse("Error al subir imagen: " + e.getMessage(), true, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Recupera todas las imágenes asociadas a un activo.
     *
     * @param activoId ID del activo a consultar.
     * @return ApiResponse con la lista de imágenes encontradas.
     */
    @Transactional(readOnly = true)
    public ApiResponse findAll(Long activoId) {
        List<ImagenActivo> lista = imagenActivoRepository.findByActivoId(activoId);
        return new ApiResponse("OK", lista, HttpStatus.OK);
    }

    /**
     * Elimina una imagen del sistema.
     * Borra el archivo de Cloudinary y el registro de la base de datos.
     *
     * @param imagenId ID de la imagen a eliminar.
     * @return ApiResponse indicando el éxito o fallo de la eliminación.
     */
    @Transactional
    public ApiResponse delete(Long imagenId) {
        Optional<ImagenActivo> found = imagenActivoRepository.findById(imagenId);
        if (found.isEmpty())
            return new ApiResponse("Imagen no encontrada", true, HttpStatus.NOT_FOUND);
        try {
            cloudinaryService.delete(found.get().getPublicIdCloudinary());
            imagenActivoRepository.delete(found.get());
            return new ApiResponse("Imagen eliminada correctamente", HttpStatus.OK);
        } catch (IOException e) {
            return new ApiResponse("Error al eliminar imagen: " + e.getMessage(), true, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
