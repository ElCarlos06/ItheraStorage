package mx.edu.utez.modules.imagen_activo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagenActivoRepository extends JpaRepository<ImagenActivo, Long> {
    List<ImagenActivo> findByActivoId(Long activoId);
}

