package mx.edu.utez.modules.location.campus;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gestión de campus en SIRMA.
 * Proporciona endpoints para operaciones CRUD de campus.
 *
 * @author Ithera Team
 */
@RestController
@RequestMapping("/api/campus")
@AllArgsConstructor
public class CampusController {

    private final CampusService campusService;

    /**
     * Endpoint GET que facilita todos los entes tipo campus paginados.
     *
     * @param pageable Formato de orden y página. Sort por default DESC sobre id.
     * @return Transforma las localizaciones de la tabla a Entity Response Http.
     */
    @GetMapping
    public ResponseEntity<ApiResponse> findAll(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        ApiResponse response = campusService.findAll(pageable);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Extrae al cliente el objeto individual según se haya provisto por ruta paramétrica.
     *
     * @param id Entero largo referenciando la clave única.
     * @return El modelo DTO enriquecido del campus o aviso 404.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id) {
        ApiResponse response = campusService.findById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Guarda el enviado mediante request body y valida en las constraints.
     *
     * @param dto Molde transaccional de un campus.
     * @return Alerta de creado 201 en éxitos.
     */
    @PostMapping
    public ResponseEntity<ApiResponse> save(@Valid @RequestBody CampusDTO dto) {
        ApiResponse response = campusService.save(dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Interfaz PUT total para actualizar registros que sean ubicables e inconflictivos.
     *
     * @param id Clave del campus a alterar proveída en la ruta.
     * @param dto Información validada.
     * @return Actualización envuelta en ResponseEntity.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody CampusDTO dto) {
        ApiResponse response = campusService.update(id, dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Cambia la palanca boleana de "activo o inactivo" internamente afectando la topología lógica.
     *
     * @param id Identificador extraído de variable en ruta estática.
     * @return <code>ResponseEntity</code> exponiendo el estado con la resolución.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse> toggleStatus(@PathVariable Long id) {
        ApiResponse response = campusService.toggleStatus(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Elimina rúdamente un modelo de la DB por id. (Manejar con cuidado).
     *
     * @param id Identificador particular extraído url.
     * @return <code>ApiResponse</code> estandarizado sin contendio sobre acierto.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteById(@PathVariable Long id) {
        ApiResponse response = campusService.deleteById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

}
