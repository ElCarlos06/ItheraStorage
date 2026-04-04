package mx.edu.utez.modules.mantenimientos;

import mx.edu.utez.modules.mantenimientos.projections.MantenimientoProjection;
import mx.edu.utez.modules.mantenimientos.projections.TiempoPromedioProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Long> {
    @Query("SELECT m FROM Mantenimiento m WHERE m.reporte.id = :reporteId")
    Optional<Mantenimiento> findByReporteId(@Param("reporteId") Long reporteId);
    List<Mantenimiento> findByActivoId(Long activoId);
    List<Mantenimiento> findByUsuarioTecnicoId(Long tecnicoId);
    List<Mantenimiento> findByEstadoMantenimiento(String estado);

    @Query("""
        SELECT m.usuarioTecnico.nombreCompleto AS tecnico, COUNT(m) AS numMantenimientos
        FROM Mantenimiento m
        WHERE m.fechaFin IS NOT NULL
        GROUP BY m.usuarioTecnico.id, m.usuarioTecnico.nombreCompleto
        ORDER BY COUNT(m) DESC
        LIMIT 4
    """)
    List<MantenimientoProjection> findMantenimientosStatsGlobal();

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

