package mx.edu.utez.modules.mantenimientos;

import mx.edu.utez.modules.mantenimientos.projections.MantenimientoProjection;
import mx.edu.utez.modules.mantenimientos.projections.TiempoPromedioProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interface Repository de JPA. Desglosa los servicios CRUD e implementa queries transaccionales
 * requeridas para consultar las reparaciones o mantenimientos y recolectar sus métricas estadísticas.
 *
 * @author Ithera Team
 */
@Repository
public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Long> {

    /**
     * Localiza a un mantenimiento asegurando la dependencia sobre un respectivo Reporte madre.
     * @param reporteId Identificación del origen del trámite.
     * @return El trámite envuelto o su ausencia.
     */
    @Query("SELECT m FROM Mantenimiento m WHERE m.reporte.id = :reporteId")
    Optional<Mantenimiento> findByReporteId(@Param("reporteId") Long reporteId);

    /** Descarga o extrae todo el compendio de atenciones que el activo específico ha sufrido. */
    List<Mantenimiento> findByActivoId(Long activoId);

    /** Facilita el acceso a la bandeja de mantenimientos responsabilizada a un único operador de soporte. */
    List<Mantenimiento> findByUsuarioTecnicoId(Long tecnicoId);

    /** Colecciona todos los mantenimientos categorizados al estado parametrizado pasado. */
    List<Mantenimiento> findByEstadoMantenimiento(String estado);

    /**
     * Lista paginada de mantenimientos excluyendo el estado indicado.
     * Se usa para la bandeja de administrador: oculta los 'Asignado' (aún sin diagnóstico)
     * y solo muestra los que el técnico ya inició ('En Proceso', 'Finalizado', etc.).
     */
    Page<Mantenimiento> findByEstadoMantenimientoNot(String estado, Pageable pageable);

    /**
     * Calcula a los top 4 de técnicos basados sobre el total o número descenciente de tickets que exitosamente ellos hayan logrado concluir.
     */
    @Query("""
        SELECT m.usuarioTecnico.nombreCompleto AS tecnico, COUNT(m) AS numMantenimientos
        FROM Mantenimiento m
        WHERE m.fechaFin IS NOT NULL
        GROUP BY m.usuarioTecnico.id, m.usuarioTecnico.nombreCompleto
        ORDER BY COUNT(m) DESC
        LIMIT 4
    """)
    List<MantenimientoProjection> findMantenimientosStatsGlobal();

    /**
     * Arroja un desglose mes por mes en un lapso determinado estimando y cuantificando las horas promediadas por atención finalizada,
     * dividiendo por el tipo de trabajo (Preventivo/Correctivo).
     */
    @Query(value = """
        SELECT 
            MONTHNAME(m.fecha_inicio) AS mes,
            m.tipo_asignado AS tipoMantenimiento,
            AVG(TIMESTAMPDIFF(HOUR, m.fecha_inicio, m.fecha_fin)) AS promedioHoras
        FROM mantenimiento m
        WHERE m.fecha_fin IS NOT NULL
            AND m.fecha_inicio BETWEEN :start AND :end
        GROUP BY MONTH(m.fecha_inicio), MONTHNAME(m.fecha_inicio), m.tipo_asignado
        ORDER BY MONTH(m.fecha_inicio)
    """,
    nativeQuery = true)
    List<TiempoPromedioProjection> findTiempoPromedioPorSemestre(
            LocalDate start,
            LocalDate end
    );

}

