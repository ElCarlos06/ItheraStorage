package mx.edu.utez.modules.reportes;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reportes")
@AllArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping
    public ResponseEntity<ApiResponse> findAll() {
        ApiResponse response = reporteService.findAll();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id) {
        ApiResponse response = reporteService.findById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/activo/{activoId}")
    public ResponseEntity<ApiResponse> findByActivo(@PathVariable Long activoId) {
        ApiResponse response = reporteService.findByActivo(activoId);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping
    public ResponseEntity<ApiResponse> save(@Valid @RequestBody ReporteDTO dto) {
        ApiResponse response = reporteService.save(dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody ReporteDTO dto) {
        ApiResponse response = reporteService.update(id, dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

}

