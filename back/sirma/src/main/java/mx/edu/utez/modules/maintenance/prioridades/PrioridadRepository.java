package mx.edu.utez.modules.maintenance.prioridades;

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

    /**
     * Verifica la existencia de una prioridad que se llame de igual manera.
     * @param nivel Valor del nivel para búsqueda exacta.
     * @return <code>true</code> en caso de ya haber sido persistido; <code>false</code> de lo contrario.
     */
    boolean existsByNivel(String nivel);
}
