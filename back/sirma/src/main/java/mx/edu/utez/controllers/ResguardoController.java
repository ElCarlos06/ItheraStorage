package mx.edu.utez.controllers;

import lombok.RequiredArgsConstructor;
import mx.edu.utez.dtos.CreateResguardoDTO;
import mx.edu.utez.services.ResguardoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static mx.edu.utez.util.HandleStatus.getStatus;

/**
 * Controlador REST para gestionar las operaciones relacionadas con los resguardos.
 * Proporciona endpoints para obtener, crear, actualizar y eliminar resguardos.
 * Utiliza ResguardoService para la lógica de negocio y devuelve respuestas con el estado HTTP adecuado.
 * Permite solicitudes CORS desde cualquier origen.
 * @author Ithera Team
 */
@RestController
@RequestMapping("/api/resguardo")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ResguardoController {

    private final ResguardoService resguardoService;

    /**
     * Obtiene todos los resguardos mediante una petición GET
     * Mediante un mapa se devuelve la lista de resguardos por la inyección del servicio
     * @return ResponseEntity con la lista de resguardos y el estado HTTP
     */
    @GetMapping("")
    public ResponseEntity<Object> getAll() {
        Map<String, Object> response = resguardoService.getAll();

        int code = (int) response.get("success");

        return new ResponseEntity<>(response, getStatus(code));
    }

    /**
     * Obtiene un resguardo por su ID mediante una petición GET
     * @param id ID del resguardo a buscar
     * @return ResponseEntity con el resguardo encontrado y el estado HTTP
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Integer id) {
        Map<String, Object> response = resguardoService.getById(id);

        int code = (int) response.get("success");

        return new ResponseEntity<>(response, getStatus(code));
    }

    /**
     * Crea un nuevo resguardo mediante una petición POST
     * @param createResguardoDTO DTO con los datos del nuevo resguardo
     * @return ResponseEntity con el resultado de la creación y el estado HTTP
     */
    @PostMapping("")
    public ResponseEntity<Object> create(@RequestBody CreateResguardoDTO createResguardoDTO) {
        Map<String, Object> response = resguardoService.create(createResguardoDTO);

        int code = (int) response.get("success");

        return new ResponseEntity<>(response, getStatus(code));
    }

    /**
     * Actualiza un resguardo existente mediante una petición PUT
     * @param id ID del resguardo a actualizar
     * @param createResguardoDTO DTO con los datos actualizados del resguardo
     * @return ResponseEntity con el resultado de la actualización y el estado HTTP
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Integer id, @RequestBody CreateResguardoDTO createResguardoDTO) {
        Map<String, Object> response = resguardoService.update(id, createResguardoDTO);

        int code = (int) response.get("success");

        return new ResponseEntity<>(response, getStatus(code));
    }

    /**
     * Elimina un resguardo mediante una petición DELETE
     * @param id ID del resguardo a eliminar
     * @return ResponseEntity con el resultado de la eliminación y el estado HTTP
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {
        Map<String, Object> response = resguardoService.delete(id);

        int code = (int) response.get("success");

        return new ResponseEntity<>(response, getStatus(code));
    }
}
