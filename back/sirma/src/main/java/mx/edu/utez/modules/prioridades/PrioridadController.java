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

    @GetMapping
    public ResponseEntity<ApiResponse> findAll() {
        ApiResponse response = prioridadService.findAll();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id) {
        ApiResponse response = prioridadService.findById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping
    public ResponseEntity<ApiResponse> save(@Valid @RequestBody PrioridadDTO dto) {
        ApiResponse response = prioridadService.save(dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody PrioridadDTO dto) {
        ApiResponse response = prioridadService.update(id, dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteById(@PathVariable Long id) {
        ApiResponse response = prioridadService.deleteById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

}
