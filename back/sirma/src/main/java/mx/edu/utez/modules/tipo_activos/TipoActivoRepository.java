package mx.edu.utez.modules.tipo_activos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoActivoRepository extends JpaRepository<TipoActivo, Long> {
    boolean existsByNombre(String nombre);
}

