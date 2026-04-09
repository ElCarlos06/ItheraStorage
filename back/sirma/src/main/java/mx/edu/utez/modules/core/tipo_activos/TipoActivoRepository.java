package mx.edu.utez.modules.core.tipo_activos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de Spring Data JPA para la entidad TipoActivo.
 *
 * @author Ithera Team
 */
@Repository
public interface TipoActivoRepository extends JpaRepository<TipoActivo, Long> {
    boolean existsByNombreAndEsActivoTrue(String nombre);

    Optional<TipoActivo> findFirstByNombreAndEsActivoFalse(String nombre);

    Page<TipoActivo> findByEsActivoTrue(Pageable pageable);

    Optional<TipoActivo> findByNombreAndMarcaAndModelo(String tipoStr, String marcaStr, String modeloStr);

    Optional<TipoActivo> findByNombre(String nombre);

    Optional<TipoActivo> findByNombreAndMarcaAndTipoBienAndModelo(String nombreStr, String marcaStr, String bienStr, String modeloStr);

    @Query("SELECT t FROM TipoActivo t WHERE CONCAT(t.nombre,'-',t.marca,'-',t.tipoBien,'-',t.modelo) IN :keys")
    List<TipoActivo> findByCompositeKeys(@Param("keys") Set<String> keys);
}
