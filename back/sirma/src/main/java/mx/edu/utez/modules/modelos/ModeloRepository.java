package mx.edu.utez.modules.modelos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModeloRepository extends JpaRepository<Modelo, Long> {
    List<Modelo> findByMarcaId(Long marcaId);
    boolean existsByMarcaIdAndNombre(Long marcaId, String nombre);
    Optional<Modelo> findFirstByMarcaIdAndNombre(Long marcaId, String nombre);
}

