package mx.edu.utez.controllers;

import lombok.RequiredArgsConstructor;
import mx.edu.utez.dtos.CreateActivoDTO;
import mx.edu.utez.services.ActivoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static mx.edu.utez.util.HandleStatus.getStatus;

/**
 * Controlador REST para gestionar las operaciones relacionadas con los activos.
 * Proporciona endpoints para obtener, crear, actualizar y eliminar activos.
 * Utiliza ActivoService para la lógica de negocio y devuelve respuestas con el estado HTTP adecuado.
 * Permite solicitudes CORS desde cualquier origen.
 * @author Ithera Team
 */
@RestController
@RequestMapping("/api/activo")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ActivoController {

    private final ActivoService activoService;

    /**
     * Obtiene todos los activos mediante una petición GET
     * Mediante un mapa se devuelve la lista de activos por la inyección del servicio
     * @return ResponseEntity con la lista de activos y el estado HTTP
     */
    @GetMapping("")
    public ResponseEntity<Object> getAll() {
        Map<String, Object> response = activoService.getAll();

        int code = (int) response.get("success");

        return new ResponseEntity<>(response, getStatus(code));

    }

    /**
     * Obtiene un activo por su ID mediante una petición GET
     * @param id ID del activo a buscar
     * @return ResponseEntity con el activo encontrado y el estado HTTP
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Integer id) {
        Map<String, Object> response = activoService.getById(id);

        int code = (int) response.get("success");

        return new ResponseEntity<>(response, getStatus(code));
    }

    /**
     * Crea un nuevo activo mediante una petición POST
     * @param createActivoDTO DTO con los datos del nuevo activo
     * @return ResponseEntity con el resultado de la creación y el estado HTTP
     */
    @PostMapping("")
    public ResponseEntity<Object> create(@RequestBody CreateActivoDTO createActivoDTO) {
        Map<String, Object> response = activoService.create(createActivoDTO);

        int code = (int) response.get("success");

        return new ResponseEntity<>(response, getStatus(code));
    }

    /**
     * Actualiza un activo existente mediante una petición PUT
     * @param id ID del activo a actualizar
     * @param createActivoDTO DTO con los datos actualizados del activo
     * @return ResponseEntity con el resultado de la actualización y el estado HTTP
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Integer id, @RequestBody CreateActivoDTO createActivoDTO) {
        Map<String, Object> response = activoService.update(id, createActivoDTO);

        int code = (int) response.get("success");

        return new ResponseEntity<>(response, getStatus(code));
    }

    /**
     * Elimina un activo mediante una petición DELETE
     * @param id ID del activo a eliminar
     * @return ResponseEntity con el resultado de la eliminación y el estado HTTP
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {
        Map<String, Object> response = activoService.delete(id);

        int code = (int) response.get("success");

        return new ResponseEntity<>(response, getStatus(code));
    }

}
