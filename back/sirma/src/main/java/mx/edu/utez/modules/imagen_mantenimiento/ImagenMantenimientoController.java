package mx.edu.utez.modules.imagen_mantenimiento;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controlador REST para gestionar las imágenes asociadas a Mantenimientos.
 * Permite listar las imágenes de un mantenimiento, subir nuevas pruebas y eliminar existentes.
 *
 * @author Ithera Team
 */
@RestController
@RequestMapping("/api/imagen-mantenimiento")
@AllArgsConstructor
public class ImagenMantenimientoController {

    private final ImagenMantenimientoService imagenMantenimientoService;

    /**
     * Recupera todas las imágenes vinculadas a un mantenimiento.
     *
     * @param id Identificador del mantenimiento.
     * @return Respuesta HTTP con la lista de imágenes encontradas.
     */
    @GetMapping("/mantenimiento/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id) {
        ApiResponse response = imagenMantenimientoService.listarImagenes(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Carga una imagen para un registro de mantenimiento.
     *
     * @param id   Identificador del mantenimiento.
     * @param file Archivo de imagen proporcionado en la petición multipart.
     * @return Respuesta HTTP indicando el éxito de la creación.
     */
    @PostMapping("/mantenimiento/{id}")
    public ResponseEntity<ApiResponse> save(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        ApiResponse response = imagenMantenimientoService.subirImagen(id, file);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Borra una imagen específica del sistema y del almacenamiento en la nube.
     *
     * @param id Identificador único de la imagen.
     * @return Respuesta HTTP confirmando la eliminación.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        ApiResponse response = imagenMantenimientoService.delete(id);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
