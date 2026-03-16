package mx.edu.utez.modules.imagen_perfil;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controlador REST para gestionar la foto de perfil de los usuarios.
 * Permite consultar, actualizar (subir) y eliminar la foto de perfil.
 *
 * @author Ithera Team
 */
@RestController
@RequestMapping("/api/imagen-perfil")
@AllArgsConstructor
public class ImagenPerfilController {

    private final ImagenPerfilService imagenPerfilService;

    /**
     * Obtiene la(s) imagen(es) de perfil de un usuario.
     * Normalmente retorna la foto actual.
     *
     * @param id Identificador del usuario.
     * @return Respuesta HTTP con la información de la imagen de perfil.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findByCorreo(@PathVariable String id) {
        ApiResponse response = imagenPerfilService.obtenerImagen(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Sube una nueva foto de perfil para el usuario indicado.
     *
     * @param id   Identificador del usuario.
     * @param file Archivo de imagen de la nueva foto de perfil.
     * @return Respuesta HTTP con los datos de la nueva imagen guardada.
     */
    @PostMapping("/{id}")
    public ResponseEntity<ApiResponse> save(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        ApiResponse response = imagenPerfilService.subirImagen(id, file);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Elimina una foto de perfil específica.
     *
     * @param id Identificador de la imagen (no del usuario).
     * @return Respuesta HTTP con el estado de la eliminación.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        ApiResponse response = imagenPerfilService.delete(id);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
