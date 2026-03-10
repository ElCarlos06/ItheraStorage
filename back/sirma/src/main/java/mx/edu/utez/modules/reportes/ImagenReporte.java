package mx.edu.utez.modules.reportes;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.kernel.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "IMAGEN_REPORTE")
@AttributeOverride(name = "id", column = @Column(name = "id_imagen_reporte"))
@Getter
@Setter
@NoArgsConstructor
public class ImagenReporte extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reporte", nullable = false)
    private Reporte reporte;

    @Column(name = "ruta_archivo", nullable = false, length = 255)
    private String rutaArchivo;

    @Column(name = "fecha_subida", insertable = false, updatable = false)
    private LocalDateTime fechaSubida;

}

