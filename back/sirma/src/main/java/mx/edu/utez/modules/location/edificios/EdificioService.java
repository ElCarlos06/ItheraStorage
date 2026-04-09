package mx.edu.utez.modules.location.edificios;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.location.campus.Campus;
import mx.edu.utez.modules.location.campus.CampusRepository;
import mx.edu.utez.modules.location.espacios.EspacioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Lógica de negocio que administra la información operada sobre Edificios en el sistema.
 *
 * @author Ithera Team
 */
@Service
@AllArgsConstructor
public class EdificioService {

    private final EdificioRepository edificioRepository;
    private final CampusRepository campusRepository;
    private final EspacioRepository espacioRepository;

    /**
     * Muestra todo el catálogo activo de edificios con paginación integrada.
     *
     * @param pageable Preferencias de envoltura del cliente.
     * @return Conjunto en ApiResponse.
     */
    @Transactional(readOnly = true)
    public ApiResponse findAll(Pageable pageable) {
        Page<Edificio> page = edificioRepository.findAllByEsActivoTrue(pageable);
        return new ApiResponse("OK", page, HttpStatus.OK);
    }

    /**
     * Encuentra y devuelve un Edificio empleando su Id.
     *
     * @param id Entero base asociado.
     * @return Instancia 1:1 o error de no existir.
     */
    @Transactional(readOnly = true)
    public ApiResponse findById(Long id) {
        Optional<Edificio> found = edificioRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Edificio no encontrado", true, HttpStatus.NOT_FOUND);
        return new ApiResponse("OK", found.get(), HttpStatus.OK);
    }

    /**
     * Agrupa y lista de golpe todos los Edificios emparentados hacia el parámetro de campus entregado.
     *
     * @param campusId Base o FK de referencia.
     * @return Conjunto de elementos.
     */
    @Transactional(readOnly = true)
    public ApiResponse findByCampus(Long campusId) {
        List<Edificio> list = edificioRepository.findByCampusIdAndEsActivoTrue(campusId);
        return new ApiResponse("OK", list, HttpStatus.OK);
    }

    /**
     * Valida que no haya un edificio con el mismo nombre y guarda un registro.
     * También permite resucitar uno si por baja previa coincidiera con sus estatutos.
     *
     * @param dto Input DTO con toda la data esencial.
     * @return Formato empaquetado para el endpoint respectivo.
     */
    @Transactional
    public ApiResponse save(EdificioDTO dto) {
        Optional<Campus> campus = campusRepository.findById(dto.getIdCampus());
        if (campus.isEmpty())
            return new ApiResponse("Campus no encontrado", true, HttpStatus.NOT_FOUND);
        if (edificioRepository.existsByCampusIdAndNombreAndEsActivoTrue(dto.getIdCampus(), dto.getNombre()))
            return new ApiResponse("Ya existe un edificio activo con ese nombre en ese campus. Desactívelo y actívelo de nuevo si desea reutilizarlo.", true, HttpStatus.CONFLICT);
        var existenteInactivo = edificioRepository.findFirstByCampusIdAndNombreAndEsActivoFalse(dto.getIdCampus(), dto.getNombre());
        if (existenteInactivo.isPresent()) {
            Edificio entity = existenteInactivo.get();
            entity.setEsActivo(true);
            edificioRepository.save(entity);
            return new ApiResponse("Edificio reactivado", entity, HttpStatus.OK);
        }
        Edificio entity = new Edificio();
        entity.setCampus(campus.get());
        entity.setNombre(dto.getNombre());
        entity.setEsActivo(dto.getEsActivo() != null ? dto.getEsActivo() : true);
        edificioRepository.save(entity);
        return new ApiResponse("Edificio registrado", entity, HttpStatus.CREATED);
    }

    /**
     * Actualiza el valor o las propiedades de este sin colisionar con entidades hermanas activas en la BD.
     *
     * @param id ID Principal.
     * @param dto Base validada de actualización.
     * @return <code>ApiResponse</code> con el feedback en el Payload.
     */
    @Transactional
    public ApiResponse update(Long id, EdificioDTO dto) {
        Optional<Edificio> found = edificioRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Edificio no encontrado", true, HttpStatus.NOT_FOUND);
        Optional<Campus> campus = campusRepository.findById(dto.getIdCampus());
        if (campus.isEmpty())
            return new ApiResponse("Campus no encontrado", true, HttpStatus.NOT_FOUND);
        if (edificioRepository.existsByCampusIdAndNombreAndEsActivoTrueAndIdNot(dto.getIdCampus(), dto.getNombre(), id))
            return new ApiResponse("Ya existe otro edificio activo con ese nombre en ese campus. Desactívelo y actívelo de nuevo si desea reutilizarlo.", true, HttpStatus.CONFLICT);
        Edificio entity = found.get();
        entity.setCampus(campus.get());
        entity.setNombre(dto.getNombre());
        if (dto.getEsActivo() != null) entity.setEsActivo(dto.getEsActivo());
        edificioRepository.save(entity);
        return new ApiResponse("Edificio actualizado", entity, HttpStatus.OK);
    }

    /**
     * Invierte la bandera booleana en base de datos.
     * De ser una inactividad forzada, se pasará en efecto cascada apagando los espacios contenidos.
     *
     * @param id Principal objeto.
     * @return Éxito.
     */
    @Transactional
    public ApiResponse toggleStatus(Long id) {
        Optional<Edificio> found = edificioRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Edificio no encontrado", true, HttpStatus.NOT_FOUND);
        Edificio entity = found.get();
        boolean nuevoEstado = !entity.getEsActivo();
        entity.setEsActivo(nuevoEstado);
        edificioRepository.save(entity);
        if (!nuevoEstado) {
            espacioRepository.findByEdificioId(id).forEach(esp -> {
                esp.setEsActivo(false);
                espacioRepository.save(esp);
            });
        }
        return new ApiResponse("Estado actualizado", entity, HttpStatus.OK);
    }

    /**
     * Eliminación de borrado profundo (DELETE) ignorando historial. Cascada manual.
     *
     * @param id Clave referenciada a exterminar.
     * @return Estatus.
     */
    @Transactional
    public ApiResponse deleteById(Long id) {
        Optional<Edificio> found = edificioRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Edificio no encontrado", true, HttpStatus.NOT_FOUND);
        espacioRepository.findByEdificioId(id).forEach(espacioRepository::delete);
        edificioRepository.deleteById(id);
        return new ApiResponse("Edificio eliminado", null, HttpStatus.OK);
    }

}

