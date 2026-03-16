package mx.edu.utez.modules.assets;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.espacios.Espacio;
import mx.edu.utez.modules.espacios.EspacioRepository;
import mx.edu.utez.modules.marcas.Marca;
import mx.edu.utez.modules.marcas.MarcaRepository;
import mx.edu.utez.modules.modelos.Modelo;
import mx.edu.utez.modules.modelos.ModeloRepository;
import mx.edu.utez.modules.tipo_activos.TipoActivo;
import mx.edu.utez.modules.tipo_activos.TipoActivoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class AssetsService {

    private final AssetsRepository assetsRepository;
    private final TipoActivoRepository tipoActivoRepository;
    private final ModeloRepository modeloRepository;
    private final MarcaRepository marcaRepository;
    private final EspacioRepository espacioRepository;

    @Transactional(readOnly = true)
    public ApiResponse findAll(Pageable pageable) {
        Page<Assets> page = assetsRepository.findByEsActivoTrue(pageable);
        return new ApiResponse("OK", page, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ApiResponse findById(Long id) {
        Optional<Assets> found = assetsRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Activo no encontrado", true, HttpStatus.NOT_FOUND);
        return new ApiResponse("OK", found.get(), HttpStatus.OK);
    }

    @Transactional
    public ApiResponse save(AssetsDTO dto) {
        if (assetsRepository.existsByNumeroSerie(dto.getNumeroSerie()))
            return new ApiResponse("Ya existe un activo con ese número de serie", true, HttpStatus.CONFLICT);
        Optional<TipoActivo> tipoActivo = tipoActivoRepository.findById(dto.getIdTipoActivo());
        if (tipoActivo.isEmpty())
            return new ApiResponse("Tipo de activo no encontrado", true, HttpStatus.NOT_FOUND);
        Modelo modelo = resolveModelo(dto, tipoActivo.get());
        if (modelo == null)
            return new ApiResponse("El tipo de activo debe tener marca y modelo definidos en Catálogos", true, HttpStatus.BAD_REQUEST);
        Optional<Espacio> espacio = espacioRepository.findById(dto.getIdEspacio());
        if (espacio.isEmpty())
            return new ApiResponse("Espacio no encontrado", true, HttpStatus.NOT_FOUND);

        Assets entity = new Assets();
        entity.setEtiqueta(dto.getEtiqueta());
        entity.setNumeroSerie(dto.getNumeroSerie());
        entity.setTipoActivo(tipoActivo.get());
        entity.setModelo(modelo);
        entity.setEspacio(espacio.get());
        entity.setEstadoCustodia(dto.getEstadoCustodia() != null ? dto.getEstadoCustodia() : "Disponible");
        entity.setEstadoOperativo(dto.getEstadoOperativo() != null ? dto.getEstadoOperativo() : "OK");
        entity.setDescripcion(dto.getDescripcion());
        entity.setCosto(dto.getCosto());
        entity.setQrCodigo(dto.getQrCodigo());
        entity.setFechaAlta(dto.getFechaAlta() != null ? LocalDate.parse(dto.getFechaAlta()) : LocalDate.now());
        entity.setEsActivo(true);
        assetsRepository.save(entity);
        return new ApiResponse("Activo registrado", entity, HttpStatus.CREATED);
    }

    @Transactional
    public ApiResponse update(Long id, AssetsDTO dto) {
        Optional<Assets> found = assetsRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Activo no encontrado", true, HttpStatus.NOT_FOUND);
        Optional<TipoActivo> tipoActivo = tipoActivoRepository.findById(dto.getIdTipoActivo());
        if (tipoActivo.isEmpty())
            return new ApiResponse("Tipo de activo no encontrado", true, HttpStatus.NOT_FOUND);
        Modelo modelo = resolveModelo(dto, tipoActivo.get());
        if (modelo == null)
            return new ApiResponse("El tipo de activo debe tener marca y modelo definidos en Catálogos", true, HttpStatus.BAD_REQUEST);
        Optional<Espacio> espacio = espacioRepository.findById(dto.getIdEspacio());
        if (espacio.isEmpty())
            return new ApiResponse("Espacio no encontrado", true, HttpStatus.NOT_FOUND);

        Assets entity = found.get();
        entity.setEtiqueta(dto.getEtiqueta());
        entity.setNumeroSerie(dto.getNumeroSerie());
        entity.setTipoActivo(tipoActivo.get());
        entity.setModelo(modelo);
        entity.setEspacio(espacio.get());
        if (dto.getEstadoCustodia() != null) entity.setEstadoCustodia(dto.getEstadoCustodia());
        if (dto.getEstadoOperativo() != null) entity.setEstadoOperativo(dto.getEstadoOperativo());
        entity.setDescripcion(dto.getDescripcion());
        entity.setCosto(dto.getCosto());
        entity.setQrCodigo(dto.getQrCodigo());
        if (dto.getEsActivo() != null) entity.setEsActivo(dto.getEsActivo());
        assetsRepository.save(entity);
        return new ApiResponse("Activo actualizado", entity, HttpStatus.OK);
    }

    @Transactional
    public ApiResponse toggleStatus(Long id) {
        Optional<Assets> found = assetsRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Activo no encontrado", true, HttpStatus.NOT_FOUND);
        Assets entity = found.get();
        entity.setEsActivo(false);
        assetsRepository.save(entity);
        return new ApiResponse("Activo desactivado", entity, HttpStatus.OK);
    }

    /** Resuelve Modelo: por idModelo o por marca+modelo del tipo de activo */
    private Modelo resolveModelo(AssetsDTO dto, TipoActivo tipo) {
        if (dto.getIdModelo() != null) {
            return modeloRepository.findById(dto.getIdModelo()).orElse(null);
        }
        String marcaNombre = trim(tipo.getMarca());
        String modeloNombre = trim(tipo.getModelo());
        if (marcaNombre == null || modeloNombre == null) return null;
        Marca marca = marcaRepository.findByNombre(marcaNombre).orElseGet(() -> {
            Marca m = new Marca();
            m.setNombre(marcaNombre);
            return marcaRepository.save(m);
        });
        return modeloRepository.findFirstByMarcaIdAndNombre(marca.getId(), modeloNombre).orElseGet(() -> {
            Modelo mod = new Modelo();
            mod.setMarca(marca);
            mod.setNombre(modeloNombre);
            return modeloRepository.save(mod);
        });
    }

    private static String trim(String s) {
        return s != null && !s.isBlank() ? s.trim() : null;
    }

}
