package mx.edu.utez.modules.imagen_activo;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.assets.Assets;
import mx.edu.utez.modules.assets.AssetsRepository;
import mx.edu.utez.modules.imagen.BaseImagenService;
import mx.edu.utez.util.CloudinaryPaths;
import mx.edu.utez.util.CloudinaryService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
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
public class ImagenActivoService extends BaseImagenService<ImagenActivo, ImagenActivoRepository> {

    private final AssetsRepository assetsRepository;

    public ImagenActivoService(ImagenActivoRepository repository, AssetsRepository assetsRepository, CloudinaryService cloudinaryService) {
        super(repository, cloudinaryService);
        this.assetsRepository = assetsRepository;
    }

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

        Map<String, Object> resultado;
        try {
            // Usamos la nueva constante centralizada
            resultado = cloudinaryService.upload(file, CloudinaryPaths.ACTIVOS);
        } catch (IOException e) {
            return new ApiResponse("Error al subir imagen a Cloudinary", true, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        ImagenActivo img = new ImagenActivo();
        img.setActivo(found.get());
        img.llenarDesdeCloudinary(resultado, file.getOriginalFilename());
        repository.save(img);

        return new ApiResponse("Imagen registrada correctamente", img, HttpStatus.CREATED);
    }

    /**
     * Recupera todas las imágenes asociadas a un activo.
     *
     * @param activoId ID del activo a consultar.
     * @return ApiResponse con la lista de imágenes encontradas.
     */
    @Transactional(readOnly = true)
    public ApiResponse findAll(Long activoId) {
        List<ImagenActivo> lista = repository.findByActivoId(activoId);
        return new ApiResponse("OK", lista, HttpStatus.OK);
    }
}
