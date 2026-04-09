package mx.edu.utez.modules.security.roles;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.kernel.BaseEntity;

/**
 * Entidad JPA que representa un rol de usuario en SIRMA.
 * Mapea a la tabla ROL con roles como Administrador, Empleado, Técnico.
 *
 * @author Ithera Team
 */
@Entity
@Table(name = "ROL")
@AttributeOverride(name = "id", column = @Column(name = "id_rol"))
@Getter
@Setter
@NoArgsConstructor
public class Role extends BaseEntity {

    @Column(name = "nombre", nullable = false, length = 50, unique = true)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

}
