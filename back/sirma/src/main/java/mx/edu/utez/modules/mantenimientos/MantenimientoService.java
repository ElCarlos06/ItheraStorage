package mx.edu.utez.modules.mantenimientos;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.assets.Assets;
import mx.edu.utez.modules.assets.AssetsRepository;
import mx.edu.utez.modules.assets.AssetsService;
import mx.edu.utez.modules.bitacora.BitacoraService;
import mx.edu.utez.modules.imagen_mantenimiento.ImagenMantenimiento;
import mx.edu.utez.modules.imagen_mantenimiento.ImagenMantenimientoRepository;
import mx.edu.utez.modules.imagen_mantenimiento.ImagenMantenimientoService;
import mx.edu.utez.modules.mantenimientos.projections.MantenimientoProjection;
import mx.edu.utez.modules.mantenimientos.projections.TiempoPromedioProjection;
import mx.edu.utez.modules.prioridades.Prioridad;
import mx.edu.utez.modules.prioridades.PrioridadRepository;
import mx.edu.utez.modules.reportes.Reporte;
import mx.edu.utez.modules.reportes.ReporteRepository;
import mx.edu.utez.modules.users.User;
import mx.edu.utez.modules.users.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Service
public class MantenimientoService {

    private final ImagenMantenimientoService imagenMantenimientoService;
    private final ImagenMantenimientoRepository imagenMantenimientoRepository;
    private final MantenimientoRepository mantenimientoRepository;
    private final ReporteRepository reporteRepository;
    private final AssetsRepository assetsRepository;
    private final UserRepository userRepository;
    private final PrioridadRepository prioridadRepository;
    private final BitacoraService bitacoraService;
    private final AssetsService assetsService;

    @Transactional(readOnly = true)
    public ApiResponse findAll(Pageable pageable) {
        Page<Mantenimiento> page = mantenimientoRepository.findAll(pageable);
        return new ApiResponse("OK", page, HttpStatus.OK);
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
        if (dto.getObservaciones() != null && !dto.getObservaciones().isBlank()) {
            entity.setObservaciones(dto.getObservaciones());
        }
        entity.setEstadoMantenimiento("Asignado");
        mantenimientoRepository.save(entity);
        Long activoId = activo.get().getId();
        String opAnt = activo.get().getEstadoOperativo();
        assetsRepository.updateEstadoOperativo(activoId, "Mantenimiento");
        assetsService.evictAssetCache(activoId);
        bitacoraService.registrarEvento(activoId, dto.getIdUsuarioAdmin(), "Asignacion Mantenimiento",
                "Mantenimiento asignado a " + tecnico.get().getNombreCompleto(),
                null, null, opAnt, "Mantenimiento");
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
            if ("Finalizado".equals(dto.getEstadoMantenimiento())) {
                entity.setFechaFin(LocalDateTime.now());
                Long activoId = entity.getActivo().getId();
                String opAnt = entity.getActivo().getEstadoOperativo();
                assetsRepository.updateEstadoOperativo(activoId, "OK");
                assetsService.evictAssetCache(activoId);
                bitacoraService.registrarEvento(activoId, null, "Cierre Mantenimiento",
                        "Mantenimiento finalizado: " + (dto.getConclusion() != null ? dto.getConclusion() : "Concluido"),
                        null, null, opAnt, "OK");
            }
        }

        mantenimientoRepository.save(entity);
        return new ApiResponse("Mantenimiento actualizado", entity, HttpStatus.OK);
    }

    /**
     * Elimina un mantenimiento y sus evidencias asociadas.
     */
    @Transactional
    public ApiResponse delete(Long id) {
        Optional<Mantenimiento> found = mantenimientoRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Mantenimiento no encontrado", true, HttpStatus.NOT_FOUND);

        for (ImagenMantenimiento img : imagenMantenimientoRepository.findByMantenimientoId(id))
            imagenMantenimientoService.delete(img.getId());

        mantenimientoRepository.deleteById(id);
        return new ApiResponse("Mantenimiento eliminado", HttpStatus.OK);
    }

    /**
     * Obtiene las estdísticas de los mantenimientos como promedio de duración de los mantenimientos por tipo de este mismo por mes,
     * técnicos con más activos reparados.
     * @return <code>ApiResponse</code> con 2 listas de las stats de los mantenimientos.
     */
    @Transactional(readOnly = true)
    public ApiResponse getMantenimientosStats() {

        LocalDate hoy = LocalDate.now();
        LocalDate inicio = normalizeDate(hoy);

        // Va a tomar la fecha más cercana a el inicio o fin de un semestre
        LocalDate fin = inicio.getMonthValue() == 1
                ? LocalDate.of(hoy.getYear(), Month.JUNE, 30)
                : LocalDate.of(hoy.getYear(), Month.DECEMBER, 31);

        List<TiempoPromedioProjection> timeProjections =
                mantenimientoRepository.findTiempoPromedioPorSemestre(inicio, fin);

        List<MantenimientoProjection> mantenimientoProjections = mantenimientoRepository.findMantenimientosStatsGlobal();

        Map<String, Object> stats = new HashMap<>();
        stats.put("promedioAtencion", timeProjections);
        stats.put("tecnicoMantenimiento", mantenimientoProjections);
        return new ApiResponse("OK", stats, HttpStatus.OK);

    }

    private LocalDate normalizeDate(LocalDate date) {

        if (date.getMonthValue() >= 7)
            return LocalDate.of(date.getYear(), Month.JULY, 1);
        else
            return LocalDate.of(date.getYear(), Month.JANUARY, 1);

    }
}
