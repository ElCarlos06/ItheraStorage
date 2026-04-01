package mx.edu.utez.modules.campus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Repositorio JPA para la entidad Campus.
 * Proporciona operaciones CRUD para campus universitarios.
 *
 * @author Ithera Team
 */
@Repository
public interface CampusRepository extends JpaRepository<Campus, Long> {
    Page<Campus> findAllByEsActivoTrue(Pageable pageable);
    boolean existsByNombreAndEsActivoTrue(String nombre);
    boolean existsByNombreAndEsActivoTrueAndIdNot(String nombre, Long id);
    Optional<Campus> findByNombre(String nombre);
    Optional<Campus> findFirstByNombreAndEsActivoFalse(String nombre);

    List<Campus> findByNombreIn(Set<String> campusLote);
}
