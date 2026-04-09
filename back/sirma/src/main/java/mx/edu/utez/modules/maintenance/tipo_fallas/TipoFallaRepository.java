package mx.edu.utez.modules.maintenance.tipo_fallas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad TipoFalla.
 * Proporciona operaciones CRUD para tipos de falla.
 *
 * @author Ithera Team
 */
@Repository
public interface TipoFallaRepository extends JpaRepository<TipoFalla, Long> {
    boolean existsByNombre(String nombre);
}
