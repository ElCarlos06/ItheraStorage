package mx.edu.utez.controllers;

import lombok.RequiredArgsConstructor;
import mx.edu.utez.dtos.CreateUbicacionDTO;
import mx.edu.utez.services.UbicacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static mx.edu.utez.util.HandleStatus.getStatus;

/**
 * Controlador REST para gestionar las operaciones relacionadas con las ubicaciones.
 * Proporciona endpoints para obtener, crear, actualizar y eliminar ubicaciones.
 * Utiliza UbicacionService para la lógica de negocio y devuelve respuestas con el estado HTTP adecuado.
 * Permite solicitudes CORS desde cualquier origen.
 * @author Ithera Team
 */
@RestController
@RequestMapping("/api/ubicacion")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UbicacionController {

    private final UbicacionService ubicacionService;

    /**
     * Obtiene todas las ubicaciones mediante una petición GET
     * Mediante un mapa se devuelve la lista de ubicaciones por la inyección del servicio
     * @return ResponseEntity con la lista de ubicaciones y el estado HTTP
     */
    @GetMapping("")
    public ResponseEntity<Object> getAll() {
        Map<String, Object> response = ubicacionService.getAll();
        int code = (int) response.get("success");
        return new ResponseEntity<>(response, getStatus(code));
    }

    /**
     * Obtiene una ubicación por su ID mediante una petición GET
     * @param id ID de la ubicación a buscar
     * @return ResponseEntity con la ubicación encontrada y el estado HTTP
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Integer id) {
        Map<String, Object> response = ubicacionService.getById(id);
        int code = (int) response.get("success");
        return new ResponseEntity<>(response, getStatus(code));
    }

    /**
     * Crea una nueva ubicación mediante una petición POST
     * @param createUbicacionDTO DTO con los datos de la nueva ubicación
     * @return ResponseEntity con el resultado de la creación y el estado HTTP
     */
    @PostMapping("")
    public ResponseEntity<Object> create(@RequestBody CreateUbicacionDTO createUbicacionDTO) {
        Map<String, Object> response = ubicacionService.create(createUbicacionDTO);
        int code = (int) response.get("success");
        return new ResponseEntity<>(response, getStatus(code));
    }

    /**
     * Actualiza una ubicación existente mediante una petición PUT
     * @param id ID de la ubicación a actualizar
     * @param createUbicacionDTO DTO con los datos actualizados de la ubicación
     * @return ResponseEntity con el resultado de la actualización y el estado HTTP
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Integer id, @RequestBody CreateUbicacionDTO createUbicacionDTO) {
        Map<String, Object> response = ubicacionService.update(id, createUbicacionDTO);
        int code = (int) response.get("success");
        return new ResponseEntity<>(response, getStatus(code));
    }

    /**
     * Elimina una ubicación mediante una petición DELETE
     * @param id ID de la ubicación a eliminar
     * @return ResponseEntity con el resultado de la eliminación y el estado HTTP
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {
        Map<String, Object> response = ubicacionService.delete(id);
        int code = (int) response.get("success");
        return new ResponseEntity<>(response, getStatus(code));
    }
}
