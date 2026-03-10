package mx.edu.utez.modules.marcas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad Marca.
 * Proporciona operaciones CRUD y consultas personalizadas para marcas.
 *
 * @author Ithera Team
 */
@Repository
public interface MarcaRepository extends JpaRepository<Marca, Long> {
    Optional<Marca> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
}
