package mx.edu.utez.modules.solicitud_bajas;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/solicitudes-baja")
@AllArgsConstructor
public class SolicitudBajaController {

    private final SolicitudBajaService solicitudBajaService;

    @GetMapping
    public ResponseEntity<ApiResponse> findAll() {
        ApiResponse response = solicitudBajaService.findAll();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id) {
        ApiResponse response = solicitudBajaService.findById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<ApiResponse> findByEstado(@PathVariable String estado) {
        ApiResponse response = solicitudBajaService.findByEstado(estado);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping
    public ResponseEntity<ApiResponse> save(@Valid @RequestBody SolicitudBajaDTO dto) {
        ApiResponse response = solicitudBajaService.save(dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody SolicitudBajaDTO dto) {
        ApiResponse response = solicitudBajaService.update(id, dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

}

