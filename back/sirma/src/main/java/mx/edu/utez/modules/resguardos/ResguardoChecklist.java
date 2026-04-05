package mx.edu.utez.modules.resguardos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.kernel.BaseEntity;

/**
 * Entidad JPA que representa un elemento de comprobación (checklist) durante la asignación/devolución de un resguardo.
 * Mapea a la tabla RESGUARDO_CHECKLIST.
 *
 * @author Ithera Team
 */
@Entity
@Table(name = "RESGUARDO_CHECKLIST")
@AttributeOverride(name = "id", column = @Column(name = "id_checklist"))
@Getter
@Setter
@NoArgsConstructor
public class ResguardoChecklist extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_resguardo", nullable = false)
    private Resguardo resguardo;

    @Column(name = "item", nullable = false, length = 100)
    private String item;

    @Column(name = "resultado", nullable = false)
    private String resultado; // OK | Falla | No Aplica

}
