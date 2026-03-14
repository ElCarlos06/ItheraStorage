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

@Service
@AllArgsConstructor
public class EspacioService {

    private final EspacioRepository espacioRepository;
    private final EdificioRepository edificioRepository;

    @Transactional(readOnly = true)
    public ApiResponse findAll(Pageable pageable) {
        Page<Espacio> page = espacioRepository.findAll(pageable);
        return new ApiResponse("OK", page, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ApiResponse findById(Long id) {
        Optional<Espacio> found = espacioRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Espacio no encontrado", true, HttpStatus.NOT_FOUND);
        return new ApiResponse("OK", found.get(), HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ApiResponse findByEdificio(Long edificioId) {
        List<Espacio> list = espacioRepository.findByEdificioId(edificioId);
        return new ApiResponse("OK", list, HttpStatus.OK);
    }

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

    @Transactional
    public ApiResponse deleteById(Long id) {
        Optional<Espacio> found = espacioRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Espacio no encontrado", true, HttpStatus.NOT_FOUND);
        espacioRepository.deleteById(id);
        return new ApiResponse("Espacio eliminado", null, HttpStatus.OK);
    }

}
