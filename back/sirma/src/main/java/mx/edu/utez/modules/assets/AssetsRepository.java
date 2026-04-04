package mx.edu.utez.modules.assets;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    @Query("SELECT a.etiqueta FROM Assets a WHERE a.etiqueta IN :etiquetas")
    Set<String> findEtiquetasExistentes(@Param("etiquetas") Set<String> etiquetas);

    @Query("SELECT a.numeroSerie FROM Assets a WHERE a.numeroSerie IN :series")
    Set<String> findSeriesExistentes(@Param("series") Set<String> series);

//    @Query("""
//    SELECT
//      COUNT(a) as total,
//      SUM(CASE WHEN a.estadoCustodia = 'Disponible' THEN 1 ELSE 0 END) as disponibles,
//      SUM(CASE WHEN a.estadoCustodia = 'Resguardado' THEN 1 ELSE 0 END) as resguardados,
//      SUM(CASE WHEN a.estadoOperativo = 'Mantenimiento' THEN 1 ELSE 0 END) as enMantenimiento,
//      SUM(CASE WHEN a.estadoOperativo = 'Reportado' THEN 1 ELSE 0 END) as reportados
//    FROM Assets a WHERE a.esActivo = true AND a.fechaAlta BETWEEN :inicio AND :fin
//""")
//    AssetsProjection findAssetsStatsByWeek(LocalDate inicio, LocalDate fin);

    @Query("""
    SELECT 
      COUNT(a) as total,
      SUM(CASE WHEN a.estadoCustodia = 'Disponible' THEN 1 ELSE 0 END) as disponibles,
      SUM(CASE WHEN a.estadoCustodia = 'Resguardado' THEN 1 ELSE 0 END) as resguardados,
      SUM(CASE WHEN a.estadoOperativo = 'Mantenimiento' THEN 1 ELSE 0 END) as enMantenimiento,
      SUM(CASE WHEN a.estadoOperativo = 'Reportado' THEN 1 ELSE 0 END) as reportados
    FROM Assets a WHERE a.esActivo = true
""")
    AssetsProjection findAssetsStatsGlobal();

    @Query("""
    SELECT 
      COUNT(a) as total,
      SUM(CASE WHEN a.estadoCustodia = 'Disponible' THEN 1 ELSE 0 END) as disponibles,
      SUM(CASE WHEN a.estadoCustodia = 'Resguardado' THEN 1 ELSE 0 END) as resguardados,
      SUM(CASE WHEN a.estadoOperativo = 'Mantenimiento' THEN 1 ELSE 0 END) as enMantenimiento,
      SUM(CASE WHEN a.estadoOperativo = 'Reportado' THEN 1 ELSE 0 END) as reportados
    FROM Assets a WHERE a.esActivo = true AND a.fechaAlta < :lastWeek
""")
    AssetsProjection findAssetsStatsOfLastWeek(LocalDate lastWeek);
}

