package mx.edu.utez.modules.imagen_activo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.kernel.BaseEntity;
import mx.edu.utez.modules.assets.Assets;

import java.time.LocalDateTime;

/**
 * Entidad que representa la asociación entre una imagen almacenada en Cloudinary y un Activo.
 * Almacena la URL pública y el ID para gestionar el archivo en la nube.
 *
 * @author Ithera Team
 */
@Entity
@Table(name = "IMAGEN_ACTIVO")
@AttributeOverride(name = "id", column = @Column(name = "id_imagen_activo"))
@Getter
@Setter
@NoArgsConstructor
public class ImagenActivo extends BaseEntity {

    /** Activo al que pertenece la imagen. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_activo", nullable = false)
    private Assets activo;

    /** URL pública de la imagen en Cloudinary. */
    @Column(name = "url_cloudinary", nullable = false, length = 500)
    private String urlCloudinary;

    /** Public ID asignado por Cloudinary (necesario para eliminar/transformar). */
    @Column(name = "public_id_cloudinary", nullable = false, length = 255)
    private String publicIdCloudinary;

    /** Nombre original del archivo subido por el usuario. */
    @Column(name = "nombre_archivo", length = 255)
    private String nombreArchivo;

    /** Fecha y hora en la que se subió la imagen. Se asigna automáticamente. */
    @Column(name = "fecha_subida", updatable = false)
    private LocalDateTime fechaSubida;

    @PrePersist
    protected void onCreate() {
        fechaSubida = LocalDateTime.now();
    }
}
