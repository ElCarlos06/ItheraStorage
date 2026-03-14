package mx.edu.utez.modules.mantenimientos;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/mantenimientos")
@AllArgsConstructor
public class MantenimientoController {

    private final MantenimientoService mantenimientoService;

    @GetMapping
    public ResponseEntity<ApiResponse> findAll(@PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
        ApiResponse response = mantenimientoService.findAll(pageable);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id) {
        ApiResponse response = mantenimientoService.findById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/tecnico/{tecnicoId}")
    public ResponseEntity<ApiResponse> findByTecnico(@PathVariable Long tecnicoId) {
        ApiResponse response = mantenimientoService.findByTecnico(tecnicoId);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/activo/{activoId}")
    public ResponseEntity<ApiResponse> findByActivo(@PathVariable Long activoId) {
        ApiResponse response = mantenimientoService.findByActivo(activoId);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping
    public ResponseEntity<ApiResponse> save(@Valid @RequestBody MantenimientoDTO dto) {
        ApiResponse response = mantenimientoService.save(dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody MantenimientoDTO dto) {
        ApiResponse response = mantenimientoService.update(id, dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

}
