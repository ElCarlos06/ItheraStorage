package mx.edu.utez.modules.marcas;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * Controlador REST para gestión de marcas en SIRMA.
 * Proporciona endpoints para operaciones CRUD de marcas.
 *
 * @author Ithera Team
 */
@RestController
@RequestMapping("/api/marcas")
@AllArgsConstructor
public class MarcaController {

    private final MarcaService marcaService;

    @GetMapping
    public ResponseEntity<ApiResponse> findAll(@PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
        ApiResponse response = marcaService.findAll(pageable);
        return ResponseEntity.status(response.getStatus())
                .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic())
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id) {
        ApiResponse response = marcaService.findById(id);
        return ResponseEntity.status(response.getStatus())
                .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic())
                .body(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> save(@Valid @RequestBody MarcaDTO dto) {
        ApiResponse response = marcaService.save(dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody MarcaDTO dto) {
        ApiResponse response = marcaService.update(id, dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteById(@PathVariable Long id) {
        ApiResponse response = marcaService.deleteById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

}
