package mx.edu.utez.modules.edificios;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoint controlador y expositor de API capaz de interactuar con Edificios y catálogos dependientes.
 *
 * @author Ithera Team
 */
@RestController
@RequestMapping("/api/edificios")
@AllArgsConstructor
public class EdificioController {

    private final EdificioService edificioService;

    /**
     * Recupera todos los edificios paginados mediante una petición GET.
     *
     * @param pageable Configuración inyectable por RequestParam.
     * @return Lista paginada.
     */
    @GetMapping
    public ResponseEntity<ApiResponse> findAll(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        ApiResponse response = edificioService.findAll(pageable);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Búsqueda individual apuntando por Id particular estático.
     *
     * @param id Clave del edificio en parámetro de ruta.
     * @return Json mapeado a Entidad o null y 404.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id) {
        ApiResponse response = edificioService.findById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Descarga o recupera los recintos que son descendencia general por agrupación natural un mismo Campus.
     *
     * @param campusId Variable provista.
     * @return <code>List&lt;Edificio&gt;</code> envueltos.
     */
    @GetMapping("/campus/{campusId}")
    public ResponseEntity<ApiResponse> findByCampus(@PathVariable Long campusId) {
        ApiResponse response = edificioService.findByCampus(campusId);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Ingresa la directriz para dar nueva alta de edificio validado con su contenedor JSON DTO.
     *
     * @param dto Mapeo enviado en body.
     * @return El recurso salvado exitosamente de cruzar todas las normas.
     */
    @PostMapping
    public ResponseEntity<ApiResponse> save(@Valid @RequestBody EdificioDTO dto) {
        ApiResponse response = edificioService.save(dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Sobreescribe parámetros vitales o el puntero de campus a un ID base en Edificio, comprobando limitantes de nombre.
     *
     * @param id Referencia en ruta.
     * @param dto Payload Json con cuerpo updateado.
     * @return Confirmación o Conflict 409.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody EdificioDTO dto) {
        ApiResponse response = edificioService.update(id, dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Habilita un cambio semántico booleano e integral de Alta a Baja lógica (siendo Patch por la focalización de efecto en bit a bit).
     *
     * @param id Puntero clave referenciable.
     * @return <code>ApiResponse</code> con las incidencias aplicadas o las cascadas reportadas.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse> toggleStatus(@PathVariable Long id) {
        ApiResponse response = edificioService.toggleStatus(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Ruta análoga destructiva que limpia por su valor referencial.
     *
     * @param id Llave id desde RequestParam.
     * @return Notificación con estatus OK confirmatorio.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteById(@PathVariable Long id) {
        ApiResponse response = edificioService.deleteById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

}

