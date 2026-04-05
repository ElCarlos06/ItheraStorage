package mx.edu.utez.modules.tipo_fallas;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gestión de tipos de falla en SIRMA.
 * Proporciona endpoints para operaciones CRUD de tipos de falla.
 *
 * @author Ithera Team
 */
@RestController
@RequestMapping("/api/tipo-fallas")
@AllArgsConstructor
public class TipoFallaController {

    private final TipoFallaService tipoFallaService;

    /**
     * Obtiene una página de tipos de fallas registradas en el sistema.
     *
     * @param pageable Configuración de paginación proporcionada por la petición.
     * @return Una respuesta HTTP con la página resultante de los tipos de falla.
     */
    @GetMapping
    public ResponseEntity<ApiResponse> findAll(@PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
        ApiResponse response = tipoFallaService.findAll(pageable);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Obtiene un tipo de falla específico por su identificador.
     *
     * @param id Identificador único del tipo de falla.
     * @return Respuesta HTTP con el contenido de dicho tipo de falla.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id) {
        ApiResponse response = tipoFallaService.findById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Crea y registra un nuevo tipo de falla dentro de la base de datos.
     *
     * @param dto El objeto con los datos a registrar proveniente validado desde la petición HTTP.
     * @return Respuesta HTTP de la petición con el resultado de la operación de registro.
     */
    @PostMapping
    public ResponseEntity<ApiResponse> save(@Valid @RequestBody TipoFallaDTO dto) {
        ApiResponse response = tipoFallaService.save(dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Modifica los datos de un tipo de falla ya registrado.
     *
     * @param id Identificador del tipo de falla que será modificado.
     * @param dto Objeto con la información a modificar de dicho registro.
     * @return Respuesta HTTP indicando el éxito o los detalles del fallo de la modificación.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody TipoFallaDTO dto) {
        ApiResponse response = tipoFallaService.update(id, dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Elimina por ID un tipo de falla de la base de datos.
     *
     * @param id Identificador del tipo de falla a suprimir.
     * @return Respuesta HTTP que informa si se eliminó con éxito el tipo de falla.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteById(@PathVariable Long id) {
        ApiResponse response = tipoFallaService.deleteById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

}
