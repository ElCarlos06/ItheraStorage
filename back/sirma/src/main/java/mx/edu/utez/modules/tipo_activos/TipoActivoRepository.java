package mx.edu.utez.modules.tipo_activos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import mx.edu.utez.modules.assets.Assets;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoActivoRepository extends JpaRepository<TipoActivo, Long> {
    boolean existsByNombreAndEsActivoTrue(String nombre);

    Optional<TipoActivo> findFirstByNombreAndEsActivoFalse(String nombre);

    Page<TipoActivo> findByEsActivoTrue(Pageable pageable);

    Optional<TipoActivo> findByNombreAndMarcaAndModelo(String tipoStr, String marcaStr, String modeloStr);

    Optional<TipoActivo> findByNombre(String nombre);

    Optional<TipoActivo> findByNombreAndMarcaAndTipoBienAndModelo(String nombreStr, String marcaStr, String bienStr, String modeloStr);
}
