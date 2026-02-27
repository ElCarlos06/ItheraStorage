package mx.edu.utez.controllers;

import lombok.RequiredArgsConstructor;
import mx.edu.utez.dtos.CreatePrioridadDTO;
import mx.edu.utez.services.PrioridadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static mx.edu.utez.util.HandleStatus.getStatus;

/**
 * Controlador REST para gestionar las operaciones relacionadas con las prioridades.
 * Proporciona endpoints para obtener, crear, actualizar y eliminar prioridades.
 * Utiliza PrioridadService para la lógica de negocio y devuelve respuestas con el estado HTTP adecuado.
 * Permite solicitudes CORS desde cualquier origen.
 * @author Ithera Team
 */
@RestController
@RequestMapping("/api/prioridad")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PrioridadController {

    private final PrioridadService prioridadService;

    /**
     * Obtiene todas las prioridades mediante una petición GET
     * Mediante un mapa se devuelve la lista de prioridades por la inyección del servicio
     * @return ResponseEntity con la lista de prioridades y el estado HTTP
     */
    @GetMapping("")
    public ResponseEntity<Object> getAll() {
        Map<String, Object> response = prioridadService.getAll();

        int code = (int) response.get("success");

        return new ResponseEntity<>(response, getStatus(code));
    }

    /**
     * Obtiene una prioridad por su ID mediante una petición GET
     * @param id ID de la prioridad a buscar
     * @return ResponseEntity con la prioridad encontrada y el estado HTTP
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Integer id) {
        Map<String, Object> response = prioridadService.getById(id);

        int code = (int) response.get("success");

        return new ResponseEntity<>(response, getStatus(code));
    }

    /**
     * Crea una nueva prioridad mediante una petición POST
     * @param createPrioridadDTO DTO con los datos de la nueva prioridad
     * @return ResponseEntity con el resultado de la creación y el estado HTTP
     */
    @PostMapping("")
    public ResponseEntity<Object> create(@RequestBody CreatePrioridadDTO createPrioridadDTO) {
        Map<String, Object> response = prioridadService.create(createPrioridadDTO);

        int code = (int) response.get("success");

        return new ResponseEntity<>(response, getStatus(code));
    }

    /**
     * Actualiza una prioridad existente mediante una petición PUT
     * @param id ID de la prioridad a actualizar
     * @param createPrioridadDTO DTO con los datos actualizados de la prioridad
     * @return ResponseEntity con el resultado de la actualización y el estado HTTP
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Integer id, @RequestBody CreatePrioridadDTO createPrioridadDTO) {
        Map<String, Object> response = prioridadService.update(id, createPrioridadDTO);

        int code = (int) response.get("success");

        return new ResponseEntity<>(response, getStatus(code));
    }

    /**
     * Elimina una prioridad mediante una petición DELETE
     * @param id ID de la prioridad a eliminar
     * @return ResponseEntity con el resultado de la eliminación y el estado HTTP
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {
        Map<String, Object> response = prioridadService.delete(id);

        int code = (int) response.get("success");

        return new ResponseEntity<>(response, getStatus(code));
    }
}
