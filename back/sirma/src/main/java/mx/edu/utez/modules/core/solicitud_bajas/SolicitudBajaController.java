package mx.edu.utez.modules.core.solicitud_bajas;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST que gestiona las peticiones HTTP relacionadas con las solicitudes de baja.
 *
 * @author Ithera Team
 */
@RestController
@RequestMapping("/api/solicitudes-baja")
@AllArgsConstructor
public class SolicitudBajaController {

    private final SolicitudBajaService solicitudBajaService;

    /**
     * Recupera una lista paginada general de todas las solicitudes de baja en sistema.
     *
     * @param pageable Configuración de paginación por url.
     * @return Entidad completa en formato web JSON.
     */
    @GetMapping
    public ResponseEntity<ApiResponse> findAll(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        ApiResponse response = solicitudBajaService.findAll(pageable);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Busca y retorna la especificación de una solicitud de baja por su indicador principal.
     *
     * @param id Puntero clave a recolectar.
     * @return Respuesta con lo que contenga la baja si se localizó.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id) {
        ApiResponse response = solicitudBajaService.findById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Recupera un filtro directo por estado actual de esa solicitud.
     *
     * @param estado Estado exacto a evaluar.
     * @return Respuesta conteniendo la tabla filtrada.
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<ApiResponse> findByEstado(@PathVariable String estado) {
        ApiResponse response = solicitudBajaService.findByEstado(estado);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Efectúa una carga hacia una nueva solicitud generada.
     *
     * @param dto Empaquetado informativo base de la petición entrante validadora del daño.
     * @return Respuesta post con resultados expuestos en ApiResponse.
     */
    @PostMapping
    public ResponseEntity<ApiResponse> save(@Valid @RequestBody SolicitudBajaDTO dto) {
        ApiResponse response = solicitudBajaService.save(dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Enmienda o sentencia los registros dictaminando actualizaciones de fase/autorización para bajar un bien material.
     *
     * @param id Referencia original de la creación previa o ID general.
     * @param dto Comentarios provenientes y resolución de aceptación.
     * @return Modificaciones inyectadas reflejadas para el cliente HTTP.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody SolicitudBajaDTO dto) {
        ApiResponse response = solicitudBajaService.update(id, dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

}
