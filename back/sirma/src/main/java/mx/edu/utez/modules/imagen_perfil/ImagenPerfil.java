package mx.edu.utez.modules.imagen_perfil;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.kernel.BaseEntity;
import mx.edu.utez.modules.users.User;

import java.time.LocalDateTime;

/**
 * Entidad para almacenar la información de la foto de perfil de un Usuario.
 *
 * @author Ithera Team
 */
@Entity
@Table(name = "IMAGEN_PERFIL")
@AttributeOverride(name = "id", column = @Column(name = "id_imagen_perfil"))
@Getter
@Setter
@NoArgsConstructor
public class ImagenPerfil extends BaseEntity {

	/** Usuario propietario de la foto de perfil. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario", nullable = false)
	private User usuario;

	/** URL pública de la foto de perfil en Cloudinary. */
	@Column(name = "url_cloudinary", nullable = false, length = 500)
	private String urlCloudinary;

	/** Public ID asignado por Cloudinary (se usa para eliminarla). */
	@Column(name = "public_id_cloudinary", nullable = false, length = 255)
	private String publicIdCloudinary;

	/** Nombre original del archivo cargado por la persona usuaria. */
	@Column(name = "nombre_archivo", length = 255)
	private String nombreArchivo;

	/** Momento en que se cargó la foto. */
	@Column(name = "fecha_subida", updatable = false)
	private LocalDateTime fechaSubida;

	@PrePersist
	protected void onCreate() {
		fechaSubida = LocalDateTime.now();
	}
}
