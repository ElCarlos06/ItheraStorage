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

    @GetMapping
    public ResponseEntity<ApiResponse> findAll(@PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
        ApiResponse response = tipoFallaService.findAll(pageable);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id) {
        ApiResponse response = tipoFallaService.findById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping
    public ResponseEntity<ApiResponse> save(@Valid @RequestBody TipoFallaDTO dto) {
        ApiResponse response = tipoFallaService.save(dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody TipoFallaDTO dto) {
        ApiResponse response = tipoFallaService.update(id, dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteById(@PathVariable Long id) {
        ApiResponse response = tipoFallaService.deleteById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

}
