package mx.edu.utez.modules.reportes;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Agente de recepción o control que expone endpoints tipo REST relativos a la creación,
 * consulta y estadística de Reportes por incidencias de un activo.
 *
 * @author Ithera Team
 */
@RestController
@RequestMapping("/api/reportes")
@AllArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    /**
     * Muestra todo el registro en colecciones por página de reportes totales o pendientes de técnico a raíz del valor query.
     *
     * @param pageable Configuración enviada para ordenarlos.
     * @param sinAsignar Parámetro GET booleano definiendo si filtrar el conjunto general.
     * @return Formato de listado estándar.
     */
    @GetMapping
    public ResponseEntity<ApiResponse> findAll(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false, defaultValue = "false") boolean sinAsignar) {
        ApiResponse response = reporteService.findAll(pageable, sinAsignar);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Consulta con id preciso a un Reporte ya ejecutado para revisarse a completitud.
     *
     * @param id Clave identificadora inyectada por el cliente url.
     * @return El formato DTO de retorno o 404 NOT FOUND.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id) {
        ApiResponse response = reporteService.findById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Busca las incidencias reportadas hacia un activo en concreto y específico.
     *
     * @param activoId ID de Asset a agrupar.
     * @return Lista <code>Reporte</code>.
     */
    @GetMapping("/activo/{activoId}")
    public ResponseEntity<ApiResponse> findByActivo(@PathVariable Long activoId) {
        ApiResponse response = reporteService.findByActivo(activoId);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Entrega información analizada o condensada desde metadatos SQL para panel gráfico.
     *
     * @return Archivo u objeto con las propiedades correspondientes.
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse> getReportesStatsGlobally() {
        ApiResponse response = reporteService.getReporteStats();
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Empieza un nuevo ciclo de flujo instanciando un Reporte base derivando en una posible apertura a reparación de mantenimiento.
     *
     * @param dto Solicitud validada incluyendo Prioridad, Falla, Activo, y Empleado levantándolo.
     * @return Detalles confirmados expuestos desde objeto base.
     */
    @PostMapping
    public ResponseEntity<ApiResponse> save(@Valid @RequestBody ReporteDTO dto) {
        ApiResponse response = reporteService.save(dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Modifica la naturaleza de un reporte (Su seguimiento, estado, problema de foco, recategorización).
     *
     * @param id ID clave del servicio objetivo.
     * @param dto Nivel JSON con las nuevas adecuaciones referenciadas.
     * @return Éxito.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody ReporteDTO dto) {
        ApiResponse response = reporteService.update(id, dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Proceso para destruir en retrospectiva la rama de reporte de un activo que al hacerlo
     * limpiará de efecto cascada también el mantenimiento derivado, imágenes y reseteará estatus.
     *
     * @param id Identificador objetivo del url path.
     * @return Estatus OK informando a cliente.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        ApiResponse response = reporteService.delete(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

}
