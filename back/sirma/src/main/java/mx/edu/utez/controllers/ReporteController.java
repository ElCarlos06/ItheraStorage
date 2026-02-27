package mx.edu.utez.controllers;

import lombok.RequiredArgsConstructor;
import mx.edu.utez.dtos.CreateReporteDTO;
import mx.edu.utez.services.ReporteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static mx.edu.utez.util.HandleStatus.getStatus;

/**
 * Controlador REST para gestionar las operaciones relacionadas con los reportes.
 * Proporciona endpoints para obtener, crear, actualizar y eliminar reportes.
 * Utiliza ReporteService para la lógica de negocio y devuelve respuestas con el estado HTTP adecuado.
 * Permite solicitudes CORS desde cualquier origen.
 * @author Ithera Team
 */
@RestController
@RequestMapping("/api/reporte")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    /**
     * Obtiene todos los reportes mediante una petición GET
     * Mediante un mapa se devuelve la lista de reportes por la inyección del servicio
     * @return ResponseEntity con la lista de reportes y el estado HTTP
     */
    @GetMapping("")
    public ResponseEntity<Object> getAll() {
        Map<String, Object> response = reporteService.getAll();

        int code = (int) response.get("success");

        return new ResponseEntity<>(response, getStatus(code));
    }

    /**
     * Obtiene un reporte por su ID mediante una petición GET
     * @param id ID del reporte a buscar
     * @return ResponseEntity con el reporte encontrado y el estado HTTP
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Integer id) {
        Map<String, Object> response = reporteService.getById(id);

        int code = (int) response.get("success");

        return new ResponseEntity<>(response, getStatus(code));
    }

    /**
     * Crea un nuevo reporte mediante una petición POST
     * @param createReporteDTO DTO con los datos del nuevo reporte
     * @return ResponseEntity con el resultado de la creación y el estado HTTP
     */
    @PostMapping("")
    public ResponseEntity<Object> create(@RequestBody CreateReporteDTO createReporteDTO) {
        Map<String, Object> response = reporteService.create(createReporteDTO);

        int code = (int) response.get("success");

        return new ResponseEntity<>(response, getStatus(code));
    }

    /**
     * Actualiza un reporte existente mediante una petición PUT
     * @param id ID del reporte a actualizar
     * @param createReporteDTO DTO con los datos actualizados del reporte
     * @return ResponseEntity con el resultado de la actualización y el estado HTTP
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Integer id, @RequestBody CreateReporteDTO createReporteDTO) {
        Map<String, Object> response = reporteService.update(id, createReporteDTO);

        int code = (int) response.get("success");

        return new ResponseEntity<>(response, getStatus(code));
    }

    /**
     * Elimina un reporte mediante una petición DELETE
     * @param id ID del reporte a eliminar
     * @return ResponseEntity con el resultado de la eliminación y el estado HTTP
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {
        Map<String, Object> response = reporteService.delete(id);

        int code = (int) response.get("success");

        return new ResponseEntity<>(response, getStatus(code));
    }
}
