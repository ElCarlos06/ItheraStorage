package mx.edu.utez.modules.imagen_mantenimiento;

import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.imagen.BaseImagenService;
import mx.edu.utez.modules.mantenimientos.Mantenimiento;
import mx.edu.utez.modules.mantenimientos.MantenimientoRepository;
import mx.edu.utez.util.CloudinaryPaths;
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
public class ImagenMantenimientoService extends BaseImagenService<ImagenMantenimiento, ImagenMantenimientoRepository> {

    private final MantenimientoRepository mantenimientoRepository;

    public ImagenMantenimientoService(ImagenMantenimientoRepository repository, MantenimientoRepository mantenimientoRepository, CloudinaryService cloudinaryService) {
        super(repository, cloudinaryService);
        this.mantenimientoRepository = mantenimientoRepository;
    }

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
            Map<String, Object> resultado = cloudinaryService.upload(file, CloudinaryPaths.mantenimientos(mantenimientoId));

            ImagenMantenimiento img = new ImagenMantenimiento();
            img.setMantenimiento(found.get());
            img.llenarDesdeCloudinary(resultado, file.getOriginalFilename());
            repository.save(img);

            return new ApiResponse("Imagen subida correctamente", true, HttpStatus.CREATED);
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
        List<ImagenMantenimiento> lista = repository.findByMantenimientoId(mantenimientoId);
        return new ApiResponse("OK", lista, HttpStatus.OK);
    }
}
