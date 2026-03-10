package mx.edu.utez.modules.reportes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagenReporteRepository extends JpaRepository<ImagenReporte, Long> {
    List<ImagenReporte> findByReporteId(Long reporteId);
}

