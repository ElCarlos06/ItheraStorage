package mx.edu.utez.modules.edificios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EdificioRepository extends JpaRepository<Edificio, Long> {
    List<Edificio> findByCampusId(Long campusId);
    boolean existsByCampusIdAndNombreAndEsActivoTrue(Long campusId, String nombre);
    boolean existsByCampusIdAndNombreAndEsActivoTrueAndIdNot(Long campusId, String nombre, Long id);
    Optional<Edificio> findFirstByCampusIdAndNombreAndEsActivoFalse(Long campusId, String nombre);
}

