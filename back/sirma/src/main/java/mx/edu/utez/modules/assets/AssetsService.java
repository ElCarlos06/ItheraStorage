package mx.edu.utez.modules.assets;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.espacios.Espacio;
import mx.edu.utez.modules.espacios.EspacioRepository;
import mx.edu.utez.modules.modelos.Modelo;
import mx.edu.utez.modules.modelos.ModeloRepository;
import mx.edu.utez.modules.tipo_activos.TipoActivo;
import mx.edu.utez.modules.tipo_activos.TipoActivoRepository;
import mx.edu.utez.util.CloudinaryService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AssetsService {

    private final AssetsRepository assetsRepository;
    private final TipoActivoRepository tipoActivoRepository;
    private final ModeloRepository modeloRepository;
    private final EspacioRepository espacioRepository;
    private final ImagenActivoRepository imagenActivoRepository;
    private final CloudinaryService cloudinaryService;

    @Transactional(readOnly = true)
    public ApiResponse findAll() {
        List<Assets> list = assetsRepository.findAll();
        return new ApiResponse("OK", list, HttpStatus.OK);
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
        Optional<Modelo> modelo = modeloRepository.findById(dto.getIdModelo());
        if (modelo.isEmpty())
            return new ApiResponse("Modelo no encontrado", true, HttpStatus.NOT_FOUND);
        Optional<Espacio> espacio = espacioRepository.findById(dto.getIdEspacio());
        if (espacio.isEmpty())
            return new ApiResponse("Espacio no encontrado", true, HttpStatus.NOT_FOUND);

        Assets entity = new Assets();
        entity.setEtiqueta(dto.getEtiqueta());
        entity.setNumeroSerie(dto.getNumeroSerie());
        entity.setTipoActivo(tipoActivo.get());
        entity.setModelo(modelo.get());
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
        Optional<Modelo> modelo = modeloRepository.findById(dto.getIdModelo());
        if (modelo.isEmpty())
            return new ApiResponse("Modelo no encontrado", true, HttpStatus.NOT_FOUND);
        Optional<Espacio> espacio = espacioRepository.findById(dto.getIdEspacio());
        if (espacio.isEmpty())
            return new ApiResponse("Espacio no encontrado", true, HttpStatus.NOT_FOUND);

        Assets entity = found.get();
        entity.setEtiqueta(dto.getEtiqueta());
        entity.setNumeroSerie(dto.getNumeroSerie());
        entity.setTipoActivo(tipoActivo.get());
        entity.setModelo(modelo.get());
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
        entity.setEsActivo(!entity.getEsActivo());
        assetsRepository.save(entity);
        return new ApiResponse("Estado actualizado", entity, HttpStatus.OK);
    }

    // ────────── IMÁGENES ──────────

    private static final String CARPETA_CLOUDINARY = "sirma/activos";

    @Transactional
    public ApiResponse subirImagen(Long activoId, MultipartFile file) {
        Optional<Assets> found = assetsRepository.findById(activoId);
        if (found.isEmpty())
            return new ApiResponse("Activo no encontrado", true, HttpStatus.NOT_FOUND);
        try {
            Map<String, Object> resultado = cloudinaryService.upload(file, CARPETA_CLOUDINARY);

            ImagenActivo img = new ImagenActivo();
            img.setActivo(found.get());
            img.setUrlCloudinary((String) resultado.get("secure_url"));
            img.setPublicIdCloudinary((String) resultado.get("public_id"));
            img.setNombreArchivo(file.getOriginalFilename());
            imagenActivoRepository.save(img);

            return new ApiResponse("Imagen subida correctamente", img, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ApiResponse("Error al subir imagen: " + e.getMessage(), true, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse listarImagenes(Long activoId) {
        List<ImagenActivo> lista = imagenActivoRepository.findByActivoId(activoId);
        return new ApiResponse("OK", lista, HttpStatus.OK);
    }

    @Transactional
    public ApiResponse eliminarImagen(Long imagenId) {
        Optional<ImagenActivo> found = imagenActivoRepository.findById(imagenId);
        if (found.isEmpty())
            return new ApiResponse("Imagen no encontrada", true, HttpStatus.NOT_FOUND);
        try {
            cloudinaryService.delete(found.get().getPublicIdCloudinary());
            imagenActivoRepository.delete(found.get());
            return new ApiResponse("Imagen eliminada correctamente", HttpStatus.OK);
        } catch (IOException e) {
            return new ApiResponse("Error al eliminar imagen: " + e.getMessage(), true, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

