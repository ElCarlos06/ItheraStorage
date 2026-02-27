package mx.edu.utez.controllers;

import lombok.RequiredArgsConstructor;
import mx.edu.utez.dtos.CreateMantenimientoDTO;
import mx.edu.utez.services.MantenimientoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static mx.edu.utez.util.HandleStatus.getStatus;

/**
 * Controlador REST para gestionar las operaciones relacionadas con los mantenimientos.
 * Proporciona endpoints para obtener, crear, actualizar y eliminar mantenimientos.
 * Utiliza MantenimientoService para la lógica de negocio y devuelve respuestas con el estado HTTP adecuado.
 * Permite solicitudes CORS desde cualquier origen.
 * @author Ithera Team
 */
@RestController
@RequestMapping("/api/mantenimiento")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MantenimientoController {

    private final MantenimientoService mantenimientoService;

    /**
     * Obtiene todos los mantenimientos mediante una petición GET
     * Mediante un mapa se devuelve la lista de mantenimientos por la inyección del servicio
     * @return ResponseEntity con la lista de mantenimientos y el estado HTTP
     */
    @GetMapping("")
    public ResponseEntity<Object> getAll() {
        Map<String, Object> response = mantenimientoService.getAll();

        int code = (int) response.get("success");

        return new ResponseEntity<>(response, getStatus(code));
    }

    /**
     * Obtiene un mantenimiento por su ID mediante una petición GET
     * @param id ID del mantenimiento a buscar
     * @return ResponseEntity con el mantenimiento encontrado y el estado HTTP
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Integer id) {
        Map<String, Object> response = mantenimientoService.getById(id);

        int code = (int) response.get("success");

        return new ResponseEntity<>(response, getStatus(code));
    }

    /**
     * Crea un nuevo mantenimiento mediante una petición POST
     * @param createMantenimientoDTO DTO con los datos del nuevo mantenimiento
     * @return ResponseEntity con el resultado de la creación y el estado HTTP
     */
    @PostMapping("")
    public ResponseEntity<Object> create(@RequestBody CreateMantenimientoDTO createMantenimientoDTO) {
        Map<String, Object> response = mantenimientoService.create(createMantenimientoDTO);

        int code = (int) response.get("success");

        return new ResponseEntity<>(response, getStatus(code));
    }

    /**
     * Actualiza un mantenimiento existente mediante una petición PUT
     * @param id ID del mantenimiento a actualizar
     * @param createMantenimientoDTO DTO con los datos actualizados del mantenimiento
     * @return ResponseEntity con el resultado de la actualización y el estado HTTP
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Integer id, @RequestBody CreateMantenimientoDTO createMantenimientoDTO) {
        Map<String, Object> response = mantenimientoService.update(id, createMantenimientoDTO);

        int code = (int) response.get("success");

        return new ResponseEntity<>(response, getStatus(code));
    }

    /**
     * Elimina un mantenimiento mediante una petición DELETE
     * @param id ID del mantenimiento a eliminar
     * @return ResponseEntity con el resultado de la eliminación y el estado HTTP
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {
        Map<String, Object> response = mantenimientoService.delete(id);

        int code = (int) response.get("success");

        return new ResponseEntity<>(response, getStatus(code));
    }
}
