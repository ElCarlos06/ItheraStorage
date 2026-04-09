package mx.edu.utez.modules.location.campus;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.location.edificios.EdificioRepository;
import mx.edu.utez.modules.location.espacios.EspacioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Servicio de negocio para gestión de campus en SIRMA.
 * Maneja operaciones CRUD con validaciones de unicidad y estado.
 *
 * @author Ithera Team
 */
@Service
@AllArgsConstructor
public class CampusService {

    private final CampusRepository campusRepository;
    private final EdificioRepository edificioRepository;
    private final EspacioRepository espacioRepository;

    /**
     * Extrae un listado de modelos activos procesado en paginación.
     *
     * @param pageable Configuraciones de despliegue pautadas.
     * @return <code>ApiResponse</code> conteniendo el bloque de elementos campus.
     */
    @Transactional(readOnly = true)
    public ApiResponse findAll(Pageable pageable) {
        Page<Campus> page = campusRepository.findAllByEsActivoTrue(pageable);
        return new ApiResponse("OK", page, HttpStatus.OK);
    }

    /**
     * Consulta con id directo un campus registrado y sus propiedades en bd.
     *
     * @param id Clave identificadora del campus objetivo.
     * @return Modelo de campus de tenerse constancia, error de no tenerse visibilidad.
     */
    @Transactional(readOnly = true)
    public ApiResponse findById(Long id) {
        Optional<Campus> found = campusRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Campus no encontrado", true, HttpStatus.NOT_FOUND);
        return new ApiResponse("OK", found.get(), HttpStatus.OK);
    }

    /**
     * Instancia un nuevo registro de Campus en el aplicativo. Si existió previamente
     * un campus inactivo con idéntico nombre, permite reactivarlo con la info que traiga
     * aprovechando reuso en base datos.
     *
     * @param dto Información validada de formulario con campos.
     * @return Referencia completa a dicho objeto y comprobante de resultado.
     */
    @Transactional
    public ApiResponse save(CampusDTO dto) {
        if (campusRepository.existsByNombreAndEsActivoTrue(dto.getNombre()))
            return new ApiResponse("Ya existe un campus activo con ese nombre. Desactívelo y actívelo de nuevo si desea reutilizarlo.", true, HttpStatus.CONFLICT);
        var existenteInactivo = campusRepository.findFirstByNombreAndEsActivoFalse(dto.getNombre());
        if (existenteInactivo.isPresent()) {
            Campus entity = existenteInactivo.get();
            entity.setDescripcion(dto.getDescripcion());
            entity.setEsActivo(true);
            campusRepository.save(entity);
            return new ApiResponse("Campus reactivado", entity, HttpStatus.OK);
        }
        Campus entity = new Campus();
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        entity.setEsActivo(dto.getEsActivo() != null ? dto.getEsActivo() : true);
        campusRepository.save(entity);
        return new ApiResponse("Campus registrado", entity, HttpStatus.CREATED);
    }

    /**
     * Cambia las propiedades básicas de un campus asegurando no duplicaturas.
     *
     * @param id De la entidad destino a sobreescribir.
     * @param dto DTO conteniendo la actualización parcial/total.
     * @return Envoltorio con el feedback de la persistencia de cambios.
     */
    @Transactional
    public ApiResponse update(Long id, CampusDTO dto) {
        Optional<Campus> found = campusRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Campus no encontrado", true, HttpStatus.NOT_FOUND);
        if (campusRepository.existsByNombreAndEsActivoTrueAndIdNot(dto.getNombre(), id))
            return new ApiResponse("Ya existe otro campus activo con ese nombre. Desactívelo y actívelo de nuevo si desea reutilizarlo.", true, HttpStatus.CONFLICT);
        Campus entity = found.get();
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        if (dto.getEsActivo() != null) entity.setEsActivo(dto.getEsActivo());
        campusRepository.save(entity);
        return new ApiResponse("Campus actualizado", entity, HttpStatus.OK);
    }

    /**
     * Funcionalidad para aplicar un baja/alta lógica. Si la bandera pasará a falsa,
     * cascada en todos los edificios dependientes y a la vez en espacios la desactivación.
     *
     * @param id Llave directa del campus.
     * @return La respuesta englobada con la entidad Campus mutada tras operación.
     */
    @Transactional
    public ApiResponse toggleStatus(Long id) {
        Optional<Campus> found = campusRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Campus no encontrado", true, HttpStatus.NOT_FOUND);
        Campus entity = found.get();
        boolean nuevoEstado = !entity.getEsActivo();
        entity.setEsActivo(nuevoEstado);
        campusRepository.save(entity);
        if (!nuevoEstado) {
            var edificios = edificioRepository.findByCampusId(id);
            for (var ed : edificios) {
                ed.setEsActivo(false);
                edificioRepository.save(ed);
                espacioRepository.findByEdificioId(ed.getId()).forEach(esp -> {
                    esp.setEsActivo(false);
                    espacioRepository.save(esp);
                });
            }
        }
        return new ApiResponse("Estado actualizado", entity, HttpStatus.OK);
    }

    /**
     * Elimina enteramente y de raíz toda la instancia de BD perdiéndose historial.
     * Aplica como cascada dura desde Edificio para liberar espacio completo y constraints de llave (si no ha tenido tickets u otras capas).
     *
     * @param id Id a remover.
     * @return El éxito validado.
     */
    @Transactional
    public ApiResponse deleteById(Long id) {
        Optional<Campus> found = campusRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Campus no encontrado", true, HttpStatus.NOT_FOUND);
        var edificios = edificioRepository.findByCampusId(id);
        for (var ed : edificios) {
            espacioRepository.deleteAll(espacioRepository.findByEdificioId(ed.getId()));
            edificioRepository.delete(ed);
        }
        campusRepository.deleteById(id);
        return new ApiResponse("Campus eliminado", null, HttpStatus.OK);
    }

}
