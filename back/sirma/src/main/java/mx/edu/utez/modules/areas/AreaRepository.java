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

    /**
     * Verifica si existe un área registrada con el nombre proporcionado.
     *
     * @param nombre El nombre del área a buscar.
     * @return <code>true</code> si el área existe, de lo contrario <code>false</code>.
     */
    boolean existsByNombre(String nombre);
}
