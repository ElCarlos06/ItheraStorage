package mx.edu.utez.modules.reportes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {
    List<Reporte> findByActivoId(Long activoId);
    List<Reporte> findByUsuarioReportaId(Long usuarioId);
    List<Reporte> findByEstadoReporte(String estado);
}

