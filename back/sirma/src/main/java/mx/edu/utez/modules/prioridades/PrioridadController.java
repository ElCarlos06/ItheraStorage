package mx.edu.utez.modules.prioridades;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gestión de prioridades en SIRMA.
 * Proporciona endpoints para operaciones CRUD de prioridades.
 *
 * @author Ithera Team
 */
@RestController
@RequestMapping("/api/prioridades")
@AllArgsConstructor
public class PrioridadController {

    private final PrioridadService prioridadService;

    /**
     * Devuelve toda la colección en bloque sin formato paginador paramétrico.
     * 
     * @return El grupo completo listado.
     */
    @GetMapping
    public ResponseEntity<ApiResponse> findAll() {
        ApiResponse response = prioridadService.findAll();
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Llama puntualmente a revisar una entidad en específico.
     * 
     * @param id Referencia en url.
     * @return El JSON del nivel consultado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id) {
        ApiResponse response = prioridadService.findById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Consolida y almacena una prioridad si cruza las barreras preventivas de seguridad interna.
     * 
     * @param dto Esqueleto con la estipulación requerida.
     * @return Notificación envuelta confirmando el alta.
     */
    @PostMapping
    public ResponseEntity<ApiResponse> save(@Valid @RequestBody PrioridadDTO dto) {
        ApiResponse response = prioridadService.save(dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Aplica nuevas propiedades a las variables descriptivas de la meta configuración.
     * 
     * @param id Identificador anclado.
     * @param dto Contenido para nutrir la sobreescritura.
     * @return La variable mutada en señalación resolutoria.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody PrioridadDTO dto) {
        ApiResponse response = prioridadService.update(id, dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Efectúa una baja o remoción pura y directa de base de datos de esta instancia de configuración.
     * 
     * @param id Índice extirpable.
     * @return El estatus transaccional resolviendo afirmativamente.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteById(@PathVariable Long id) {
        ApiResponse response = prioridadService.deleteById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

}
