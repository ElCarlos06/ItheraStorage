package mx.edu.utez.modules.location.areas;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gestión de áreas institucionales en SIRMA.
 * Proporciona endpoints para operaciones CRUD de áreas.
 *
 * @author Ithera Team
 */
@RestController
@RequestMapping("/api/areas")
@AllArgsConstructor
public class AreaController {

    private final AreaService areaService;

    /**
     * Endpoint para obtener un listado paginado de todas las áreas disponibles.
     *
     * @param pageable Parámetros de paginación y ordenamiento por defecto enviados por la petición GET.
     * @return <code>ResponseEntity</code> conteniendo el <code>ApiResponse</code> devuelto por el servicio.
     */
    @GetMapping
    public ResponseEntity<ApiResponse> findAll(@PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
        ApiResponse response = areaService.findAll(pageable);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Endpoint para consultar los detalles de un área usando su identificador.
     *
     * @param id Identificador del área a buscar proveniente de la ruta.
     * @return <code>ResponseEntity</code> conteniendo el <code>ApiResponse</code> con el objeto encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id) {
        ApiResponse response = areaService.findById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Endpoint para agregar o crear un área desde el cliente.
     *
     * @param dto El objeto JSON con los datos del área a guardar (nombre y descripción).
     * @return <code>ResponseEntity</code> conteniendo el <code>ApiResponse</code> y el estatus HTTP correspondiente (creado, conflicto, etc.).
     */
    @PostMapping
    public ResponseEntity<ApiResponse> save(@Valid @RequestBody AreaDTO dto) {
        ApiResponse response = areaService.save(dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Endpoint para actualizar los datos de un área según su identificador.
     *
     * @param id  El identificador del área indicado en la ruta.
     * @param dto El objeto JSON con los datos validados actualizados del área.
     * @return <code>ResponseEntity</code> conteniendo el <code>ApiResponse</code> con el detalle de la operación.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody AreaDTO dto) {
        ApiResponse response = areaService.update(id, dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Endpoint para solicitar la eliminación de un área registrada.
     *
     * @param id El identificador del área a ser eliminada extraído de la ruta de acceso.
     * @return <code>ResponseEntity</code> con el mensaje de éxito de la operación.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteById(@PathVariable Long id) {
        ApiResponse response = areaService.deleteById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

}
