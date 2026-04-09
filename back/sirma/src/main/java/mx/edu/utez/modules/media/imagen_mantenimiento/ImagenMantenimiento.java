package mx.edu.utez.modules.media.imagen_mantenimiento;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.modules.media.imagen.BaseImagen;
import mx.edu.utez.modules.maintenance.mantenimientos.Mantenimiento;

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
public class ImagenMantenimiento extends BaseImagen {

    /** Mantenimiento asociado a la imagen. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mantenimiento", nullable = false)
    private Mantenimiento mantenimiento;
}
