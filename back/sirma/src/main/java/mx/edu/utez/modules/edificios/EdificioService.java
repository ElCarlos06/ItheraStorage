package mx.edu.utez.modules.edificios;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.campus.Campus;
import mx.edu.utez.modules.campus.CampusRepository;
import mx.edu.utez.modules.espacios.EspacioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EdificioService {

    private final EdificioRepository edificioRepository;
    private final CampusRepository campusRepository;
    private final EspacioRepository espacioRepository;

    @Transactional(readOnly = true)
    public ApiResponse findAll(Pageable pageable) {
        Page<Edificio> page = edificioRepository.findAllByEsActivoTrue(pageable);
        return new ApiResponse("OK", page, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ApiResponse findById(Long id) {
        Optional<Edificio> found = edificioRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Edificio no encontrado", true, HttpStatus.NOT_FOUND);
        return new ApiResponse("OK", found.get(), HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ApiResponse findByCampus(Long campusId) {
        List<Edificio> list = edificioRepository.findByCampusIdAndEsActivoTrue(campusId);
        return new ApiResponse("OK", list, HttpStatus.OK);
    }

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

