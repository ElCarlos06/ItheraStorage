package mx.edu.utez.modules.resguardos;

import mx.edu.utez.modules.assets.Assets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad Resguardo.
 *
 * @author Ithera Team
 */
@Repository
public interface ResguardoRepository extends JpaRepository<Resguardo, Long> {
    List<Resguardo> findByActivoId(Long activoId);
    List<Resguardo> findByUsuarioEmpleadoId(Long usuarioId);
    List<Resguardo> findByEstadoResguardo(String estado);

    Optional<Resguardo> findByActivoAndEstadoResguardo(Assets assets, String pendiente);

    Optional<Resguardo> findFirstByActivoIdAndEstadoResguardoIn(Long id, List<String> pendiente);

    // empleado + activo + estado_resguardo (ej. Confirmado)
    boolean existsByUsuarioEmpleado_IdAndActivo_IdAndEstadoResguardo(
            Long usuarioEmpleadoId, Long activoId, String estadoResguardo);
}
