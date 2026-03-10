package mx.edu.utez.modules.areas;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
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

    @GetMapping
    public ResponseEntity<ApiResponse> findAll() {
        ApiResponse response = areaService.findAll();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id) {
        ApiResponse response = areaService.findById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping
    public ResponseEntity<ApiResponse> save(@Valid @RequestBody AreaDTO dto) {
        ApiResponse response = areaService.save(dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody AreaDTO dto) {
        ApiResponse response = areaService.update(id, dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteById(@PathVariable Long id) {
        ApiResponse response = areaService.deleteById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

}
