package mx.edu.utez.modules.edificios;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/edificios")
@AllArgsConstructor
public class EdificioController {

    private final EdificioService edificioService;

    @GetMapping
    public ResponseEntity<ApiResponse> findAll() {
        ApiResponse response = edificioService.findAll();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id) {
        ApiResponse response = edificioService.findById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/campus/{campusId}")
    public ResponseEntity<ApiResponse> findByCampus(@PathVariable Long campusId) {
        ApiResponse response = edificioService.findByCampus(campusId);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping
    public ResponseEntity<ApiResponse> save(@Valid @RequestBody EdificioDTO dto) {
        ApiResponse response = edificioService.save(dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody EdificioDTO dto) {
        ApiResponse response = edificioService.update(id, dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse> toggleStatus(@PathVariable Long id) {
        ApiResponse response = edificioService.toggleStatus(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteById(@PathVariable Long id) {
        ApiResponse response = edificioService.deleteById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

}

