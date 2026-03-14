package mx.edu.utez.modules.imagen_perfil;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagenPerfilRepository extends JpaRepository<ImagenPerfil, Long> {
	List<ImagenPerfil> findByUsuarioId(Long usuarioId);
}
