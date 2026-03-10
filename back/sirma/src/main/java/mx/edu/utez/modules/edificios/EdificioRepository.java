package mx.edu.utez.modules.edificios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EdificioRepository extends JpaRepository<Edificio, Long> {
    List<Edificio> findByCampusId(Long campusId);
    boolean existsByCampusIdAndNombre(Long campusId, String nombre);
}

