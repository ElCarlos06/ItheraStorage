package mx.edu.utez.modules.campus;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
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

    @GetMapping
    public ResponseEntity<ApiResponse> findAll() {
        ApiResponse response = campusService.findAll();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id) {
        ApiResponse response = campusService.findById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping
    public ResponseEntity<ApiResponse> save(@Valid @RequestBody CampusDTO dto) {
        ApiResponse response = campusService.save(dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody CampusDTO dto) {
        ApiResponse response = campusService.update(id, dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse> toggleStatus(@PathVariable Long id) {
        ApiResponse response = campusService.toggleStatus(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

}
