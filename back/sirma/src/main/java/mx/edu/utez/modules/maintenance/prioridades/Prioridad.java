package mx.edu.utez.modules.maintenance.prioridades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.kernel.BaseEntity;

/**
 * Entidad JPA que representa una prioridad de reporte en SIRMA.
 * Mapea a la tabla PRIORIDAD con niveles como Baja, Media, Alta.
 *
 * @author Ithera Team
 */
@Entity
@Table(name = "PRIORIDAD")
@AttributeOverride(name = "id", column = @Column(name = "id_prioridad"))
@Getter
@Setter
@NoArgsConstructor
public class Prioridad extends BaseEntity {

    /** Título o descripción del nivel de prioridad (ej. Baja, Media, Alta). */
    @Column(name = "nivel", nullable = false, length = 50, unique = true)
    private String nivel;

    /** Cantidad de horas estimadas o pactadas como máximo para dar respuesta. */
    @Column(name = "tiempo_respuesta_horas")
    private Integer tiempoRespuestaHoras;

}
