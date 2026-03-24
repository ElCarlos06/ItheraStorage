package mx.edu.utez.modules.assets;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetsRepository extends JpaRepository<Assets, Long> {

    /**
     * Actualiza solo estado_custodia sin tocar el resto de columnas (evita Data truncated).
     */
    @Modifying
    @Query("UPDATE Assets a SET a.estadoCustodia = :estado WHERE a.id = :id")
    void updateEstadoCustodia(Long id, String estado);

    /** Actualiza estado_custodia y esActivo (para desactivar activo). */
    @Modifying
    @Query("UPDATE Assets a SET a.estadoCustodia = :estado, a.esActivo = :esActivo WHERE a.id = :id")
    void updateEstadoYActivo(Long id, String estado, Boolean esActivo);

    /** Actualiza solo estado_operativo (OK | Reportado | Mantenimiento). */
    @Modifying
    @Query("UPDATE Assets a SET a.estadoOperativo = :estado WHERE a.id = :id")
    void updateEstadoOperativo(Long id, String estado);
    Optional<Assets> findByEtiqueta(String etiqueta);
    Optional<Assets> findByNumeroSerie(String numeroSerie);
    boolean existsByEtiqueta(String etiqueta);
    boolean existsByNumeroSerie(String numeroSerie);
    Page<Assets> findByEsActivoTrue(Pageable pageable);

    @Query("SELECT a.tipoActivo.id, COUNT(a) FROM Assets a WHERE a.esActivo = true GROUP BY a.tipoActivo.id")
    List<Object[]> countActiveAssetsByTipoActivoId();

}

