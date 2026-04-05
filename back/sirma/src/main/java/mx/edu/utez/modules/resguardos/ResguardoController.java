package mx.edu.utez.modules.resguardos;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST que gestiona las peticiones HTTP relacionadas con los resguardos.
 *
 * @author Ithera Team
 */
@RestController
@RequestMapping("/api/resguardos")
@AllArgsConstructor
public class ResguardoController {

    private final ResguardoService resguardoService;

    /**
     * Obtiene una página listando todos los resguardos.
     *
     * @param pageable Configuración de paginación provista por la URL.
     * @return ResponseEntity con la información solicitada.
     */
    @GetMapping
    public ResponseEntity<ApiResponse> findAll(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        ApiResponse response = resguardoService.findAll(pageable);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Obtiene un resguardo por su llave primaria.
     *
     * @param id Identificador único del resguardo.
     * @return ResponseEntity con los detalles encontrados.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id) {
        ApiResponse response = resguardoService.findById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Obtiene resguardos filtrados por el identificador del equipo prestado.
     *
     * @param activoId Identificativo del bien material (Assets).
     * @return ResponseEntity listando las asociaciones.
     */
    @GetMapping("/activo/{activoId}")
    public ResponseEntity<ApiResponse> findByActivo(@PathVariable Long activoId) {
        ApiResponse response = resguardoService.findByActivo(activoId);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Obtiene los resguardos adscritos al número identificador de un empleado.
     *
     * @param userId Identificador del usuario propietario del resguardo.
     * @return ResponseEntity refiriendo las asignaciones del empleado consultado.
     */
    @GetMapping("/empleado/{userId}")
    public ResponseEntity<ApiResponse> findByEmpleado(@PathVariable Long userId) {
        ApiResponse response = resguardoService.findByEmpleado(userId);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Genera una nueva relación de resguardo hacia un empleado y su respectivo Activo.
     *
     * @param dto Extracción empaquetada de los valores enviados conteniendo al activo y las partes.
     * @return ResponseEntity con resultado de la inserción y código 201 generado.
     */
    @PostMapping
    public ResponseEntity<ApiResponse> save(@Valid @RequestBody ResguardoDTO dto) {
        ApiResponse response = resguardoService.save(dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Da actualización o avanza el estatus de las observaciones y ciclos de devolución o firma de resguardos prexistentes.
     *
     * @param id Conector del resguardo a ajustar.
     * @param dto Valores de seguimiento proporcionados en la transacción.
     * @return ResponseEntity evidenciando las mutaciones aplicadas.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody ResguardoDTO dto) {
        ApiResponse response = resguardoService.update(id, dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

}
