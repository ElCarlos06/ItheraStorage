package mx.edu.utez.modules.core.tipo_activos;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * Controlador REST que gestiona las peticiones HTTP relacionadas con los tipos de activo.
 *
 * @author Ithera Team
 */
@RestController
@RequestMapping("/api/tipo-activos")
@AllArgsConstructor
public class TipoActivoController {

    private final TipoActivoService tipoActivoService;

    @GetMapping
    public ResponseEntity<ApiResponse> findAll(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        ApiResponse response = tipoActivoService.findAll(pageable);
        return ResponseEntity.status(response.getStatus())
                .cacheControl(CacheControl.noCache().mustRevalidate())
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id) {
        ApiResponse response = tipoActivoService.findById(id);
        return ResponseEntity.status(response.getStatus())
                .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic())
                .body(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> save(@Valid @RequestBody TipoActivoDTO dto) {
        ApiResponse response = tipoActivoService.save(dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody TipoActivoDTO dto) {
        ApiResponse response = tipoActivoService.update(id, dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse> toggleStatus(@PathVariable Long id) {
        ApiResponse response = tipoActivoService.toggleStatus(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

}
