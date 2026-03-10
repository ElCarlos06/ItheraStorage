package mx.edu.utez.modules.assets;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssetsRepository extends JpaRepository<Assets, Long> {
    Optional<Assets> findByEtiqueta(String etiqueta);
    Optional<Assets> findByNumeroSerie(String numeroSerie);
    boolean existsByEtiqueta(String etiqueta);
    boolean existsByNumeroSerie(String numeroSerie);
}

