package mx.edu.utez.modules.imagen_reporte;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.reportes.Reporte;
import mx.edu.utez.modules.reportes.ReporteRepository;
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
 * Servicio para la gestión de imágenes de Reportes (evidencias).
 * Maneja el flujo de subida a Cloudinary y guardado en base de datos.
 *
 * @author Ithera Team
 */
@Service
@AllArgsConstructor
public class ImagenReporteService {

    private static final String CARPETA_CLOUDINARY = "sirma/reportes";

    private final ImagenReporteRepository imagenReporteRepository;
    private final ReporteRepository reporteRepository;
    private final CloudinaryService cloudinaryService;

    /**
     * Sube y registra una imagen como evidencia de un reporte.
     *
     * @param reporteId ID del reporte al que se adjunta la imagen.
     * @param file      Archivo de imagen.
     * @return ApiResponse con la confirmación de la carga.
     */
    @Transactional
    public ApiResponse subirImagen(Long reporteId, MultipartFile file) {
        Optional<Reporte> found = reporteRepository.findById(reporteId);
        if (found.isEmpty())
            return new ApiResponse("Reporte no encontrado", true, HttpStatus.NOT_FOUND);
        try {
            Map<String, Object> resultado = cloudinaryService.upload(file, CARPETA_CLOUDINARY);

            ImagenReporte img = new ImagenReporte();
            img.setReporte(found.get());
            img.setUrlCloudinary((String) resultado.get("secure_url"));
            img.setPublicIdCloudinary((String) resultado.get("public_id"));
            img.setNombreArchivo(file.getOriginalFilename());
            imagenReporteRepository.save(img);

            return new ApiResponse("Imagen subida correctamente", img, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ApiResponse("Error al subir imagen: " + e.getMessage(), true, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene el listado de imágenes asociadas a un reporte.
     *
     * @param reporteId ID del reporte.
     * @return ApiResponse con la lista de imágenes.
     */
    @Transactional(readOnly = true)
    public ApiResponse listarImagenes(Long reporteId) {
        List<ImagenReporte> lista = imagenReporteRepository.findByReporteId(reporteId);
        return new ApiResponse("OK", lista, HttpStatus.OK);
    }

    /**
     * Borra una imagen de evidencia de reporte, tanto de la nube como de la BD.
     *
     * @param imagenId ID de la imagen a eliminar.
     * @return ApiResponse confirmando la eliminación.
     */
    @Transactional
    public ApiResponse eliminarImagen(Long imagenId) {
        Optional<ImagenReporte> found = imagenReporteRepository.findById(imagenId);
        if (found.isEmpty())
            return new ApiResponse("Imagen no encontrada", true, HttpStatus.NOT_FOUND);
        try {
            cloudinaryService.delete(found.get().getPublicIdCloudinary());
            imagenReporteRepository.delete(found.get());
            return new ApiResponse("Imagen eliminada correctamente", HttpStatus.OK);
        } catch (IOException e) {
            return new ApiResponse("Error al eliminar imagen: " + e.getMessage(), true, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
