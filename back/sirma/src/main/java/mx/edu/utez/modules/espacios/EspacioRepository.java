package mx.edu.utez.modules.espacios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EspacioRepository extends JpaRepository<Espacio, Long> {
    List<Espacio> findByEdificioId(Long edificioId);
    boolean existsByEdificioIdAndNombreEspacioAndEsActivoTrue(Long edificioId, String nombreEspacio);
    boolean existsByEdificioIdAndNombreEspacioAndEsActivoTrueAndIdNot(Long edificioId, String nombreEspacio, Long id);
    Optional<Espacio> findFirstByEdificioIdAndNombreEspacioAndEsActivoFalse(Long edificioId, String nombreEspacio);
}

