package mx.edu.utez.modules.imagen_perfil;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.modules.imagen.BaseImagen;
import mx.edu.utez.modules.users.User;

/**
 * Entidad para almacenar la información de la foto de perfil de un Usuario.
 *
 * @author Ithera Team
 */
@Entity
@Table(name = "IMAGEN_PERFIL")
@AttributeOverride(name = "id", column = @Column(name = "id_imagen"))
@Getter
@Setter
@NoArgsConstructor
public class ImagenPerfil extends BaseImagen {

	/** Usuario propietario de la foto de perfil. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario", nullable = false)
	private User usuario;
}
