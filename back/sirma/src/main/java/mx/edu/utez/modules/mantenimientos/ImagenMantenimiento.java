package mx.edu.utez.modules.mantenimientos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.kernel.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "IMAGEN_MANTENIMIENTO")
@AttributeOverride(name = "id", column = @Column(name = "id_imagen_mant"))
@Getter
@Setter
@NoArgsConstructor
public class ImagenMantenimiento extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mantenimiento", nullable = false)
    private Mantenimiento mantenimiento;

    /** URL pública de la imagen en Cloudinary. */
    @Column(name = "url_cloudinary", nullable = false, length = 500)
    private String urlCloudinary;

    /** Public ID asignado por Cloudinary (necesario para eliminar/transformar). */
    @Column(name = "public_id_cloudinary", nullable = false, length = 255)
    private String publicIdCloudinary;

    /** Nombre original del archivo subido por el usuario. */
    @Column(name = "nombre_archivo", length = 255)
    private String nombreArchivo;

    @Column(name = "fecha_subida", insertable = false, updatable = false)
    private LocalDateTime fechaSubida;

}

