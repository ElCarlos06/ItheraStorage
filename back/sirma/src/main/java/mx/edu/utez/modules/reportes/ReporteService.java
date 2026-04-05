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

/**
 * Lógica principal del módulo de incidentes y problemas operativos que asocia un Activo con
 * requerimientos de Mantenimiento y reajusta automáticamente los estados funcionales entre componentes.
 *
 * @author Ithera Team
 */
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
    /**
     * Entregará la página conteniendo un listado variable dependiendo para qué cliente vaya a servir
     * enfocado en separar por los reportes sueltos que el administrador pueda asignar.
     *
     * @param pageable Preferencias de datos.
     * @param sinAsignar Bandeja controlada que limita los reportes puros sin técnico asignado.
     * @return Paginación de <code>Reporte</code>.
     */
    @Transactional(readOnly = true)
    public ApiResponse findAll(Pageable pageable, boolean sinAsignar) {
        Page<Reporte> page = sinAsignar
                ? reporteRepository.findAllSinMantenimiento(pageable)
                : reporteRepository.findAll(pageable);
        page.getContent().forEach(this::enrichNombreTecnicoAsignado);
        return new ApiResponse("OK", page, HttpStatus.OK);
    }

    /**
     * Auxiliar interno que embebe forzosamente el nombre del técnico de soporte sobre
     * una propiedad Transiente para fácil renderizado.
     * @param r Objeto sobre el que rellenar el dato calculándolo inversamente desde el mantenimiento.
     */
    private void enrichNombreTecnicoAsignado(Reporte r) {
        if (r == null || r.getId() == null) return;
        mantenimientoRepository
                .findByReporteId(r.getId())
                .map(Mantenimiento::getUsuarioTecnico)
                .map(this::nombreVisibleParaTecnico)
                .ifPresent(r::setNombreTecnicoAsignado);
    }

    /**
     * Fallback por predeterminado que evalúa el nombre de quién fue la persona encomendada.
     * @param t Usuario que servirá al mantenimiento en caso de existir.
     * @return Cadena tratada.
     */
    private String nombreVisibleParaTecnico(User t) {
        String nombre = t.getNombreCompleto();
        if (nombre != null && !nombre.isBlank()) return nombre;
        return t.getCorreo() != null ? t.getCorreo() : "—";
    }

    /**
     * Búsqueda por Id de una incidencia individual enriquecida.
     *
     * @param id Clavé subyacente.
     * @return El reporte con un HTTP Status dictaminando si fue hallado.
     */
    @Transactional(readOnly = true)
    public ApiResponse findById(Long id) {
        Optional<Reporte> found = reporteRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Reporte no encontrado", true, HttpStatus.NOT_FOUND);
        Reporte r = found.get();
        enrichNombreTecnicoAsignado(r);
        return new ApiResponse("OK", r, HttpStatus.OK);
    }

    /**
     * Listado específico por agrupación natural de cada Activo Fijo.
     *
     * @param activoId Entero referencial de bien.
     * @return Una colección <code>List</code> con historiales reportados.
     */
    @Transactional(readOnly = true)
    public ApiResponse findByActivo(Long activoId) {
        List<Reporte> list = reporteRepository.findByActivoId(activoId);
        list.forEach(this::enrichNombreTecnicoAsignado);
        return new ApiResponse("OK", list, HttpStatus.OK);
    }

    /**
     * Verifica restricciones operativas del Activo Físico antes de instanciar la solicitud pertinente.
     * Configura el tipo de urgencia (Prioridad), alerta inconsistencias de los catálogos y reconfigura
     * el activo central bajo un escenario de estatus alterado sumando la correspondiente bitácora.
     *
     * @param dto Archivo base con la validación de datos integrales.
     * @return Almacenaje concretado empaquetado de cara asincrónica.
     */
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

        a.setEstadoOperativo("Reportado");

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

    /**
     * Aplica la sobreescritura a ciertos componentes actualizables de un reporte en función de la dinámica.
     *
     * @param id Modificándose base a Id de persistencia.
     * @param dto Información validada cruzante.
     * @return Concreción si todo estuvo acorde a BD o mensaje de errores pre-validando.
     */
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

    /**
     * Transacción de deshecho que de cancelar o eliminar por algún motivo este proceso, deshace las huellas
     * del posible impacto generado en reparaciones de la mesa, limpiezas fotográficas locales y reestabelece
     * la normalidad original hacia el activo emitiendo la bitácora conclusiva correspondiente.
     *
     * @param id Índice extirpable que engloba su cadena de mantenimientos.
     * @return Resultado.
     */
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

    /**
     * Llama recursivamente a los extractores de la BD que recaban por nosotros y grafican o agrupan los datos.
     * @return <code>ApiResponse</code> con las estadísticas de los reportes.
     */
    @Transactional(readOnly = true)
    public ApiResponse getReporteStats() {

        List<ReporteProjection> stats = reporteRepository.getReportesStatsGlobally();
        return new ApiResponse("Estadísticas juntadas con éxito", stats, HttpStatus.OK);

    }
}
