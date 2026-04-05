package mx.edu.utez.modules.espacios;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.edificios.Edificio;
import mx.edu.utez.modules.edificios.EdificioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Lógica de negocio orquestadora para manejar la dependencia real entre aulas (espacios)
 * con sus entidades padre (edificios).
 *
 * @author Ithera Team
 */
@Service
@AllArgsConstructor
public class EspacioService {

    private final EspacioRepository espacioRepository;
    private final EdificioRepository edificioRepository;

    /**
     * Muestra todo el catálogo activo de espacios en el sistema mediante paginación.
     *
     * @param pageable Preferencias de indexación de salida.
     * @return Conjunto envuelto en ApiResponse.
     */
    @Transactional(readOnly = true)
    public ApiResponse findAll(Pageable pageable) {
        Page<Espacio> page = espacioRepository.findAllByEsActivoTrue(pageable);
        return new ApiResponse("OK", page, HttpStatus.OK);
    }

    /**
     * Emite la consulta por un factor clave de identidad para ver si su registro existe.
     *
     * @param id Entero largo representativo.
     * @return Instancia acoplada si es verdadera, o error not found.
     */
    @Transactional(readOnly = true)
    public ApiResponse findById(Long id) {
        Optional<Espacio> found = espacioRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Espacio no encontrado", true, HttpStatus.NOT_FOUND);
        return new ApiResponse("OK", found.get(), HttpStatus.OK);
    }

    /**
     * Agrupa y lista todos los espacios correspondientes directamente al id de edificio enviado.
     *
     * @param edificioId Base FK del edificio físico.
     * @return Listado de pertenencias limitadas a las activadas.
     */
    @Transactional(readOnly = true)
    public ApiResponse findByEdificio(Long edificioId) {
        List<Espacio> list = espacioRepository.findByEdificioIdAndEsActivoTrue(edificioId);
        return new ApiResponse("OK", list, HttpStatus.OK);
    }

    /**
     * Valida que un nuevo entorno no entre en choque por título contra otro ya dado en el edificio, en su defecto lo reactiva
     * reconfigurando su naturaleza si estaba bajo un estado inactivo.
     *
     * @param dto Molde de campos requeridos para un Espacio nuevo.
     * @return Respuesta estandarizada con CREATED 201 en la normalidad.
     */
    @Transactional
    public ApiResponse save(EspacioDTO dto) {
        Optional<Edificio> edificio = edificioRepository.findById(dto.getIdEdificio());
        if (edificio.isEmpty())
            return new ApiResponse("Edificio no encontrado", true, HttpStatus.NOT_FOUND);
        if (espacioRepository.existsByEdificioIdAndNombreEspacioAndEsActivoTrue(dto.getIdEdificio(), dto.getNombreEspacio()))
            return new ApiResponse("Ya existe un aula activa con ese nombre en ese edificio. Desactívela y actívela de nuevo si desea reutilizarla.", true, HttpStatus.CONFLICT);
        var existenteInactivo = espacioRepository.findFirstByEdificioIdAndNombreEspacioAndEsActivoFalse(dto.getIdEdificio(), dto.getNombreEspacio());
        if (existenteInactivo.isPresent()) {
            Espacio entity = existenteInactivo.get();
            entity.setTipoEspacio(dto.getTipoEspacio());
            entity.setDescripcion(dto.getDescripcion());
            entity.setEsActivo(true);
            espacioRepository.save(entity);
            return new ApiResponse("Aula reactivada", entity, HttpStatus.OK);
        }
        Espacio entity = new Espacio();
        entity.setEdificio(edificio.get());
        entity.setNombreEspacio(dto.getNombreEspacio());
        entity.setTipoEspacio(dto.getTipoEspacio());
        entity.setDescripcion(dto.getDescripcion());
        entity.setEsActivo(dto.getEsActivo() != null ? dto.getEsActivo() : true);
        espacioRepository.save(entity);
        return new ApiResponse("Espacio registrado", entity, HttpStatus.CREATED);
    }

    /**
     * Reconfiguración que altera o cambia detalles perimetrales o de pertenencia, respetando las exclusividades de nombre intramuros.
     *
     * @param id Focalización clave.
     * @param dto Molde mutador en sistema.
     * @return Objeto sobreescrito confirmando los cambios inyectados.
     */
    @Transactional
    public ApiResponse update(Long id, EspacioDTO dto) {
        Optional<Espacio> found = espacioRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Espacio no encontrado", true, HttpStatus.NOT_FOUND);
        Optional<Edificio> edificio = edificioRepository.findById(dto.getIdEdificio());
        if (edificio.isEmpty())
            return new ApiResponse("Edificio no encontrado", true, HttpStatus.NOT_FOUND);
        if (espacioRepository.existsByEdificioIdAndNombreEspacioAndEsActivoTrueAndIdNot(dto.getIdEdificio(), dto.getNombreEspacio(), id))
            return new ApiResponse("Ya existe otra aula activa con ese nombre en ese edificio. Desactívela y actívela de nuevo si desea reutilizarla.", true, HttpStatus.CONFLICT);
        Espacio entity = found.get();
        entity.setEdificio(edificio.get());
        entity.setNombreEspacio(dto.getNombreEspacio());
        entity.setTipoEspacio(dto.getTipoEspacio());
        entity.setDescripcion(dto.getDescripcion());
        if (dto.getEsActivo() != null) entity.setEsActivo(dto.getEsActivo());
        espacioRepository.save(entity);
        return new ApiResponse("Espacio actualizado", entity, HttpStatus.OK);
    }

    /**
     * Enciende o apaga dinámicamente un área inhabilitándola para el motor sin llegar a borrar la data de integridad del servicio.
     *
     * @param id Parametro a aislar.
     * @return Success al actualizarse su estado semántico de switch.
     */
    @Transactional
    public ApiResponse toggleStatus(Long id) {
        Optional<Espacio> found = espacioRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Espacio no encontrado", true, HttpStatus.NOT_FOUND);
        Espacio entity = found.get();
        entity.setEsActivo(!entity.getEsActivo());
        espacioRepository.save(entity);
        return new ApiResponse("Estado actualizado", entity, HttpStatus.OK);
    }

    /**
     * Erradica sin condiciones de rastro histórico (o bitácora) al espacio entero provisto.
     * En base de datos fallará la consistencia externa si esto formaba parte persistente de un recurso mayor (Activos/Tickets que no permiten CASCAD).
     *
     * @param id Elemento borrable.
     * @return Ok al aniquilar.
     */
    @Transactional
    public ApiResponse deleteById(Long id) {
        Optional<Espacio> found = espacioRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Espacio no encontrado", true, HttpStatus.NOT_FOUND);
        espacioRepository.deleteById(id);
        return new ApiResponse("Espacio eliminado", null, HttpStatus.OK);
    }

}
