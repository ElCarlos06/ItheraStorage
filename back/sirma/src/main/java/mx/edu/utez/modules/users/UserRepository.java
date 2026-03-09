package mx.edu.utez.modules.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /** Busca un usuario por su correo. */
    Optional<User> findByCorreo(String correo);
}
