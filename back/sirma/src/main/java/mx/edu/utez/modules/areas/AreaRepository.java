package mx.edu.utez.modules.areas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad Area.
 * Proporciona operaciones CRUD para áreas institucionales.
 *
 * @author Ithera Team
 */
@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {
    boolean existsByNombre(String nombre);
}
