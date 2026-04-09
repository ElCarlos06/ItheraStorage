package mx.edu.utez.modules.security.roles;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad Role.
 * Proporciona operaciones CRUD para roles de usuario.
 *
 * @author Ithera Team
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    boolean existsByNombre(String nombre);
}
