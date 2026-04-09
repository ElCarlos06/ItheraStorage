package mx.edu.utez.modules.location.areas;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Servicio de negocio para gestión de áreas en SIRMA.
 * Maneja operaciones CRUD con validaciones de unicidad.
 *
 * @author Ithera Team
 */
@Service
@AllArgsConstructor
public class AreaService {

    private final AreaRepository areaRepository;

    /**
     * Recupera una lista paginada de todas las áreas registradas.
     *
     * @param pageable Configuración de paginación solicitada (página, tamaño, orden).
     * @return <code>ApiResponse</code> conteniendo la página de áreas (Page&lt;Area&gt;) y estatus de éxito.
     */
    @Transactional(readOnly = true)
    public ApiResponse findAll(Pageable pageable) {
        Page<Area> page = areaRepository.findAll(pageable);
        return new ApiResponse("OK", page, HttpStatus.OK);
    }

    /**
     * Busca un área específica por su identificador.
     *
     * @param id Identificador único del área a buscar.
     * @return <code>ApiResponse</code> con el área encontrada o mensaje de error en caso de no existir.
     */
    @Transactional(readOnly = true)
    public ApiResponse findById(Long id) {
        Optional<Area> found = areaRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Área no encontrada", true, HttpStatus.NOT_FOUND);
        return new ApiResponse("OK", found.get(), HttpStatus.OK);
    }

    /**
     * Registra una nueva área validando que no exista otra con el mismo nombre.
     *
     * @param dto El objeto de transferencia de datos con la información de la nueva área.
     * @return <code>ApiResponse</code> confirmando la creación o alertando sobre conflictos/errores.
     */
    @Transactional
    public ApiResponse save(AreaDTO dto) {
        if (areaRepository.existsByNombre(dto.getNombre()))
            return new ApiResponse("Ya existe un área con ese nombre", true, HttpStatus.CONFLICT);
        Area entity = new Area();
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        areaRepository.save(entity);
        return new ApiResponse("Área registrada", entity, HttpStatus.CREATED);
    }

    /**
     * Actualiza la información de un área existente de acuerdo con su identificador.
     *
     * @param id  Identificador único del área a actualizar.
     * @param dto El objeto con los datos modificados.
     * @return <code>ApiResponse</code> confirmando la actualización o alertando si no fue encontrada.
     */
    @Transactional
    public ApiResponse update(Long id, AreaDTO dto) {
        Optional<Area> found = areaRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Área no encontrada", true, HttpStatus.NOT_FOUND);
        Area entity = found.get();
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        areaRepository.save(entity);
        return new ApiResponse("Área actualizada", entity, HttpStatus.OK);
    }

    /**
     * Elimina lógicamente o físicamente un área (dependiendo de la implementación del repositorio).
     * En este caso, ejecuta un borrado físico del registro tras verificar que existe.
     *
     * @param id Identificador único del área a eliminar.
     * @return <code>ApiResponse</code> con el mensaje de confirmación o alerta si no se encontró.
     */
    @Transactional
    public ApiResponse deleteById(Long id) {
        Optional<Area> found = areaRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Área no encontrada", true, HttpStatus.NOT_FOUND);
        areaRepository.deleteById(id);
        return new ApiResponse("Área eliminada", HttpStatus.OK);
    }

}
