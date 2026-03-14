package mx.edu.utez.modules.imagen_activo;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controlador REST para gestionar las imágenes asociadas a Activos.
 * Proporciona endpoints para listar, subir y eliminar imágenes.
 *
 * @author Ithera Team
 */
@RequestMapping("/api/imagen-activo")
@RestController
@AllArgsConstructor
public class ImagenActivoController {

    private final ImagenActivoService imagenActivoService;

    /**
     * Obtiene una lista de imágenes asociadas a un activo específico.
     *
     * @param id Identificador del activo.
     * @return ResponseEntity con la lista de imágenes y el estado HTTP correspondiente.
     */
    @GetMapping("/activo/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id) {
        ApiResponse response = imagenActivoService.findAll(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Sube una nueva imagen y la asocia a un activo.
     *
     * @param id   Identificador del activo.
     * @param file Archivo de imagen a subir.
     * @return ResponseEntity con los detalles de la imagen creada y el estado HTTP.
     */
    @PostMapping("/activo/{id}")
    public ResponseEntity<ApiResponse> save(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        ApiResponse response = imagenActivoService.save(id, file);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Elimina una imagen existente por su identificador.
     *
     * @param id Identificador de la imagen a eliminar.
     * @return ResponseEntity con el resultado de la operación y el estado HTTP.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        ApiResponse response = imagenActivoService.delete(id);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
