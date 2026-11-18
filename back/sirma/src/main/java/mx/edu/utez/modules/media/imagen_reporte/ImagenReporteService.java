package mx.edu.utez.modules.media.imagen_reporte;

import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.media.imagen.BaseImagenService;
import mx.edu.utez.modules.reporting.reportes.Reporte;
import mx.edu.utez.modules.reporting.reportes.ReporteRepository;
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
 * Servicio para la gestión de imágenes de Reportes (evidencias).
 * Maneja el flujo de subida a AWS S3 y guardado en base de datos.
 *
 * @author Ithera Team
 */
@Service
public class ImagenReporteService extends BaseImagenService<ImagenReporte, ImagenReporteRepository> {

    private final ReporteRepository reporteRepository;

    public ImagenReporteService(ImagenReporteRepository repository, ReporteRepository reporteRepository, S3Service s3Service) {
        super(repository, s3Service);
        this.reporteRepository = reporteRepository;
    }

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
            String folder = StoragePaths.reportes(reporteId);
            Map<String, Object> resultado = s3Service.upload(file, folder);

            ImagenReporte img = new ImagenReporte();
            img.setReporte(found.get());
            img.llenarDesdeStorage(resultado, file.getOriginalFilename());
            repository.save(img);

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
        List<ImagenReporte> lista = repository.findByReporteId(reporteId);
        return new ApiResponse("OK", lista, HttpStatus.OK);
    }
}
