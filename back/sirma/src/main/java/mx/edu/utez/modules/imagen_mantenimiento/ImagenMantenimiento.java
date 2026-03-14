package mx.edu.utez.modules.imagen_mantenimiento;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.kernel.BaseEntity;
import mx.edu.utez.modules.mantenimientos.Mantenimiento;

import java.time.LocalDateTime;

/**
 * Entidad que vincula una evidencia fotográfica con un registro de Mantenimiento.
 * Permite documentar visualmente el proceso de mantenimiento.
 *
 * @author Ithera Team
 */
@Entity
@Table(name = "IMAGEN_MANTENIMIENTO")
@AttributeOverride(name = "id", column = @Column(name = "id_imagen_mant"))
@Getter
@Setter
@NoArgsConstructor
public class ImagenMantenimiento extends BaseEntity {

    /** Mantenimiento asociado a la imagen. */
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

    /** Timestamp de creación del registro. */
    @Column(name = "fecha_subida", updatable = false)
    private LocalDateTime fechaSubida;

    @PrePersist
    protected void onCreate() {
        fechaSubida = LocalDateTime.now();
    }

}
