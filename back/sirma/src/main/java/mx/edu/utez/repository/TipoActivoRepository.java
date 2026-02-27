package mx.edu.utez.repository;

import mx.edu.utez.model.TipoActivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoActivoRepository extends JpaRepository<TipoActivo, Integer> {
}
