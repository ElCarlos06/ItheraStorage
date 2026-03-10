package mx.edu.utez.modules.marcas;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de negocio para gestión de marcas en SIRMA.
 * Maneja operaciones CRUD con validaciones de unicidad.
 *
 * @author Ithera Team
 */
@Service
@AllArgsConstructor
public class MarcaService {

    private final MarcaRepository marcaRepository;

    @Transactional(readOnly = true)
    public ApiResponse findAll() {
        List<Marca> list = marcaRepository.findAll();
        return new ApiResponse("OK", list, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ApiResponse findById(Long id) {
        Optional<Marca> found = marcaRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Marca no encontrada", true, HttpStatus.NOT_FOUND);
        return new ApiResponse("OK", found.get(), HttpStatus.OK);
    }

    @Transactional
    public ApiResponse save(MarcaDTO dto) {
        if (marcaRepository.existsByNombre(dto.getNombre()))
            return new ApiResponse("Ya existe una marca con ese nombre", true, HttpStatus.CONFLICT);
        Marca marca = new Marca();
        marca.setNombre(dto.getNombre());
        marcaRepository.save(marca);
        return new ApiResponse("Marca registrada", marca, HttpStatus.CREATED);
    }

    @Transactional
    public ApiResponse update(Long id, MarcaDTO dto) {
        Optional<Marca> found = marcaRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Marca no encontrada", true, HttpStatus.NOT_FOUND);
        Marca marca = found.get();
        marca.setNombre(dto.getNombre());
        marcaRepository.save(marca);
        return new ApiResponse("Marca actualizada", marca, HttpStatus.OK);
    }

    @Transactional
    public ApiResponse deleteById(Long id) {
        Optional<Marca> found = marcaRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Marca no encontrada", true, HttpStatus.NOT_FOUND);
        marcaRepository.deleteById(id);
        return new ApiResponse("Marca eliminada", HttpStatus.OK);
    }

}
