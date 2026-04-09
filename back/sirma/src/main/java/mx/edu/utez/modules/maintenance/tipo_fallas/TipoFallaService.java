package mx.edu.utez.modules.maintenance.tipo_fallas;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Servicio de negocio para gestión de tipos de falla en SIRMA.
 * Maneja operaciones CRUD con validaciones de unicidad.
 *
 * @author Ithera Team
 */
@Service
@AllArgsConstructor
public class TipoFallaService {

    private final TipoFallaRepository tipoFallaRepository;

    /**
     * Lista todos los tipos de falla existentes basándose en un formato paginado.
     *
     * @param pageable Configuración que dicta cómo se pagina y se ordenan los resultados.
     * @return Una respuesta estructurada que encapsula el contenido de los tipos de falla encontrados.
     */
    @Transactional(readOnly = true)
    public ApiResponse findAll(Pageable pageable) {
        Page<TipoFalla> page = tipoFallaRepository.findAll(pageable);
        return new ApiResponse("OK", page, HttpStatus.OK);
    }

    /**
     * Localiza y retorna un tipo de falla mediante su respectivo ID.
     *
     * @param id Número identificador a buscar en la tabla TipoFalla.
     * @return Devuelve un objeto ApiResponse conteniendo este tipo de falla o un error 404 en su defecto.
     */
    @Transactional(readOnly = true)
    public ApiResponse findById(Long id) {
        Optional<TipoFalla> found = tipoFallaRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Tipo de falla no encontrado", true, HttpStatus.NOT_FOUND);
        return new ApiResponse("OK", found.get(), HttpStatus.OK);
    }

    /**
     * Almacena y permite dar de alta un nuevo tipo del cual existe una falla.
     * Controla que no haya nombres duplicados de forma exacta de un tipo de fallo preexistente.
     *
     * @param dto El objeto o DTO con toda la información nueva relacionada al tipo de falla a intentar guardar.
     * @return ApiResponse con un error si ya existe ese nombre y si no con la información insertada y un código estatus creado.
     */
    @Transactional
    public ApiResponse save(TipoFallaDTO dto) {
        if (tipoFallaRepository.existsByNombre(dto.getNombre()))
            return new ApiResponse("Ya existe un tipo de falla con ese nombre", true, HttpStatus.CONFLICT);
        TipoFalla entity = new TipoFalla();
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        tipoFallaRepository.save(entity);
        return new ApiResponse("Tipo de falla registrado", entity, HttpStatus.CREATED);
    }

    /**
     * Actualiza atributos sobre un tipo de falla en el registro principal de sistema, tomando a consideración si éste ha sido encontrado.
     *
     * @param id Corresponde a la llave de donde se buscará el tipo de falla que será manipulado.
     * @param dto Los valores que contendrá y mutará para aplicarse en sí esta entidad de fallos.
     * @return ApiResponse retornará al usuario en la solicitud HTTP un contexto en base al estado de operación exitoso o no.
     */
    @Transactional
    public ApiResponse update(Long id, TipoFallaDTO dto) {
        Optional<TipoFalla> found = tipoFallaRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Tipo de falla no encontrado", true, HttpStatus.NOT_FOUND);
        TipoFalla entity = found.get();
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        tipoFallaRepository.save(entity);
        return new ApiResponse("Tipo de falla actualizado", entity, HttpStatus.OK);
    }

    /**
     * Suprime lógicamente o físicamente este registro si existe directamente apoyándose con el repositorio actual.
     *
     * @param id Identificador que localiza directamente en la base los atributos únicos del tipo de falla.
     * @return Una respuesta final positiva tras la manipulación y destrucción de los valores pertinentes.
     */
    @Transactional
    public ApiResponse deleteById(Long id) {
        Optional<TipoFalla> found = tipoFallaRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Tipo de falla no encontrado", true, HttpStatus.NOT_FOUND);
        tipoFallaRepository.deleteById(id);
        return new ApiResponse("Tipo de falla eliminado", HttpStatus.OK);
    }

}
