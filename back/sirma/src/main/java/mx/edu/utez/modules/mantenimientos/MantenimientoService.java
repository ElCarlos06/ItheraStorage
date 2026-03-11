package mx.edu.utez.modules.mantenimientos;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.assets.Assets;
import mx.edu.utez.modules.assets.AssetsRepository;
import mx.edu.utez.modules.prioridades.Prioridad;
import mx.edu.utez.modules.prioridades.PrioridadRepository;
import mx.edu.utez.modules.reportes.Reporte;
import mx.edu.utez.modules.reportes.ReporteRepository;
import mx.edu.utez.modules.users.User;
import mx.edu.utez.modules.users.UserRepository;
import mx.edu.utez.util.CloudinaryService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MantenimientoService {

    private final MantenimientoRepository mantenimientoRepository;
    private final ReporteRepository reporteRepository;
    private final AssetsRepository assetsRepository;
    private final UserRepository userRepository;
    private final PrioridadRepository prioridadRepository;
    private final ImagenMantenimientoRepository imagenMantenimientoRepository;
    private final CloudinaryService cloudinaryService;

    @Transactional(readOnly = true)
    public ApiResponse findAll() {
        List<Mantenimiento> list = mantenimientoRepository.findAll();
        return new ApiResponse("OK", list, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ApiResponse findById(Long id) {
        Optional<Mantenimiento> found = mantenimientoRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Mantenimiento no encontrado", true, HttpStatus.NOT_FOUND);
        return new ApiResponse("OK", found.get(), HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ApiResponse findByTecnico(Long tecnicoId) {
        List<Mantenimiento> list = mantenimientoRepository.findByUsuarioTecnicoId(tecnicoId);
        return new ApiResponse("OK", list, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ApiResponse findByActivo(Long activoId) {
        List<Mantenimiento> list = mantenimientoRepository.findByActivoId(activoId);
        return new ApiResponse("OK", list, HttpStatus.OK);
    }

    @Transactional
    public ApiResponse save(MantenimientoDTO dto) {
        Optional<Reporte> reporte = reporteRepository.findById(dto.getIdReporte());
        if (reporte.isEmpty())
            return new ApiResponse("Reporte no encontrado", true, HttpStatus.NOT_FOUND);
        if (mantenimientoRepository.findByReporteId(dto.getIdReporte()).isPresent())
            return new ApiResponse("Ya existe un mantenimiento para ese reporte", true, HttpStatus.CONFLICT);
        Optional<Assets> activo = assetsRepository.findById(dto.getIdActivo());
        if (activo.isEmpty())
            return new ApiResponse("Activo no encontrado", true, HttpStatus.NOT_FOUND);
        Optional<User> tecnico = userRepository.findById(dto.getIdUsuarioTecnico());
        if (tecnico.isEmpty())
            return new ApiResponse("Técnico no encontrado", true, HttpStatus.NOT_FOUND);
        Optional<User> admin = userRepository.findById(dto.getIdUsuarioAdmin());
        if (admin.isEmpty())
            return new ApiResponse("Administrador no encontrado", true, HttpStatus.NOT_FOUND);
        Optional<Prioridad> prioridad = prioridadRepository.findById(dto.getIdPrioridad());
        if (prioridad.isEmpty())
            return new ApiResponse("Prioridad no encontrada", true, HttpStatus.NOT_FOUND);

        Mantenimiento entity = new Mantenimiento();
        entity.setReporte(reporte.get());
        entity.setActivo(activo.get());
        entity.setUsuarioTecnico(tecnico.get());
        entity.setUsuarioAdmin(admin.get());
        entity.setPrioridad(prioridad.get());
        entity.setTipoAsignado(dto.getTipoAsignado());
        entity.setEstadoMantenimiento("Asignado");
        mantenimientoRepository.save(entity);
        return new ApiResponse("Mantenimiento registrado", entity, HttpStatus.CREATED);
    }

    @Transactional
    public ApiResponse update(Long id, MantenimientoDTO dto) {
        Optional<Mantenimiento> found = mantenimientoRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Mantenimiento no encontrado", true, HttpStatus.NOT_FOUND);
        Mantenimiento entity = found.get();

        if (dto.getTipoEjecutado() != null) entity.setTipoEjecutado(dto.getTipoEjecutado());
        if (dto.getDiagnostico() != null) entity.setDiagnostico(dto.getDiagnostico());
        if (dto.getAccionesRealizadas() != null) entity.setAccionesRealizadas(dto.getAccionesRealizadas());
        if (dto.getPiezasUtilizadas() != null) entity.setPiezasUtilizadas(dto.getPiezasUtilizadas());
        if (dto.getConclusion() != null) entity.setConclusion(dto.getConclusion());
        if (dto.getObservaciones() != null) entity.setObservaciones(dto.getObservaciones());
        if (dto.getCosto() != null) entity.setCosto(dto.getCosto());

        if (dto.getEstadoMantenimiento() != null) {
            entity.setEstadoMantenimiento(dto.getEstadoMantenimiento());
            if ("En Proceso".equals(dto.getEstadoMantenimiento()))
                entity.setFechaInicio(LocalDateTime.now());
            if ("Finalizado".equals(dto.getEstadoMantenimiento()))
                entity.setFechaFin(LocalDateTime.now());
        }

        mantenimientoRepository.save(entity);
        return new ApiResponse("Mantenimiento actualizado", entity, HttpStatus.OK);
    }

    // ────────── IMÁGENES ──────────

    private static final String CARPETA_CLOUDINARY = "sirma/mantenimientos";

    @Transactional
    public ApiResponse subirImagen(Long mantenimientoId, MultipartFile file) {
        Optional<Mantenimiento> found = mantenimientoRepository.findById(mantenimientoId);
        if (found.isEmpty())
            return new ApiResponse("Mantenimiento no encontrado", true, HttpStatus.NOT_FOUND);
        try {
            Map<String, Object> resultado = cloudinaryService.upload(file, CARPETA_CLOUDINARY);

            ImagenMantenimiento img = new ImagenMantenimiento();
            img.setMantenimiento(found.get());
            img.setUrlCloudinary((String) resultado.get("secure_url"));
            img.setPublicIdCloudinary((String) resultado.get("public_id"));
            img.setNombreArchivo(file.getOriginalFilename());
            imagenMantenimientoRepository.save(img);

            return new ApiResponse("Imagen subida correctamente", img, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ApiResponse("Error al subir imagen: " + e.getMessage(), true, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse listarImagenes(Long mantenimientoId) {
        List<ImagenMantenimiento> lista = imagenMantenimientoRepository.findByMantenimientoId(mantenimientoId);
        return new ApiResponse("OK", lista, HttpStatus.OK);
    }

    @Transactional
    public ApiResponse eliminarImagen(Long imagenId) {
        Optional<ImagenMantenimiento> found = imagenMantenimientoRepository.findById(imagenId);
        if (found.isEmpty())
            return new ApiResponse("Imagen no encontrada", true, HttpStatus.NOT_FOUND);
        try {
            cloudinaryService.delete(found.get().getPublicIdCloudinary());
            imagenMantenimientoRepository.delete(found.get());
            return new ApiResponse("Imagen eliminada correctamente", HttpStatus.OK);
        } catch (IOException e) {
            return new ApiResponse("Error al eliminar imagen: " + e.getMessage(), true, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

