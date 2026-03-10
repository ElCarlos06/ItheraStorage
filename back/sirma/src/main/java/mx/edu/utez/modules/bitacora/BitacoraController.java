package mx.edu.utez.modules.bitacora;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bitacora")
@AllArgsConstructor
public class BitacoraController {

    private final BitacoraService bitacoraService;

    @GetMapping
    public ResponseEntity<ApiResponse> findAll() {
        ApiResponse response = bitacoraService.findAll();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id) {
        ApiResponse response = bitacoraService.findById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/activo/{activoId}")
    public ResponseEntity<ApiResponse> findByActivo(@PathVariable Long activoId) {
        ApiResponse response = bitacoraService.findByActivo(activoId);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<ApiResponse> findByUsuario(@PathVariable Long usuarioId) {
        ApiResponse response = bitacoraService.findByUsuario(usuarioId);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping
    public ResponseEntity<ApiResponse> save(@Valid @RequestBody BitacoraDTO dto) {
        ApiResponse response = bitacoraService.save(dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

}

