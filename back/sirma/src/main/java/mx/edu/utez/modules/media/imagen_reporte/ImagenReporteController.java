package mx.edu.utez.modules.media.imagen_reporte;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controlador REST para gestionar evidencias fotográficas de Reportes.
 * Facilita la consulta, carga y eliminación de imágenes asociadas a reportes de incidencias.
 *
 * @author Ithera Team
 */
@RestController
@RequestMapping("/api/imagen-reporte")
@AllArgsConstructor
public class ImagenReporteController {

    private final ImagenReporteService imagenReporteService;

    /**
     * Lista todas las imágenes adjuntas a un reporte específico.
     *
     * @param id Identificador del reporte.
     * @return ResponseEntity conteniendo la lista de imágenes.
     */
    @GetMapping("/reporte/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id) {
        ApiResponse response = imagenReporteService.listarImagenes(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Adjunta una imagen a un reporte.
     *
     * @param id   Identificador del reporte al cual se agrega la imagen.
     * @param file Archivo de imagen a subir como evidencia.
     * @return ResponseEntity con la confirmación y datos de la imagen creada.
     */
    @PostMapping("/reporte/{id}")
    public ResponseEntity<ApiResponse> save(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        ApiResponse response = imagenReporteService.subirImagen(id, file);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Elimina una imagen de evidencia de un reporte.
     *
     * @param id Identificador de la imagen a borrar.
     * @return ResponseEntity confirmando la acción.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        ApiResponse response = imagenReporteService.delete(id);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
