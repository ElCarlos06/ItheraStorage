package mx.edu.utez.modules.resguardos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagenResguardoRepository extends JpaRepository<ImagenResguardo, Long> {
    List<ImagenResguardo> findByResguardoId(Long resguardoId);
}

