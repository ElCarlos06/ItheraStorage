package mx.edu.utez.modules.mantenimientos;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controlador REST para la gestión de mantenimientos.
 * Gestiona el ciclo de vida de los mantenimientos correctivos y preventivos.
 * @author Ithera Team
 */
@RestController
@RequestMapping("/api/mantenimientos")
@AllArgsConstructor
public class MantenimientoController {

    private final MantenimientoService mantenimientoService;

    /**
     * Recupera una lista paginada de todos los mantenimientos registrados.
     * @param pageable Configuración de paginación y ordenamiento.
     * @return ResponseEntity con la lista de mantenimientos.
     */
    @GetMapping
    public ResponseEntity<ApiResponse> findAll(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        ApiResponse response = mantenimientoService.findAll(pageable);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Busca un mantenimiento por su ID.
     * @param id Identificador del mantenimiento.
     * @return ResponseEntity con el mantenimiento encontrado o error si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id) {
        ApiResponse response = mantenimientoService.findById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Busca los mantenimientos asignados a un técnico específico.
     * @param tecnicoId Identificador del usuario técnico.
     * @return ResponseEntity con la lista de mantenimientos del técnico.
     */
    @GetMapping("/tecnico/{tecnicoId}")
    public ResponseEntity<ApiResponse> findByTecnico(@PathVariable Long tecnicoId) {
        ApiResponse response = mantenimientoService.findByTecnico(tecnicoId);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Busca los mantenimientos realizados a un activo específico.
     * @param activoId Identificador del activo.
     * @return ResponseEntity con el historial de mantenimientos del activo.
     */
    @GetMapping("/activo/{activoId}")
    public ResponseEntity<ApiResponse> findByActivo(@PathVariable Long activoId) {
        ApiResponse response = mantenimientoService.findByActivo(activoId);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Estadístiucas sobre los manteniemientos, tiempos promedio de atención por mes técnicos con más manetnimientos completados
     * @return <code>ApiResponse</code> con un resumen de las estadísticas de mantenimientos.
     */
    @GetMapping("/stats")
    public  ResponseEntity<ApiResponse> getStats() {
        ApiResponse response = mantenimientoService.getMantenimientosStats();
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Registra un nuevo mantenimiento (inicia proceso de atención).
     * @param dto Datos del mantenimiento a crear.
     * @return ResponseEntity con el mantenimiento creado.
     */
    @PostMapping
    public ResponseEntity<ApiResponse> save(@Valid @RequestBody MantenimientoDTO dto) {
        ApiResponse response = mantenimientoService.save(dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Actualiza el estado o información de un mantenimiento existente.
     * @param id Identificador del mantenimiento.
     * @param dto Nuevos datos del mantenimiento.
     * @return ResponseEntity con el mantenimiento actualizado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody MantenimientoDTO dto) {
        ApiResponse response = mantenimientoService.update(id, dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Elimina un registro de mantenimiento y sus evidencias.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        ApiResponse response = mantenimientoService.delete(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

}
