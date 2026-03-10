package mx.edu.utez.modules.tipo_fallas;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.kernel.BaseEntity;

/**
 * Entidad JPA que representa un tipo de falla en SIRMA.
 * Mapea a la tabla TIPO_FALLA con tipos como Hardware, Software, etc.
 *
 * @author Ithera Team
 */
@Entity
@Table(name = "TIPO_FALLA")
@AttributeOverride(name = "id", column = @Column(name = "id_tipo_falla"))
@Getter
@Setter
@NoArgsConstructor
public class TipoFalla extends BaseEntity {

    @Column(name = "nombre", nullable = false, length = 100, unique = true)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

}

