package mx.edu.utez.modules.areas;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.kernel.BaseEntity;

/**
 * Entidad JPA que representa un área institucional en SIRMA.
 * Mapea a la tabla AREA con áreas como Recursos Humanos, TI, etc.
 *
 * @author Ithera Team
 */
@Entity
@Table(name = "AREA")
@AttributeOverride(name = "id", column = @Column(name = "id_area"))
@Getter
@Setter
@NoArgsConstructor
public class Area extends BaseEntity {

    /**
     * Nombre descriptivo y único del área.
     */
    @Column(name = "nombre", nullable = false, length = 100, unique = true)
    private String nombre;

    /**
     * Breve descripción de la funcionalidad o propósito del área.
     */
    @Column(name = "descripcion", length = 255)
    private String descripcion;

}
