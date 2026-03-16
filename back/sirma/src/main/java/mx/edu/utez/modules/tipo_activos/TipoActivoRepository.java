package mx.edu.utez.modules.tipo_activos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoActivoRepository extends JpaRepository<TipoActivo, Long> {
    boolean existsByNombreAndEsActivoTrue(String nombre);

    Optional<TipoActivo> findFirstByNombreAndEsActivoFalse(String nombre);

    Page<TipoActivo> findByEsActivoTrue(Pageable pageable);
}

