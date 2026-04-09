package mx.edu.utez.modules.core.imports;

import lombok.RequiredArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

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

    /**
     * Recibe un archivo de Excel o similar al cual guardaremos los activos que tenga en el excel,
     * el servicio se encarga de procesar el archivo y devolver un resultado con el número de filas procesadas,
     * filas exitosas, filas con errores y mensajes de error.
     * @param file <code>MultipartFile</code> que es el archivo de excel
     * @return <code>ResponseEntity</code> con un <code>ApiResponse</code> que contiene el resultado de la importación.
     */
    @PostMapping("/")
    public ResponseEntity<ApiResponse> save(@RequestParam("file") MultipartFile file) {

        ImportResult result = importService.save(file);
        HttpStatus status = result.tieneErrores() ? HttpStatus.MULTI_STATUS : HttpStatus.CREATED;
        // return new ResponseEntity<>(new ApiResponse(result.mensaje(), result.tieneErrores(), status), status);
        return ResponseEntity.status(status).body(new ApiResponse(result.mensaje(), result.tieneErrores(), status));
    }

}
