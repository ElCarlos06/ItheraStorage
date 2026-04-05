package mx.edu.utez.modules.prioridades;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de negocio para gestión de prioridades en SIRMA.
 * Maneja operaciones CRUD con validaciones de unicidad.
 *
 * @author Ithera Team
 */
@Service
@AllArgsConstructor
public class PrioridadService {

    private final PrioridadRepository prioridadRepository;

    /**
     * Entrega un listado completo sin paginar de todas las prioridades estipuladas.
     * 
     * @return <code>ApiResponse</code> con el compendio de objetos base.
     */
    @Transactional(readOnly = true)
    public ApiResponse findAll() {
        List<Prioridad> list = prioridadRepository.findAll();
        return new ApiResponse("OK", list, HttpStatus.OK);
    }

    /**
     * Búsqueda singular empleando identificador explícito.
     * 
     * @param id Clave identificadora particular de control.
     * @return Éxito o 404 de no estar.
     */
    @Transactional(readOnly = true)
    public ApiResponse findById(Long id) {
        Optional<Prioridad> found = prioridadRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Prioridad no encontrada", true, HttpStatus.NOT_FOUND);
        return new ApiResponse("OK", found.get(), HttpStatus.OK);
    }

    /**
     * Registra en BD un nuevo estatuto de prioridad previniendo que no se repitan por nivel jerárquico.
     * 
     * @param dto Parámetros entrantes vía POST validados en Controller.
     * @return Response estandarizado afirmando su agregación.
     */
    @Transactional
    public ApiResponse save(PrioridadDTO dto) {
        if (prioridadRepository.existsByNivel(dto.getNivel()))
            return new ApiResponse("Ya existe una prioridad con ese nivel", true, HttpStatus.CONFLICT);
        Prioridad entity = new Prioridad();
        entity.setNivel(dto.getNivel());
        entity.setTiempoRespuestaHoras(dto.getTiempoRespuestaHoras());
        prioridadRepository.save(entity);
        return new ApiResponse("Prioridad registrada", entity, HttpStatus.CREATED);
    }

    /**
     * Proceso de actualización completo para los componentes básicos de la entidad.
     * 
     * @param id Objetivo referenciado.
     * @param dto Carga de datos renovada.
     * @return Alerta de cambios asimilados.
     */
    @Transactional
    public ApiResponse update(Long id, PrioridadDTO dto) {
        Optional<Prioridad> found = prioridadRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Prioridad no encontrada", true, HttpStatus.NOT_FOUND);
        Prioridad entity = found.get();
        entity.setNivel(dto.getNivel());
        entity.setTiempoRespuestaHoras(dto.getTiempoRespuestaHoras());
        prioridadRepository.save(entity);
        return new ApiResponse("Prioridad actualizada", entity, HttpStatus.OK);
    }

    /**
     * Limpieza o borrado profundo del registro de configuración.
     * 
     * @param id Principal objetivo inyectado en ruta.
     * @return Finalización de query delete.
     */
    @Transactional
    public ApiResponse deleteById(Long id) {
        Optional<Prioridad> found = prioridadRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Prioridad no encontrada", true, HttpStatus.NOT_FOUND);
        prioridadRepository.deleteById(id);
        return new ApiResponse("Prioridad eliminada", HttpStatus.OK);
    }

}
