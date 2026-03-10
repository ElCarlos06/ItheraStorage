package mx.edu.utez.modules.resguardos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.kernel.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "IMAGEN_RESGUARDO")
@AttributeOverride(name = "id", column = @Column(name = "id_imagen_resguardo"))
@Getter
@Setter
@NoArgsConstructor
public class ImagenResguardo extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_resguardo", nullable = false)
    private Resguardo resguardo;

    @Column(name = "momento", nullable = false)
    private String momento; // Confirmacion | Devolucion

    @Column(name = "ruta_archivo", nullable = false, length = 255)
    private String rutaArchivo;

    @Column(name = "fecha_subida", insertable = false, updatable = false)
    private LocalDateTime fechaSubida;

}

