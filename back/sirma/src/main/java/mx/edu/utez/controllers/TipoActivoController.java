package mx.edu.utez.controllers;

import lombok.RequiredArgsConstructor;
import mx.edu.utez.dtos.CreateTipoActivoDTO;
import mx.edu.utez.services.TipoActivoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static mx.edu.utez.util.HandleStatus.getStatus;

/**
 * Controlador REST para gestionar las operaciones relacionadas con los tipos de activo.
 * Proporciona endpoints para obtener, crear, actualizar y eliminar tipos de activo.
 * Utiliza TipoActivoService para la lógica de negocio y devuelve respuestas con el estado HTTP adecuado.
 * Permite solicitudes CORS desde cualquier origen.
 * @author Ithera Team
 */
@RestController
@RequestMapping("/api/tipo-activo")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TipoActivoController {

    private final TipoActivoService tipoActivoService;

    /**
     * Obtiene todos los tipos de activo mediante una petición GET
     * Mediante un mapa se devuelve la lista de tipos de activo por la inyección del servicio
     * @return ResponseEntity con la lista de tipos de activo y el estado HTTP
     */
    @GetMapping("")
    public ResponseEntity<Object> getAll() {
        Map<String, Object> response = tipoActivoService.getAll();

        int code = (int) response.get("success");

        return new ResponseEntity<>(response, getStatus(code));
    }

    /**
     * Obtiene un tipo de activo por su ID mediante una petición GET
     * @param id ID del tipo de activo a buscar
     * @return ResponseEntity con el tipo de activo encontrado y el estado HTTP
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Integer id) {
        Map<String, Object> response = tipoActivoService.getById(id);

        int code = (int) response.get("success");

        return new ResponseEntity<>(response, getStatus(code));
    }

    /**
     * Crea un nuevo tipo de activo mediante una petición POST
     * @param createTipoActivoDTO DTO con los datos del nuevo tipo de activo
     * @return ResponseEntity con el resultado de la creación y el estado HTTP
     */
    @PostMapping("")
    public ResponseEntity<Object> create(@RequestBody CreateTipoActivoDTO createTipoActivoDTO) {
        Map<String, Object> response = tipoActivoService.create(createTipoActivoDTO);

        int code = (int) response.get("success");

        return new ResponseEntity<>(response, getStatus(code));
    }

    /**
     * Actualiza un tipo de activo existente mediante una petición PUT
     * @param id ID del tipo de activo a actualizar
     * @param createTipoActivoDTO DTO con los datos actualizados del tipo de activo
     * @return ResponseEntity con el resultado de la actualización y el estado HTTP
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Integer id, @RequestBody CreateTipoActivoDTO createTipoActivoDTO) {
        Map<String, Object> response = tipoActivoService.update(id, createTipoActivoDTO);

        int code = (int) response.get("success");

        return new ResponseEntity<>(response, getStatus(code));
    }

    /**
     * Elimina un tipo de activo mediante una petición DELETE
     * @param id ID del tipo de activo a eliminar
     * @return ResponseEntity con el resultado de la eliminación y el estado HTTP
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {
        Map<String, Object> response = tipoActivoService.delete(id);

        int code = (int) response.get("success");

        return new ResponseEntity<>(response, getStatus(code));
    }
}
