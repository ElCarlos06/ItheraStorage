package mx.edu.utez.modules.prioridades;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad Prioridad.
 * Proporciona operaciones CRUD para prioridades de reportes.
 *
 * @author Ithera Team
 */
@Repository
public interface PrioridadRepository extends JpaRepository<Prioridad, Long> {
    boolean existsByNivel(String nivel);
}
