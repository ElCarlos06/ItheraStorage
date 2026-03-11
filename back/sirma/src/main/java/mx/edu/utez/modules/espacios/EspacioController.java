package mx.edu.utez.modules.espacios;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/espacios")
@AllArgsConstructor
public class EspacioController {

    private final EspacioService espacioService;

    @GetMapping
    public ResponseEntity<ApiResponse> findAll() {
        ApiResponse response = espacioService.findAll();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id) {
        ApiResponse response = espacioService.findById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/edificio/{edificioId}")
    public ResponseEntity<ApiResponse> findByEdificio(@PathVariable Long edificioId) {
        ApiResponse response = espacioService.findByEdificio(edificioId);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping
    public ResponseEntity<ApiResponse> save(@Valid @RequestBody EspacioDTO dto) {
        ApiResponse response = espacioService.save(dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody EspacioDTO dto) {
        ApiResponse response = espacioService.update(id, dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse> toggleStatus(@PathVariable Long id) {
        ApiResponse response = espacioService.toggleStatus(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteById(@PathVariable Long id) {
        ApiResponse response = espacioService.deleteById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

}

