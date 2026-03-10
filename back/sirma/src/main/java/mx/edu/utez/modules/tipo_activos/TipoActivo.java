package mx.edu.utez.modules.tipo_activos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.kernel.BaseEntity;

/**
 * Entidad JPA que representa un tipo de activo en SIRMA.
 * Mapea a la tabla TIPO_ACTIVO con tipos como Computadora, Impresora, etc.
 * Incluye clasificación de bien (Mueble/Inmueble).
 *
 * @author Ithera Team
 */
@Entity
@Table(name = "TIPO_ACTIVO")
@AttributeOverride(name = "id", column = @Column(name = "id_tipo_activo"))
@Getter
@Setter
@NoArgsConstructor
public class TipoActivo extends BaseEntity {

    @Column(name = "nombre", nullable = false, length = 100, unique = true)
    private String nombre;

    @Column(name = "tipo_bien", nullable = false)
    private String tipoBien; // 'Mueble' | 'Inmueble'

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "es_activo", nullable = false)
    private Boolean esActivo = true;

}
