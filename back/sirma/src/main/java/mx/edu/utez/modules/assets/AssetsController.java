package mx.edu.utez.modules.assets;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * Controlador REST para la gestión de activos.
 * Expone endpoints para crear, leer, actualizar y cambiar el estatus de los activos.
 * @author Ithera Team
 */
@AllArgsConstructor
@RestController
@RequestMapping("/api/activos")
public class AssetsController {

    private final AssetsService assetsService;

    /**
     * Recupera una lista paginada de activos activos.
     * @param pageable Configuración de paginación y ordenamiento.
     * @return ResponseEntity con la lista de activos.
     */
    @GetMapping
    public ResponseEntity<ApiResponse> findAll(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        ApiResponse response = assetsService.findAll(pageable);
        return ResponseEntity.status(response.getStatus())
                .cacheControl(CacheControl.noStore().mustRevalidate())
                .body(response);
    }

    /**
     * Busca un activo por su ID.
     * @param id Identificador del activo.
     * @return ResponseEntity con el activo encontrado o error si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id) {
        ApiResponse response = assetsService.findById(id);
        return ResponseEntity.status(response.getStatus())
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS).cachePublic())
                .body(response);
    }

    /**
     * Recupera estadísticas generales de los activos registrados (ej. totales, inactivos, por estado, etc.).
     * @return ResponseEntity conteniendo el <code>ApiResponse</code> con el objeto de estadísticas.
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse> getStats() {
        ApiResponse response = assetsService.getAssetsStats();
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Registra un nuevo activo.
     * @param dto Datos del activo a registrar.
     * @return ResponseEntity con el activo creado.
     */
    @PostMapping
    public ResponseEntity<ApiResponse> save(@Valid @RequestBody AssetsDTO dto) {
        ApiResponse response = assetsService.save(dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Actualiza la información de un activo existente.
     * @param id Identificador del activo a actualizar.
     * @param dto Nuevos datos del activo.
     * @return ResponseEntity con el activo actualizado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody AssetsDTO dto) {
        ApiResponse response = assetsService.update(id, dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Cambia el estatus de un activo a inactivo (baja lógica).
     * @param id Identificador del activo.
     * @return ResponseEntity con el resultado de la operación.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse> toggleStatus(@PathVariable Long id) {
        ApiResponse response = assetsService.toggleStatus(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

}
