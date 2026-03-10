package mx.edu.utez.modules.bitacora;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BitacoraRepository extends JpaRepository<Bitacora, Long> {
    List<Bitacora> findByActivoId(Long activoId);
    List<Bitacora> findByUsuarioId(Long usuarioId);
    List<Bitacora> findByTipoEvento(String tipoEvento);
}

