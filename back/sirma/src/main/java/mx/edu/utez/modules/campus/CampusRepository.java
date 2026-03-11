package mx.edu.utez.modules.campus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad Campus.
 * Proporciona operaciones CRUD para campus universitarios.
 *
 * @author Ithera Team
 */
@Repository
public interface CampusRepository extends JpaRepository<Campus, Long> {
    boolean existsByNombreAndEsActivoTrue(String nombre);
    boolean existsByNombreAndEsActivoTrueAndIdNot(String nombre, Long id);
}
