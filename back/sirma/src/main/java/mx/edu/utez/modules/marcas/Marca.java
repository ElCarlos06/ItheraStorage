package mx.edu.utez.modules.marcas;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.kernel.BaseEntity;

/**
 * Entidad JPA que representa una marca de equipo en SIRMA.
 * Mapea a la tabla MARCA con marcas como Dell, HP, Lenovo, etc.
 *
 * @author Ithera Team
 */
@Entity
@Table(name = "MARCA")
@AttributeOverride(name = "id", column = @Column(name = "id_marca"))
@Getter
@Setter
@NoArgsConstructor
public class Marca extends BaseEntity {

    @Column(name = "nombre", nullable = false, length = 100, unique = true)
    private String nombre;

}

