package mx.edu.utez.modules.reporting.bitacora;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST diseñado para exponer los endpoints de consulta a la Bitácora del sistema.
 * Permite buscar eventos globales o filtrados por un usuario o activo específico.
 *
 * @author Ithera Team
 */
@RestController
@RequestMapping("/api/bitacora")
@AllArgsConstructor
public class BitacoraController {

    private final BitacoraService bitacoraService;

    /**
     * Extrae un listado general de todos los eventos registrados en la bitácora.
     *
     * @return <code>ResponseEntity</code> envolviendo el ApiResponse con la colección de eventos.
     */
    @GetMapping
    public ResponseEntity<ApiResponse> findAll() {
        ApiResponse response = bitacoraService.findAll();
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Consulta el detalle específico de un registro de bitácora de acuerdo a su id.
     *
     * @param id Identificador de bitácora obtenido de la ruta.
     * @return Retorna un <code>ApiResponse</code> con los datos o error 404 de no encontrarse.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id) {
        ApiResponse response = bitacoraService.findById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Extrae la colección de eventos que han afectado puntualmente el ciclo de vida de un activo.
     *
     * @param activoId Clave foránea del activo a analizar.
     * @return Lista de eventos de bitácora vinculados con este elemento.
     */
    @GetMapping("/activo/{activoId}")
    public ResponseEntity<ApiResponse> findByActivo(@PathVariable Long activoId) {
        ApiResponse response = bitacoraService.findByActivo(activoId);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Retorna todos los eventos generados por la interacción directa de un usuario en concreto.
     *
     * @param usuarioId Clave foránea del usuario que efectuó la acción.
     * @return Colección de entradas detonadas por el usuario determinado.
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<ApiResponse> findByUsuario(@PathVariable Long usuarioId) {
        ApiResponse response = bitacoraService.findByUsuario(usuarioId);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Inserta manualmente una nueva entrada en la bitácora desde cliente.
     * Normalmente los métodos transaccionales del backend se alimentan con la bitácora por detrás usando el servicio para registrar automáticamente los eventos.
     *
     * @param dto DTO recibido como request body validado.
     * @return Confirmación y resultado de la inserción de evento.
     */
    @PostMapping
    public ResponseEntity<ApiResponse> save(@Valid @RequestBody BitacoraDTO dto) {
        ApiResponse response = bitacoraService.save(dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

}
