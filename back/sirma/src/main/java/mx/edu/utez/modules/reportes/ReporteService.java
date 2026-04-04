package mx.edu.utez.modules.reportes;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.assets.AssetEstadoHelper;
import mx.edu.utez.modules.assets.AssetEstados;
import mx.edu.utez.modules.assets.Assets;
import mx.edu.utez.modules.assets.AssetsRepository;
import mx.edu.utez.modules.assets.AssetsService;
import mx.edu.utez.modules.bitacora.BitacoraService;
import mx.edu.utez.modules.imagen_reporte.ImagenReporte;
import mx.edu.utez.modules.imagen_reporte.ImagenReporteRepository;
import mx.edu.utez.modules.imagen_reporte.ImagenReporteService;
import mx.edu.utez.modules.mantenimientos.Mantenimiento;
import mx.edu.utez.modules.mantenimientos.MantenimientoRepository;
import mx.edu.utez.modules.mantenimientos.MantenimientoService;
import mx.edu.utez.modules.prioridades.Prioridad;
import mx.edu.utez.modules.prioridades.PrioridadRepository;
import mx.edu.utez.modules.resguardos.ResguardoRepository;
import mx.edu.utez.modules.tipo_fallas.TipoFalla;
import mx.edu.utez.modules.tipo_fallas.TipoFallaRepository;
import mx.edu.utez.modules.users.User;
import mx.edu.utez.modules.users.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ReporteService {

    // Resuelto o Cancelado: ya se puede abrir otro reporte del mismo activo
    private static final List<String> REPORTE_TERMINALES = List.of("Resuelto", "Cancelado");

    private final ReporteRepository reporteRepository;
    private final AssetsRepository assetsRepository;
    private final AssetsService assetsService;
    private final BitacoraService bitacoraService;
    private final UserRepository userRepository;
    private final TipoFallaRepository tipoFallaRepository;
    private final PrioridadRepository prioridadRepository;
    private final MantenimientoRepository mantenimientoRepository;
    private final MantenimientoService mantenimientoService;
    private final ResguardoRepository resguardoRepository;
    private final ImagenReporteRepository imagenReporteRepository;
    private final ImagenReporteService imagenReporteService;

    // sinAsignar=true: bandeja admin sin técnico; false: todos (ej. móvil)
    @Transactional(readOnly = true)
    public ApiResponse findAll(Pageable pageable, boolean sinAsignar) {
        Page<Reporte> page = sinAsignar
                ? reporteRepository.findAllSinMantenimiento(pageable)
                : reporteRepository.findAll(pageable);
        page.getContent().forEach(this::enrichNombreTecnicoAsignado);
        return new ApiResponse("OK", page, HttpStatus.OK);
    }

    private void enrichNombreTecnicoAsignado(Reporte r) {
        if (r == null || r.getId() == null) return;
        mantenimientoRepository
                .findByReporteId(r.getId())
                .map(Mantenimiento::getUsuarioTecnico)
                .map(this::nombreVisibleParaTecnico)
                .ifPresent(r::setNombreTecnicoAsignado);
    }

    private String nombreVisibleParaTecnico(User t) {
        String nombre = t.getNombreCompleto();
        if (nombre != null && !nombre.isBlank()) return nombre;
        return t.getCorreo() != null ? t.getCorreo() : "—";
    }

    @Transactional(readOnly = true)
    public ApiResponse findById(Long id) {
        Optional<Reporte> found = reporteRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Reporte no encontrado", true, HttpStatus.NOT_FOUND);
        Reporte r = found.get();
        enrichNombreTecnicoAsignado(r);
        return new ApiResponse("OK", r, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ApiResponse findByActivo(Long activoId) {
        List<Reporte> list = reporteRepository.findByActivoId(activoId);
        list.forEach(this::enrichNombreTecnicoAsignado);
        return new ApiResponse("OK", list, HttpStatus.OK);
    }

    @Transactional
    public ApiResponse save(ReporteDTO dto) {
        Optional<Assets> activo = assetsRepository.findById(dto.getIdActivo());
        if (activo.isEmpty())
            return new ApiResponse("Activo no encontrado", true, HttpStatus.NOT_FOUND);
        Assets a = activo.get();
        if (Boolean.FALSE.equals(a.getEsActivo()))
            return new ApiResponse("No se pueden reportar activos dados de baja", true, HttpStatus.BAD_REQUEST);
        if (AssetEstados.OPERATIVO_BAJA.equalsIgnoreCase(a.getEstadoOperativo()))
            return new ApiResponse("No se pueden reportar activos dados de baja", true, HttpStatus.BAD_REQUEST);
        if (!resguardoRepository.existsByUsuarioEmpleado_IdAndActivo_IdAndEstadoResguardo(
                dto.getIdUsuarioReporta(), a.getId(), "Confirmado"))
            return new ApiResponse("Solo puedes reportar activos bajo tu resguardo confirmado", true, HttpStatus.BAD_REQUEST);
        if (reporteRepository.existsByActivoIdAndEstadoReporteNotIn(a.getId(), REPORTE_TERMINALES))
            return new ApiResponse("Ya existe un reporte abierto para este activo", true, HttpStatus.CONFLICT);
        Optional<User> usuario = userRepository.findById(dto.getIdUsuarioReporta());
        if (usuario.isEmpty())
            return new ApiResponse("Usuario no encontrado", true, HttpStatus.NOT_FOUND);
        Optional<TipoFalla> tipoFalla = tipoFallaRepository.findById(dto.getIdTipoFalla());
        if (tipoFalla.isEmpty())
            return new ApiResponse("Tipo de falla no encontrado", true, HttpStatus.NOT_FOUND);
        Optional<Prioridad> prioridad = prioridadRepository.findById(dto.getIdPrioridad());
        if (prioridad.isEmpty())
            return new ApiResponse("Prioridad no encontrada", true, HttpStatus.NOT_FOUND);

        Reporte entity = new Reporte();
        entity.setActivo(a);
        entity.setUsuarioReporta(usuario.get());
        entity.setTipoFalla(tipoFalla.get());
        entity.setPrioridad(prioridad.get());
        entity.setDescripcionFalla(dto.getDescripcionFalla());
        entity.setEstadoReporte("Pendiente");
        reporteRepository.save(entity);

        AssetEstadoHelper.advertirSiCombinacionInusual(a);
        Long activoId = a.getId();
        String opAnt = a.getEstadoOperativo();
        String cust = a.getEstadoCustodia();
        assetsRepository.updateEstadoOperativo(activoId, AssetEstados.OPERATIVO_REPORTADO);
        assetsService.evictAssetCache(activoId);
        String desc = dto.getDescripcionFalla();
        String descCorta = desc != null && desc.length() > 100 ? desc.substring(0, 100) + "…" : (desc != null ? desc : "Daño reportado");
        bitacoraService.registrarEvento(activoId, dto.getIdUsuarioReporta(), "Reporte Daño",
                "Reporte: " + descCorta + " (custodia=" + cust + "; operativo→Reportado)",
                cust, cust, opAnt, AssetEstados.OPERATIVO_REPORTADO);

        return new ApiResponse("Reporte enviado correctamente", entity, HttpStatus.CREATED);
    }

    @Transactional
    public ApiResponse update(Long id, ReporteDTO dto) {
        Optional<Reporte> found = reporteRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Reporte no encontrado", true, HttpStatus.NOT_FOUND);
        Reporte entity = found.get();
        if (dto.getDescripcionFalla() != null) entity.setDescripcionFalla(dto.getDescripcionFalla());
        if (dto.getEstadoReporte() != null) entity.setEstadoReporte(dto.getEstadoReporte());
        if (dto.getIdTipoFalla() != null) {
            Optional<TipoFalla> tf = tipoFallaRepository.findById(dto.getIdTipoFalla());
            tf.ifPresent(entity::setTipoFalla);
        }
        if (dto.getIdPrioridad() != null) {
            Optional<Prioridad> p = prioridadRepository.findById(dto.getIdPrioridad());
            p.ifPresent(entity::setPrioridad);
        }
        reporteRepository.save(entity);
        return new ApiResponse("Reporte actualizado", entity, HttpStatus.OK);
    }

    @Transactional
    public ApiResponse delete(Long id) {
        Optional<Reporte> found = reporteRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Reporte no encontrado", true, HttpStatus.NOT_FOUND);
        Reporte r = found.get();
        Long activoId = r.getActivo().getId();
        String opAnt = r.getActivo().getEstadoOperativo();
        String cust = r.getActivo().getEstadoCustodia();

        mantenimientoRepository.findByReporteId(id)
                .ifPresent(m -> mantenimientoService.deleteForCascadeEliminarReporte(m.getId()));
        for (ImagenReporte img : imagenReporteRepository.findByReporteId(id)) {
            imagenReporteService.delete(img.getId());
        }
        reporteRepository.deleteById(id);

        assetsRepository.updateEstadoOperativo(activoId, AssetEstados.OPERATIVO_OK);
        assetsService.evictAssetCache(activoId);
        bitacoraService.registrarEvento(activoId, null, "Eliminación reporte",
                "Reporte de daño eliminado; operativo→OK (custodia=" + cust + " sin cambio)",
                cust, cust, opAnt, AssetEstados.OPERATIVO_OK);
        return new ApiResponse("Reporte eliminado", HttpStatus.OK);
    }
}
