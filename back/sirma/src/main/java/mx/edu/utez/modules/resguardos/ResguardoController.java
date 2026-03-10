package mx.edu.utez.modules.resguardos;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resguardos")
@AllArgsConstructor
public class ResguardoController {

    private final ResguardoService resguardoService;

    @GetMapping
    public ResponseEntity<ApiResponse> findAll() {
        ApiResponse response = resguardoService.findAll();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id) {
        ApiResponse response = resguardoService.findById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/activo/{activoId}")
    public ResponseEntity<ApiResponse> findByActivo(@PathVariable Long activoId) {
        ApiResponse response = resguardoService.findByActivo(activoId);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/empleado/{userId}")
    public ResponseEntity<ApiResponse> findByEmpleado(@PathVariable Long userId) {
        ApiResponse response = resguardoService.findByEmpleado(userId);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping
    public ResponseEntity<ApiResponse> save(@Valid @RequestBody ResguardoDTO dto) {
        ApiResponse response = resguardoService.save(dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody ResguardoDTO dto) {
        ApiResponse response = resguardoService.update(id, dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

}

