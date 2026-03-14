package mx.edu.utez.modules.modelos;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.marcas.Marca;
import mx.edu.utez.modules.marcas.MarcaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ModeloService {

    private final ModeloRepository modeloRepository;
    private final MarcaRepository marcaRepository;

    @Transactional(readOnly = true)
    public ApiResponse findAll(Pageable pageable) {
        Page<Modelo> page = modeloRepository.findAll(pageable);
        return new ApiResponse("OK", page, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ApiResponse findById(Long id) {
        Optional<Modelo> found = modeloRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Modelo no encontrado", true, HttpStatus.NOT_FOUND);
        return new ApiResponse("OK", found.get(), HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ApiResponse findByMarca(Long marcaId) {
        List<Modelo> list = modeloRepository.findByMarcaId(marcaId);
        return new ApiResponse("OK", list, HttpStatus.OK);
    }

    @Transactional
    public ApiResponse save(ModeloDTO dto) {
        Optional<Marca> marca = marcaRepository.findById(dto.getIdMarca());
        if (marca.isEmpty())
            return new ApiResponse("Marca no encontrada", true, HttpStatus.NOT_FOUND);
        if (modeloRepository.existsByMarcaIdAndNombre(dto.getIdMarca(), dto.getNombre()))
            return new ApiResponse("Ya existe un modelo con ese nombre para esa marca", true, HttpStatus.CONFLICT);
        Modelo entity = new Modelo();
        entity.setMarca(marca.get());
        entity.setNombre(dto.getNombre());
        modeloRepository.save(entity);
        return new ApiResponse("Modelo registrado", entity, HttpStatus.CREATED);
    }

    @Transactional
    public ApiResponse update(Long id, ModeloDTO dto) {
        Optional<Modelo> found = modeloRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Modelo no encontrado", true, HttpStatus.NOT_FOUND);
        Optional<Marca> marca = marcaRepository.findById(dto.getIdMarca());
        if (marca.isEmpty())
            return new ApiResponse("Marca no encontrada", true, HttpStatus.NOT_FOUND);
        Modelo entity = found.get();
        entity.setMarca(marca.get());
        entity.setNombre(dto.getNombre());
        modeloRepository.save(entity);
        return new ApiResponse("Modelo actualizado", entity, HttpStatus.OK);
    }

    @Transactional
    public ApiResponse deleteById(Long id) {
        Optional<Modelo> found = modeloRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Modelo no encontrado", true, HttpStatus.NOT_FOUND);
        modeloRepository.deleteById(id);
        return new ApiResponse("Modelo eliminado", HttpStatus.OK);
    }

}
