package mx.edu.utez.modules.edificios;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EdificioRepository extends JpaRepository<Edificio, Long> {
    Page<Edificio> findAllByEsActivoTrue(Pageable pageable);
    List<Edificio> findByCampusId(Long campusId);
    List<Edificio> findByCampusIdAndEsActivoTrue(Long campusId);
    boolean existsByCampusIdAndNombreAndEsActivoTrue(Long campusId, String nombre);
    boolean existsByCampusIdAndNombreAndEsActivoTrueAndIdNot(Long campusId, String nombre, Long id);
    Optional<Edificio> findFirstByCampusIdAndNombreAndEsActivoFalse(Long campusId, String nombre);
    Optional<Edificio> findByCampusIdAndNombre(Long campusId, String nombre);
}
