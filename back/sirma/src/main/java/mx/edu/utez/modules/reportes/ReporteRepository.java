package mx.edu.utez.modules.reportes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Repositorio de Spring Data JPA. Gestiona las interacciones transaccionales sobre la tabla
 * de reportes. Filtra y recupera listados específicos orientando a procesos de mantenimiento.
 *
 * @author Ithera Team
 */
@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    /** Extrae los registros de incidentes asimilados por un mismo activo en común. */
    List<Reporte> findByActivoId(Long activoId);

    /** Colecciona los documentos originados por un cierto usuario operador/empleado. */
    List<Reporte> findByUsuarioReportaId(Long usuarioId);

    /** Recupera basándose en la configuración o etapa que estén cruzando los tickets. */
    List<Reporte> findByEstadoReporte(String estado);

    // true si existe reporte con estado distinto de Resuelto/Cancelado
    /**
     * Evalúa si un Activo determinado todavía dispone de un reporte en proceso que no haya concluido.
     * @param activoId ID de Asset a testear.
     * @param estadosTerminales Cadenas del estado indicando un final de ciclo.
     * @return <code>true</code> en tal caso de ya haber algo no resuelto.
     */
    boolean existsByActivoIdAndEstadoReporteNotIn(Long activoId, Collection<String> estadosTerminales);

    // sin fila en MANTENIMIENTO  pendiente de asignar t├cnico
    /**
     * Rescata bajo un requerimiento Query DQL sobre la propia base a eventos de reporte que jamás llegaron a la tabla de mantenimientos.
     */
    @Query("SELECT r FROM Reporte r WHERE NOT EXISTS (SELECT 1 FROM Mantenimiento m WHERE m.reporte.id = r.id)")
    Page<Reporte> findAllSinMantenimiento(Pageable pageable);

    /**
     * Mapea datos estadísticos combinados a partir del número de incidencias no resultas ni canceladas,
     * agrupadas por el nombre general y extrae el top 3 para graficados informativos.
     */
    @Query("""
        SELECT 
            a.etiqueta as nombreActivo,
            a.tipoActivo.nombre as tipoActivo, 
            COUNT(r.id) as numReportes
        FROM Reporte r
        JOIN r.activo a
        WHERE r.estadoReporte NOT IN ('Resuelto', 'Cancelado')
        GROUP BY a.id, a.etiqueta, a.tipoActivo.nombre
        ORDER BY numReportes DESC
        LIMIT 3
    """)
    List<ReporteProjection> getReportesStatsGlobally();
}
