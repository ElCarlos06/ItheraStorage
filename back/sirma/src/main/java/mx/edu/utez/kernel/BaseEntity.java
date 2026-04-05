package mx.edu.utez.kernel;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase base para todas las entidades JPA en SIRMA.
 * Proporciona campos comunes de auditoría: id, createdAt, updatedAt.
 * Las subclases heredan estos campos automáticamente.
 *
 * @author Ithera Team
 */
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BaseEntity {

    /**
     * Identificador autoincremental único que representa a la entidad dentro de la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
