package mx.edu.utez.modules.reportes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {
    List<Reporte> findByActivoId(Long activoId);
    List<Reporte> findByUsuarioReportaId(Long usuarioId);
    List<Reporte> findByEstadoReporte(String estado);

    // true si existe reporte con estado distinto de Resuelto/Cancelado
    boolean existsByActivoIdAndEstadoReporteNotIn(Long activoId, Collection<String> estadosTerminales);

    // sin fila en MANTENIMIENTO → pendiente de asignar técnico
    @Query("SELECT r FROM Reporte r WHERE NOT EXISTS (SELECT 1 FROM Mantenimiento m WHERE m.reporte.id = r.id)")
    Page<Reporte> findAllSinMantenimiento(Pageable pageable);
}

