package mx.edu.utez.modules.core.assets;

import mx.edu.utez.modules.core.assets.projections.AssetsProjection;
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

/**
 * Repositorio de datos JPA para la entidad Activos.
 * Ejecuta operaciones CRUD y consultas avanzadas sobre las propiedades de cada activo,
 * incluyendo actualización optimizada de estados.
 *
 * @author Ithera Team
 */
@Repository
public interface AssetsRepository extends JpaRepository<Assets, Long> {

    /**
     * Actualiza solo estado_custodia sin tocar el resto de columnas (evita Data truncated).
     *
     * @param id Identificador del activo a modificar.
     * @param estado Nuevo estado de custodia a asignar.
     */
    @Modifying
    @Query("UPDATE Assets a SET a.estadoCustodia = :estado WHERE a.id = :id")
    void updateEstadoCustodia(Long id, String estado);

    /**
     * Actualiza estado_custodia y el atributo esActivo (por ejemplo para desactivar el activo).
     *
     * @param id Identificador del activo a modificar.
     * @param estado Nuevo estado de custodia.
     * @param esActivo Nuevo valor para el indicador logístico de estado activo.
     */
    @Modifying
    @Query("UPDATE Assets a SET a.estadoCustodia = :estado, a.esActivo = :esActivo WHERE a.id = :id")
    void updateEstadoYActivo(Long id, String estado, Boolean esActivo);

    /**
     * Actualiza solo estado_operativo (OK | Reportado | Mantenimiento).
     *
     * @param id Identificador del activo a modificar.
     * @param estado Nuevo estado operativo del activo.
     */
    @Modifying
    @Query("UPDATE Assets a SET a.estadoOperativo = :estado WHERE a.id = :id")
    void updateEstadoOperativo(Long id, String estado);

    /**
     * Busca un activo utilizando su etiqueta única.
     *
     * @param etiqueta Cadena de etiqueta a buscar.
     * @return Opcional del activo si fue encontrado.
     */
    Optional<Assets> findByEtiqueta(String etiqueta);

    /**
     * Busca un activo utilizando su número de serie.
     *
     * @param numeroSerie Serie a buscar.
     * @return Opcional del activo encontrado.
     */
    Optional<Assets> findByNumeroSerie(String numeroSerie);

    /**
     * Comprueba la existencia de un registro filtrando por su etiqueta única.
     *
     * @param etiqueta Etiqueta evaluada.
     * @return booleano true si ya existe en la BD.
     */
    boolean existsByEtiqueta(String etiqueta);

    /**
     * Comprueba la existencia de un registro filtrando por su número de serie.
     *
     * @param numeroSerie Serie evaluada.
     * @return booleano true si ya existe en la BD.
     */
    boolean existsByNumeroSerie(String numeroSerie);

    /**
     * Configura y extrae un listado paginado filtrando únicamente por elementos activos.
     *
     * @param pageable Configuración de paginación.
     * @return Página de activos correspondientes.
     */
    Page<Assets> findByEsActivoTrue(Pageable pageable);

    /**
     * Cuenta la cantidad de activos activos agrupados por su respectivo tipo de activo.
     *
     * @return Lista de objetos de conteo donde el primer elemento es el id de tipo activo.
     */
    @Query("SELECT a.tipoActivo.id, COUNT(a) FROM Assets a WHERE a.esActivo = true GROUP BY a.tipoActivo.id")
    List<Object[]> countActiveAssetsByTipoActivoId();

    /**
     * Localiza etiquetas filtrando por medio de una lista comparada.
     *
     * @param etiquetas Colección de etiquetas buscadas.
     * @return Resumen de etiquetas encontradas dentro de la plataforma.
     */
    @Query("SELECT a.etiqueta FROM Assets a WHERE a.etiqueta IN :etiquetas")
    Set<String> findEtiquetasExistentes(@Param("etiquetas") Set<String> etiquetas);

    /**
     * Localiza series filtrando por medio de una lista comparada.
     *
     * @param series Colección de series buscadas.
     * @return Resumen de series encontradas dentro de la plataforma.
     */
    @Query("SELECT a.numeroSerie FROM Assets a WHERE a.numeroSerie IN :series")
    Set<String> findSeriesExistentes(@Param("series") Set<String> series);

    /**
     * Ejecuta sumatorias SQL globales sobre los activos (Ej: Total en Mantenimiento, Reportes, Resguardos, etc).
     * @return Resultados proyectados de las métricas obtenidas.
     */
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

    /**
     * Ejecuta sumatorias SQL de rendimiento centradas sobre activos registrados previamente a un periodo especifico.
     * @param lastWeek La fecha tope a calificar.
     * @return Resultados con métricas del histórico.
     */
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

