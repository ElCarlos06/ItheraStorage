package mx.edu.utez.modules.mantenimientos;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.assets.AssetEstadoHelper;
import mx.edu.utez.modules.assets.AssetEstados;
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

/**
 * Núcleo del servicio de administraciones en el proceso final para los incidentes en un activo a tratar.
 * Modifica las integridades de entidades afines y cierra actas.
 *
 * @author Ithera Team
 */
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

    /**
     * Muestra todo el catálogo o historial general por defecto paginado.
     * @param pageable Contexto.
     * @param excluirAsignado Si true, omite los que están en estado 'Asignado' (sin diagnóstico aún).
     * @return Formato de listado genérico de respuesta.
     */
    @Transactional(readOnly = true)
    public ApiResponse findAll(Pageable pageable, boolean excluirAsignado) {
        Page<Mantenimiento> page = excluirAsignado
                ? mantenimientoRepository.findByEstadoMantenimientoNot("Asignado", pageable)
                : mantenimientoRepository.findAll(pageable);
        return new ApiResponse("OK", page, HttpStatus.OK);
    }

    /**
     * Extrae con sumo detalle una actividad o sesión en mantenimiento desde su ID principal.
     * @return El DTO procesado o falla de consulta.
     */
    @Transactional(readOnly = true)
    public ApiResponse findById(Long id) {
        Optional<Mantenimiento> found = mantenimientoRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Mantenimiento no encontrado", true, HttpStatus.NOT_FOUND);
        return new ApiResponse("OK", found.get(), HttpStatus.OK);
    }

    /**
     * Descarga la lista personal de órdenes en cola para este técnico responsable.
     * @param tecnicoId Empleado TI u operador asociado.
     * @return Arreglo de sus misiones activas y precluidas.
     */
    @Transactional(readOnly = true)
    public ApiResponse findByTecnico(Long tecnicoId) {
        List<Mantenimiento> list = mantenimientoRepository.findByUsuarioTecnicoId(tecnicoId);
        return new ApiResponse("OK", list, HttpStatus.OK);
    }

    /**
     * Recoge los arreglos de historial sobre las composturas completadas al activo pasado.
     * @param activoId Entidad Fk.
     * @return Retorna una lista con la recolección.
     */
    @Transactional(readOnly = true)
    public ApiResponse findByActivo(Long activoId) {
        List<Mantenimiento> list = mantenimientoRepository.findByActivoId(activoId);
        return new ApiResponse("OK", list, HttpStatus.OK);
    }

    /**
     * Levanta un Mantenimiento transaccionalmente, lo encadena al reporte precursor instanciándolo e imposibilitando reportes dobles, asegurando la existencia en las referencias paramétricas base del usuario y modificando las fases operativas a "En mantenimiento".
     * Culmina emitiendo su bitácora.
     * @param dto Carga original del requerimiento con datos en validación.
     * @return Retorna y confirma la instanciación persistente con ID asignado.
     */
    @Transactional
    public ApiResponse save(MantenimientoDTO dto) {

        Optional<Reporte> reporte = reporteRepository.findById(dto.getIdReporte());
        if (reporte.isEmpty())
            return new ApiResponse("Reporte no encontrado", true, HttpStatus.NOT_FOUND);

        Reporte rep = reporte.get();
        if (!rep.getActivo().getId().equals(dto.getIdActivo()))
            return new ApiResponse("El reporte no corresponde al activo indicado", true, HttpStatus.BAD_REQUEST);

        if (mantenimientoRepository.findByReporteId(dto.getIdReporte()).isPresent())
            return new ApiResponse("Ya existe un mantenimiento para ese reporte", true, HttpStatus.CONFLICT);

        Optional<Assets> activo = assetsRepository.findById(dto.getIdActivo());
        if (activo.isEmpty())
            return new ApiResponse("Activo no encontrado", true, HttpStatus.NOT_FOUND);

        AssetEstadoHelper.advertirSiCombinacionInusual(activo.get());
        if (!AssetEstadoHelper.puedeAsignarMantenimiento(activo.get()))
            return new ApiResponse(
                    "Solo se puede asignar mantenimiento con el activo en estado operativo Reportado (sin baja)",
                    true,
                    HttpStatus.BAD_REQUEST);

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

        entity.setReporte(rep);
        entity.setActivo(activo.get());
        entity.setUsuarioTecnico(tecnico.get());
        entity.setUsuarioAdmin(admin.get());
        entity.setPrioridad(prioridad.get());
        entity.setTipoAsignado(dto.getTipoAsignado());

        if (dto.getObservaciones() != null && !dto.getObservaciones().isBlank())
            entity.setObservaciones(dto.getObservaciones());

        entity.setEstadoMantenimiento("Asignado");
        entity.setFechaInicio(LocalDateTime.now());

        mantenimientoRepository.save(entity);

        rep.setEstadoReporte("En Proceso");
        reporteRepository.save(rep);

        Long activoId = activo.get().getId();
        String opAnt = activo.get().getEstadoOperativo();
        String cust = activo.get().getEstadoCustodia();

        // El activo permanece en "Reportado"; cambia a "Mantenimiento" cuando el técnico inicie atención.
        assetsService.evictAssetCache(activoId);
        bitacoraService.registrarEvento(activoId, dto.getIdUsuarioAdmin(), "Asignacion Mantenimiento",
                "Técnico asignado: " + tecnico.get().getNombreCompleto() + " (pendiente de atención)",
                cust, cust, opAnt, opAnt);

        return new ApiResponse("Mantenimiento asignado exitosamente", entity, HttpStatus.CREATED);
    }

    /**
     * Gestiona las fases subsecuentes a la asimilación del trámite.
     * Si la constante señala el mantenimiento como 'Finalizado', el núcleo se decanta y cierra el ticket de este trabajo liberando al Activo en base a su conclusión, disparando métricas hacia la bitácora informando de un 'Irreparable' o su contraparte.
     *
     * @param id Principal que será alterado en el nivel de campo.
     * @param dto Nivel cruzado y validador de inserciones extras e inferencias diagnósticas.
     * @return Actualización conteniendo los veredictos.
     */
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
            if ("En Proceso".equals(dto.getEstadoMantenimiento())) {
                entity.setFechaInicio(LocalDateTime.now());
                // El técnico inicia la atención: ahora sí el activo pasa a "Mantenimiento"
                Long activoIdEp = entity.getActivo().getId();
                String opAntEp  = entity.getActivo().getEstadoOperativo();
                String custEp   = entity.getActivo().getEstadoCustodia();
                assetsRepository.updateEstadoOperativo(activoIdEp, AssetEstados.OPERATIVO_MANTENIMIENTO);
                assetsService.evictAssetCache(activoIdEp);
                bitacoraService.registrarEvento(activoIdEp, null, "Inicio Mantenimiento",
                        "El técnico " + entity.getUsuarioTecnico().getNombreCompleto() + " inició la atención",
                        custEp, custEp, opAntEp, AssetEstados.OPERATIVO_MANTENIMIENTO);
            }
            if ("Finalizado".equals(dto.getEstadoMantenimiento())) {
                entity.setFechaFin(LocalDateTime.now());
                Long activoId = entity.getActivo().getId();
                String opAnt = entity.getActivo().getEstadoOperativo();
                String cust = entity.getActivo().getEstadoCustodia();
                String conclusion = dto.getConclusion() != null ? dto.getConclusion() : entity.getConclusion();
                String nuevoOp = AssetEstadoHelper.operativoTrasCierreMantenimiento(conclusion);
                assetsRepository.updateEstadoOperativo(activoId, nuevoOp);
                assetsService.evictAssetCache(activoId);
                String msg = AssetEstadoHelper.esConclusionIrreparable(conclusion)
                        ? "Mantenimiento cerrado como Irreparable; activo permanece en Mantenimiento hasta baja aprobada"
                        : "Mantenimiento finalizado: " + (conclusion != null ? conclusion : "Concluido");
                bitacoraService.registrarEvento(activoId, null, "Cierre Mantenimiento",
                        msg,
                        cust, cust, opAnt, nuevoOp);
            }
        }

        mantenimientoRepository.save(entity);
        return new ApiResponse("Mantenimiento actualizado", entity, HttpStatus.OK);
    }

    /**
     * Realiza un despido y descargo total del servicio de forma agresiva.
     * Desliga permanentemente el mantenimiento, anula su relación eliminando sus reportes visuales o imágenes dependientes y reestablece las operaciones activadas hacia la simple 'Reportada'. Transaccionalmente blindado.
     *
     * @param id Llave objetivo borrada.
     * @return Respuesta satisfactoria al despido del elemento.
     */
    @Transactional
    public ApiResponse delete(Long id) {
        Optional<Mantenimiento> found = mantenimientoRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Mantenimiento no encontrado", true, HttpStatus.NOT_FOUND);

        Mantenimiento entity = found.get();
        Long reporteId = entity.getReporte().getId();
        Long activoId = entity.getActivo().getId();
        String opAnt = entity.getActivo().getEstadoOperativo();
        String cust = entity.getActivo().getEstadoCustodia();

        for (ImagenMantenimiento img : imagenMantenimientoRepository.findByMantenimientoId(id))
            imagenMantenimientoService.delete(img.getId());

        String estadoMtn = entity.getEstadoMantenimiento();
        mantenimientoRepository.deleteById(id);

        reporteRepository.findById(reporteId).ifPresent(rep -> {
            rep.setEstadoReporte("Pendiente");
            reporteRepository.save(rep);
        });

        // Si el técnico ya había iniciado ("En Proceso") el activo estaba en "Mantenimiento"; hay que revertirlo.
        // Si era "Asignado", el activo nunca cambió de "Reportado", así que solo limpiamos caché.
        if (!"Asignado".equals(estadoMtn)) {
            assetsRepository.updateEstadoOperativo(activoId, AssetEstados.OPERATIVO_REPORTADO);
        }
        assetsService.evictAssetCache(activoId);
        bitacoraService.registrarEvento(activoId, null, "Eliminación mantenimiento",
                "Se eliminó la asignación de técnico; el reporte vuelve a la bandeja sin asignar.",
                cust, cust, opAnt, AssetEstados.OPERATIVO_REPORTADO);
        return new ApiResponse("Mantenimiento eliminado", HttpStatus.OK);
    }

    // solo BD + im├genes; ReporteService borra reporte y ajusta activo despu├s
    /**
     * Subservicio de eliminación profunda interna invocado directamente cuando un Reporte completo es destruido y pide despido.
     * Recolecta las imágenes subida y el mantenimiento principal al que dio nacimiento vaciándolo de la BD sin disparar avisos extras.
     * @param id ID clave provenida del llamado parent.
     */
    @Transactional
    public void deleteForCascadeEliminarReporte(Long id) {
        Optional<Mantenimiento> found = mantenimientoRepository.findById(id);
        if (found.isEmpty()) return;
        for (ImagenMantenimiento img : imagenMantenimientoRepository.findByMantenimientoId(id))
            imagenMantenimientoService.delete(img.getId());
        mantenimientoRepository.deleteById(id);
    }

    /**
     * Agrupa y calcula indicadores de desempeño para los operadores de las composturas con fines estadísticos analíticos y proyección para vistas Front-End limitándolas hacia el contexto semestral actual.
     * @return Colección Json o Map conteniendo Top técnicos y tiempos métricos.
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

    /**
     * Proporciona la fecha de arranque inicial de un semestre para parametrizar las queries de fechas.
     * @param date Una referencia LocalDate.
     * @return Enero (01-01) o Julio (07-01).
     */
    private LocalDate normalizeDate(LocalDate date) {

        if (date.getMonthValue() >= 7)
            return LocalDate.of(date.getYear(), Month.JULY, 1);
        else
            return LocalDate.of(date.getYear(), Month.JANUARY, 1);

    }
}
