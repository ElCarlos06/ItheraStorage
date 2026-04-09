package mx.edu.utez.modules.location.espacios;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.kernel.BaseEntity;
import mx.edu.utez.modules.location.edificios.Edificio;

/**
 * Entidad JPA que representa un espacio o aula pertenecientes a un edificio.
 * Mapea a la tabla ESPACIO.
 *
 * @author Ithera Team
 */
@Entity
@Table(name = "ESPACIO")
@AttributeOverride(name = "id", column = @Column(name = "id_espacio"))
@Getter
@Setter
@NoArgsConstructor
public class Espacio extends BaseEntity {

    /** Puntero al edificio contenedor del espacio. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_edificio", nullable = false)
    private Edificio edificio;

    /** Título exacto identificador del aula o espacio lógico. */
    @Column(name = "nombre_espacio", nullable = false, length = 100)
    private String nombreEspacio;

    /** Clasificación del entorno físico (ej. Laboratorio, Bodega, Aula). */
    @Column(name = "tipo_espacio", length = 50)
    private String tipoEspacio;

    /** Anotaciones libres extra de visualización u operativas. */
    @Column(name = "descripcion", length = 255)
    private String descripcion;

    /** Estatus de disponibilidad lógica del espacio en sistema. */
    @Column(name = "es_activo", nullable = false)
    private Boolean esActivo = true;

}

