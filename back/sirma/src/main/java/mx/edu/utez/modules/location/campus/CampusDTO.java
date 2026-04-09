package mx.edu.utez.modules.location.campus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para operaciones CRUD de campus en SIRMA.
 * Contiene validaciones básicas para asegurar integridad de datos.
 *
 * @author Ithera Team
 */
@Getter
@Setter
@NoArgsConstructor
public class CampusDTO {

    /** Diferenciador único de campus en el sistema (nulo para crear). */
    private Long id;

    /** Nombre de carácter obligatorio que identificará al campus. */
    @NotBlank(message = "El nombre del campus es obligatorio")
    @Size(max = 100, message = "El nombre del campus no debe exceder 100 caracteres")
    private String nombre;

    /** Notas o datos de contacto rápidos respecto al campus correspondiente. */
    private String descripcion;

    /** Nivel de disponibilidad del objeto. */
    private Boolean esActivo;

}
