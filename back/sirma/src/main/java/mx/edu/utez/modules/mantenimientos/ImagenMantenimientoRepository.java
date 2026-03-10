package mx.edu.utez.modules.mantenimientos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagenMantenimientoRepository extends JpaRepository<ImagenMantenimiento, Long> {
    List<ImagenMantenimiento> findByMantenimientoId(Long mantenimientoId);
}

