package mx.edu.utez.modules.imagen_mantenimiento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagenMantenimientoRepository extends JpaRepository<ImagenMantenimiento, Long> {
    List<ImagenMantenimiento> findByMantenimientoId(Long mantenimientoId);

    // Buscar imágenes de mantenimientos asociados a un activo
    List<ImagenMantenimiento> findByMantenimientoActivoId(Long activoId);
}
