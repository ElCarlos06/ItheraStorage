package mx.edu.utez.modules.reportes;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la gestión de reportes de incidencias.
 * Permite registrar, consultar y actualizar reportes sobre activos.
 * @author Ithera Team
 */
@RestController
@RequestMapping("/api/reportes")
@AllArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    /**
     * Recupera una lista paginada de todos los reportes.
     * @param pageable Configuración de paginación y ordenamiento.
     * @return ResponseEntity con la lista de reportes.
     */
    @GetMapping
    public ResponseEntity<ApiResponse> findAll(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        ApiResponse response = reporteService.findAll(pageable);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Busca un reporte por su ID.
     * @param id Identificador del reporte.
     * @return ResponseEntity con el reporte encontrado o error si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id) {
        ApiResponse response = reporteService.findById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Busca los reportes asociados a un activo específico.
     * @param activoId Identificador del activo.
     * @return ResponseEntity con la lista de reportes del activo.
     */
    @GetMapping("/activo/{activoId}")
    public ResponseEntity<ApiResponse> findByActivo(@PathVariable Long activoId) {
        ApiResponse response = reporteService.findByActivo(activoId);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Registra un nuevo reporte de incidencia.
     * @param dto Datos del reporte a crear.
     * @return ResponseEntity con el reporte creado.
     */
    @PostMapping
    public ResponseEntity<ApiResponse> save(@Valid @RequestBody ReporteDTO dto) {
        ApiResponse response = reporteService.save(dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Actualiza la información de un reporte existente.
     * @param id Identificador del reporte a actualizar.
     * @param dto Nuevos datos del reporte.
     * @return ResponseEntity con el reporte actualizado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody ReporteDTO dto) {
        ApiResponse response = reporteService.update(id, dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

}
