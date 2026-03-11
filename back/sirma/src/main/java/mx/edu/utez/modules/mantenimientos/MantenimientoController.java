package mx.edu.utez.modules.mantenimientos;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/mantenimientos")
@AllArgsConstructor
public class MantenimientoController {

    private final MantenimientoService mantenimientoService;

    @GetMapping
    public ResponseEntity<ApiResponse> findAll() {
        ApiResponse response = mantenimientoService.findAll();
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

    // ────────── IMÁGENES ──────────

    @PostMapping("/{id}/imagenes")
    public ResponseEntity<ApiResponse> subirImagen(@PathVariable Long id,
                                                   @RequestParam("file") MultipartFile file) {
        ApiResponse response = mantenimientoService.subirImagen(id, file);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/{id}/imagenes")
    public ResponseEntity<ApiResponse> listarImagenes(@PathVariable Long id) {
        ApiResponse response = mantenimientoService.listarImagenes(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/imagenes/{imagenId}")
    public ResponseEntity<ApiResponse> eliminarImagen(@PathVariable Long imagenId) {
        ApiResponse response = mantenimientoService.eliminarImagen(imagenId);
        return new ResponseEntity<>(response, response.getStatus());
    }

}

