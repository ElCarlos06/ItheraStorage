package mx.edu.utez.modules.media.imagen_perfil;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImagenPerfilRepository extends JpaRepository<ImagenPerfil, Long> {
	Optional<ImagenPerfil> findByUsuarioId(Long idUsuario);
}
