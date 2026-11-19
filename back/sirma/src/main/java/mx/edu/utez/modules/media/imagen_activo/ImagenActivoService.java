package mx.edu.utez.modules.media.imagen_activo;

import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.core.assets.Assets;
import mx.edu.utez.modules.core.assets.AssetsRepository;
import mx.edu.utez.modules.media.imagen.BaseImagenService;
import mx.edu.utez.util.S3Service;
import mx.edu.utez.util.StoragePaths;
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
 * Gestiona la interacción con la base de datos (ImagenActivoRepository) y el servicio de almacenamiento (S3Service).
 *
 * @author Ithera Team
 */
@Service
public class ImagenActivoService extends BaseImagenService<ImagenActivo, ImagenActivoRepository> {

    private final AssetsRepository assetsRepository;

    public ImagenActivoService(ImagenActivoRepository repository, AssetsRepository assetsRepository, S3Service s3Service) {
        super(repository, s3Service);
        this.assetsRepository = assetsRepository;
    }

    /**
     * Guarda una imagen asociada a un activo.
     * Sube el archivo a S3 y registra los metadatos en la base de datos.
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
            resultado = s3Service.upload(file, StoragePaths.activos(activoId));
        } catch (IOException e) {
            return new ApiResponse("Error al subir imagen a S3: " + e.getMessage(), true, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        ImagenActivo img = new ImagenActivo();
        img.setActivo(found.get());
        img.llenarDesdeStorage(resultado, file.getOriginalFilename());
        repository.save(img);

        return new ApiResponse("Imagen registrada correctamente", conUrlFirmada(img), HttpStatus.CREATED);
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
        lista.forEach(this::conUrlFirmada);
        return new ApiResponse("OK", lista, HttpStatus.OK);
    }
}
