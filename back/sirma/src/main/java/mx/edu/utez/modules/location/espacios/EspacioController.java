package mx.edu.utez.modules.location.espacios;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Interfaz controladora de red para emitir la comunicación de la plataforma hacia el manejo de Entornos/Espacios.
 *
 * @author Ithera Team
 */
@RestController
@RequestMapping("/api/espacios")
@AllArgsConstructor
public class EspacioController {

    private final EspacioService espacioService;

    /**
     * Extrae al cliente el objeto individual según se haya provisto por ruta paramétrica.
     *
     * @param pageable Formato de configuración paginable.
     * @return El modelo DTO enriquecido.
     */
    @GetMapping
    public ResponseEntity<ApiResponse> findAll(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        ApiResponse response = espacioService.findAll(pageable);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Búsqueda individual apuntando por Id.
     *
     * @param id Clave referenciada de la ruta paramétrica.
     * @return Formato Json de la entidad correspondiente.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findById(@PathVariable Long id) {
        ApiResponse response = espacioService.findById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Descarga o recupera todos los recintos alojados un mismo Edificio padre.
     *
     * @param edificioId Variable FK provista.
     * @return <code>List&lt;Espacio&gt;</code> filtrado con OK status.
     */
    @GetMapping("/edificio/{edificioId}")
    public ResponseEntity<ApiResponse> findByEdificio(@PathVariable Long edificioId) {
        ApiResponse response = espacioService.findByEdificio(edificioId);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Da alta de Aula/Espacio validado con su objeto JSON DTO.
     *
     * @param dto Mapeo enviado en body.
     * @return Atestamiento o status Code Conflict de ocurrir un nombre duplicado internamente.
     */
    @PostMapping
    public ResponseEntity<ApiResponse> save(@Valid @RequestBody EspacioDTO dto) {
        ApiResponse response = espacioService.save(dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Sobreescribe parámetros descriptivos y nominales de Espacio a una clave Id validada.
     *
     * @param id Referencial en ruta.
     * @param dto Actualizado DTO JSON vía body.
     * @return Notificación con el reestablecimiento o mensaje indicando un nombre prohibido sobre la base.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody EspacioDTO dto) {
        ApiResponse response = espacioService.update(id, dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Alternativa para bajar o habilitar operaciones temporales de control lógico sobre las áreas o salas.
     *
     * @param id Puntero evaluado.
     * @return Retorna bandera contraria aplicada de un boleano y su estatus correcto.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse> toggleStatus(@PathVariable Long id) {
        ApiResponse response = espacioService.toggleStatus(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Extermina a nivel SQL total una fila de la configuración actual sin historial residual.
     *
     * @param id Id en la ruta parametrizada a limpiar.
     * @return Status code 200 avisando un barrido funcional.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteById(@PathVariable Long id) {
        ApiResponse response = espacioService.deleteById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

}

