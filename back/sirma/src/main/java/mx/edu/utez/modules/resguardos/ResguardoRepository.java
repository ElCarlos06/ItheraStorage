package mx.edu.utez.modules.resguardos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResguardoRepository extends JpaRepository<Resguardo, Long> {
    List<Resguardo> findByActivoId(Long activoId);
    List<Resguardo> findByUsuarioEmpleadoId(Long usuarioId);
    List<Resguardo> findByEstadoResguardo(String estado);
}

