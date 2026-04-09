package mx.edu.utez.modules.security.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad User.
 * Proporciona operaciones CRUD y consultas personalizadas para usuarios.
 *
 * @author Ithera Team
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /** Busca un usuario por su correo (ignora mayúsculas). */
    Optional<User> findByCorreoIgnoreCase(String correo);

    boolean existsByCorreo(String correo);

    boolean existsByCurp(String curp);

    boolean existsByNumeroEmpleado(String numeroEmpleado);

    /** Cuenta usuarios por rol para generar el consecutivo del número de empleado. */
    long countByRoleId(Long roleId);
}

