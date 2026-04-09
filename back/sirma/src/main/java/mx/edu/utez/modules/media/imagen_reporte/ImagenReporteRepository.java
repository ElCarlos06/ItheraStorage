package mx.edu.utez.modules.media.imagen_reporte;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagenReporteRepository extends JpaRepository<ImagenReporte, Long> {
    List<ImagenReporte> findByReporteId(Long reporteId);

    // Buscar imágenes de reportes asociados a un activo
    List<ImagenReporte> findByReporteActivoId(Long activoId);
}
