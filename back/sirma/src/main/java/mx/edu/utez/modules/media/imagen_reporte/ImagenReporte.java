package mx.edu.utez.modules.media.imagen_reporte;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.modules.media.imagen.BaseImagen;
import mx.edu.utez.modules.reporting.reportes.Reporte;

/**
 * Entidad que representa una evidencia fotográfica de un Reporte de incidencia.
 * Se utiliza para validar visualmente el reporte generado.
 *
 * @author Ithera Team
 */
@Entity
@Table(name = "IMAGEN_REPORTE")
@AttributeOverride(name = "id", column = @Column(name = "id_imagen_reporte"))
@Getter
@Setter
@NoArgsConstructor
public class ImagenReporte extends BaseImagen {

    /** Reporte al cual sirve de evidencia. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reporte", nullable = false)
    private Reporte reporte;


}
