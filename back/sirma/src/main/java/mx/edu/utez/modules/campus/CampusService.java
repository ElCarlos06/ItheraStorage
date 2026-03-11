package mx.edu.utez.modules.campus;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.edificios.EdificioRepository;
import mx.edu.utez.modules.espacios.EspacioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    @Transactional(readOnly = true)
    public ApiResponse findAll() {
        List<Campus> list = campusRepository.findAll();
        return new ApiResponse("OK", list, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ApiResponse findById(Long id) {
        Optional<Campus> found = campusRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Campus no encontrado", true, HttpStatus.NOT_FOUND);
        return new ApiResponse("OK", found.get(), HttpStatus.OK);
    }

    @Transactional
    public ApiResponse save(CampusDTO dto) {
        if (campusRepository.existsByNombreAndEsActivoTrue(dto.getNombre()))
            return new ApiResponse("Ya existe un campus activo con ese nombre. Desactive el existente si desea reutilizarlo.", true, HttpStatus.CONFLICT);
        Campus entity = new Campus();
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        entity.setEsActivo(dto.getEsActivo() != null ? dto.getEsActivo() : true);
        campusRepository.save(entity);
        return new ApiResponse("Campus registrado", entity, HttpStatus.CREATED);
    }

    @Transactional
    public ApiResponse update(Long id, CampusDTO dto) {
        Optional<Campus> found = campusRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Campus no encontrado", true, HttpStatus.NOT_FOUND);
        if (campusRepository.existsByNombreAndEsActivoTrueAndIdNot(dto.getNombre(), id))
            return new ApiResponse("Ya existe otro campus activo con ese nombre. Desactive el existente si desea reutilizarlo.", true, HttpStatus.CONFLICT);
        Campus entity = found.get();
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        if (dto.getEsActivo() != null) entity.setEsActivo(dto.getEsActivo());
        campusRepository.save(entity);
        return new ApiResponse("Campus actualizado", entity, HttpStatus.OK);
    }

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

    @Transactional
    public ApiResponse deleteById(Long id) {
        Optional<Campus> found = campusRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Campus no encontrado", true, HttpStatus.NOT_FOUND);
        var edificios = edificioRepository.findByCampusId(id);
        for (var ed : edificios) {
            espacioRepository.findByEdificioId(ed.getId()).forEach(espacioRepository::delete);
            edificioRepository.delete(ed);
        }
        campusRepository.deleteById(id);
        return new ApiResponse("Campus eliminado", null, HttpStatus.OK);
    }

}
