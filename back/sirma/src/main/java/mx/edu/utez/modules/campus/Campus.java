package mx.edu.utez.modules.campus;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.kernel.BaseEntity;

/**
 * Entidad JPA que representa un campus universitario en SIRMA.
 * Mapea a la tabla CAMPUS con campus como Norte, Sur, Centro, etc.
 *
 * @author Ithera Team
 */
@Entity
@Table(name = "CAMPUS")
@AttributeOverride(name = "id", column = @Column(name = "id_campus"))
@Getter
@Setter
@NoArgsConstructor
public class Campus extends BaseEntity {

    /** Nombre del campus (ej. Sur, Norte, Centro). */
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    /** Descripción detallada o dirección abreviada del campus. */
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    /** Bandera de control lógico para determinar si el campus está activo/disponible. */
    @Column(name = "es_activo", nullable = false)
    private Boolean esActivo = true;

}

