package mx.edu.utez.modules.core.solicitud_bajas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad SolicitudBaja.
 *
 * @author Ithera Team
 */
@Repository
public interface SolicitudBajaRepository extends JpaRepository<SolicitudBaja, Long> {
    List<SolicitudBaja> findByActivoId(Long activoId);
    List<SolicitudBaja> findByEstado(String estado);
    Optional<SolicitudBaja> findByMantenimientoId(Long mantenimientoId);
}
