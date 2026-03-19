package mx.edu.utez.modules.imports;

import lombok.RequiredArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controlador REST que se encarga de la importación masiva de datos provenientes de un excel.
 * Recibe un archivo a través de una solicitud POST, delega el procesamiento al servicio de importación y devuelve una respuesta con el resultado de la operación.
 * @author Ithera Team
 */
@RestController
@RequestMapping("/api/imports")
@RequiredArgsConstructor
public class ImportController {

    /** La fokin inyeccion de la dependecia del servicio XD */
    private final ImportService importService;

    @PostMapping("/")
    public ResponseEntity<ApiResponse> save(@RequestParam("file") MultipartFile file) {
        ApiResponse response = importService.save(file);
        return new ResponseEntity<>(response, response.getStatus());
    }

}
