package mx.edu.utez.modules.edificios;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.kernel.BaseEntity;
import mx.edu.utez.modules.campus.Campus;

/**
 * Entidad JPA que representa un edificio perteneciente a un campus universitario.
 * Mapea a la tabla EDIFICIO.
 *
 * @author Ithera Team
 */
@Entity
@Table(name = "EDIFICIO")
@AttributeOverride(name = "id", column = @Column(name = "id_edificio"))
@Getter
@Setter
@NoArgsConstructor
public class Edificio extends BaseEntity {

    /** Campus asociado y al que pertenece un edificio referenciado por llave foránea. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_campus", nullable = false)
    private Campus campus;

    /** Nombre nominal del edificio (ej. Edificio A, Biblioteca). */
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    /** Estatus de disponibilidad lógica del edificio. */
    @Column(name = "es_activo", nullable = false)
    private Boolean esActivo = true;

}
