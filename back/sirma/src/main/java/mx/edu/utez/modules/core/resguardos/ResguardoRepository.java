package mx.edu.utez.modules.core.resguardos;

import mx.edu.utez.modules.core.assets.Assets;
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

    /**
     * Marca como "Devuelto" todos los resguardos activos de un empleado.
     * Se usa al desactivar al empleado para liberar los activos bajo su custodia.
     */
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query(
        "UPDATE Resguardo r SET r.estadoResguardo = 'Devuelto' " +
        "WHERE r.usuarioEmpleado.id = :empleadoId " +
        "AND r.estadoResguardo IN ('Pendiente', 'Confirmado')")
    void devolverPorEmpleado(@org.springframework.data.repository.query.Param("empleadoId") Long empleadoId);
}
