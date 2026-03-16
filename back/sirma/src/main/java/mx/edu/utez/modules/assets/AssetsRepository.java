package mx.edu.utez.modules.assets;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetsRepository extends JpaRepository<Assets, Long> {
    Optional<Assets> findByEtiqueta(String etiqueta);
    Optional<Assets> findByNumeroSerie(String numeroSerie);
    boolean existsByEtiqueta(String etiqueta);
    boolean existsByNumeroSerie(String numeroSerie);
    Page<Assets> findByEsActivoTrue(Pageable pageable);

    @Query("SELECT a.tipoActivo.id, COUNT(a) FROM Assets a WHERE a.esActivo = true GROUP BY a.tipoActivo.id")
    List<Object[]> countActiveAssetsByTipoActivoId();
}

