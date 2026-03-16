package mx.edu.utez.modules.assets;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.espacios.Espacio;
import mx.edu.utez.modules.espacios.EspacioRepository;
import mx.edu.utez.modules.imagen_activo.ImagenActivo;
import mx.edu.utez.modules.imagen_activo.ImagenActivoRepository;
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

/**
 * Servicio de negocio para la gestión de Activos Fijos.
 * Contiene la lógica para registrar, consultar, actualizar y desactivar activos,
 * así como para enriquecer la respuesta con imágenes asociadas.
 *
 * @author Ithera Team
 */
@AllArgsConstructor
@Service
public class AssetsService {

    private final AssetsRepository assetsRepository;
    private final TipoActivoRepository tipoActivoRepository;
    private final ModeloRepository modeloRepository;
    private final MarcaRepository marcaRepository;
    private final EspacioRepository espacioRepository;
    // Inyectamos repositorio de imágenes para detalle extendido
    private final ImagenActivoRepository imagenActivoRepository;

    private static final String QR_FILENAME = "QR_CODE";

    /**
     * Obtiene una lista paginada de todos los activos que tienen estatus 'activo' (true).
     *
     * @param pageable Configuración de paginación.
     * @return ApiResponse con la página de activos encontrados.
     */
    @Transactional(readOnly = true)
    public ApiResponse findAll(Pageable pageable) {
        Page<Assets> page = assetsRepository.findByEsActivoTrue(pageable);
        return new ApiResponse("OK", page, HttpStatus.OK);
    }

    /**
     * Busca un activo por ID y enriquece la respuesta con las URLs de sus imágenes asociadas.
     *
     * @param id Identificador del activo.
     * @return ApiResponse con el DTO del activo (incluyendo imágenes) o mensaje de error.
     */
    @Transactional(readOnly = true)
    public ApiResponse findById(Long id) {
        Optional<Assets> found = assetsRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Activo no encontrado", true, HttpStatus.NOT_FOUND);

        Assets asset = found.get();
        AssetsDTO dto = toDTO(asset);
        
        // Enriquecer DTO con imágenes
        enrichWithImages(dto);
        
        return new ApiResponse("OK", dto, HttpStatus.OK);
    }

    /**
     * Enriquece el DTO del activo con las URLs de imágenes de perfil.
     * Excluye la imagen que corresponde al código QR generado.
     *
     * @param dto DTO del activo a enriquecer.
     */
    private void enrichWithImages(AssetsDTO dto) {
        if (dto.getId() == null) return;
        
        // Imágenes Activo (excluyendo QR)
        List<String> perfilImgs = imagenActivoRepository.findByActivoId(dto.getId()).stream()
                .filter(img -> !QR_FILENAME.equals(img.getNombreArchivo()))
                .map(ImagenActivo::getUrlCloudinary)
                .toList();
        dto.setImagenesPerfil(perfilImgs);
    }
    
    /**
     * Convierte una entidad Assets a su DTO correspondiente.
     *
     * @param entity Entidad Assets.
     * @return AssetsDTO con los datos de la entidad.
     */
    private AssetsDTO toDTO(Assets entity) {
        AssetsDTO dto = new AssetsDTO();
        dto.setId(entity.getId());
        dto.setEtiqueta(entity.getEtiqueta());
        dto.setNumeroSerie(entity.getNumeroSerie());
        dto.setIdTipoActivo(entity.getTipoActivo().getId());
        dto.setIdModelo(entity.getModelo().getId());
        dto.setIdEspacio(entity.getEspacio().getId());
        dto.setEstadoCustodia(entity.getEstadoCustodia());
        dto.setEstadoOperativo(entity.getEstadoOperativo());
        dto.setDescripcion(entity.getDescripcion());
        dto.setCosto(entity.getCosto());
        dto.setQrCodigo(entity.getQrCodigo());
        dto.setFechaAlta(entity.getFechaAlta().toString());
        dto.setEsActivo(entity.getEsActivo());
        return dto;
    }

    /**
     * Registra un nuevo activo en el sistema.
     * Valida unicidad de número de serie y existencia de catálogos (tipo, modelo, espacio).
     *
     * @param dto Datos del nuevo activo.
     * @return ApiResponse con el activo creado.
     */
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

    /**
     * Actualiza la información de un activo existente.
     *
     * @param id Identificador del activo.
     * @param dto Nuevos datos a actualizar.
     * @return ApiResponse con el activo actualizado.
     */
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

    /**
     * Realiza una baja lógica del activo (cambia esActivo a false).
     *
     * @param id Identificador del activo.
     * @return ApiResponse confirmando la desactivación.
     */
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

    /** 
     * Resuelve y obtiene (o crea) el Modelo asociado al activo.
     * Si no se proporciona idModelo, busca o crea la Marca y el Modelo basados en los strings del TipoActivo.
     *
     * @param dto DTO del activo con datos de modelo/marca.
     * @param tipo Tipo de activo seleccionado.
     * @return Entidad Modelo resuelta o null si faltan datos.
     */
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
