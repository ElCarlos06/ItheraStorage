package mx.edu.utez.modules.modelos;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/modelos")
@AllArgsConstructor
public class ModeloController {

    private final ModeloService modeloService;

    @GetMapping
    public ResponseEntity<ApiResponse> findAll(@PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
        ApiResponse response = modeloService.findAll(pageable);
        return ResponseEntity.status(response.getStatus())
                .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic())
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id) {
        ApiResponse response = modeloService.findById(id);
        return ResponseEntity.status(response.getStatus())
                .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic())
                .body(response);
    }

    @GetMapping("/marca/{marcaId}")
    public ResponseEntity<ApiResponse> findByMarca(@PathVariable Long marcaId) {
        ApiResponse response = modeloService.findByMarca(marcaId);
        return ResponseEntity.status(response.getStatus())
                .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic())
                .body(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> save(@Valid @RequestBody ModeloDTO dto) {
        ApiResponse response = modeloService.save(dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody ModeloDTO dto) {
        ApiResponse response = modeloService.update(id, dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteById(@PathVariable Long id) {
        ApiResponse response = modeloService.deleteById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

}
