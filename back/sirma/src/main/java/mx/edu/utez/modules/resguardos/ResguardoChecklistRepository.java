package mx.edu.utez.modules.resguardos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de Spring Data JPA para la entidad ResguardoChecklist.
 *
 * @author Ithera Team
 */
@Repository
public interface ResguardoChecklistRepository extends JpaRepository<ResguardoChecklist, Long> {
    List<ResguardoChecklist> findByResguardoId(Long resguardoId);
}
