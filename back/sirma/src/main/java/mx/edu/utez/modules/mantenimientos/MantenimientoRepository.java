package mx.edu.utez.modules.mantenimientos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Long> {
    @Query("SELECT m FROM Mantenimiento m WHERE m.reporte.id = :reporteId")
    Optional<Mantenimiento> findByReporteId(@Param("reporteId") Long reporteId);
    List<Mantenimiento> findByActivoId(Long activoId);
    List<Mantenimiento> findByUsuarioTecnicoId(Long tecnicoId);
    List<Mantenimiento> findByEstadoMantenimiento(String estado);
}

