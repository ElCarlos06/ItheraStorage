package mx.edu.utez.modules.campus;

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

    private Long id;

    @NotBlank(message = "El nombre del campus es obligatorio")
    @Size(max = 100, message = "El nombre del campus no debe exceder 100 caracteres")
    private String nombre;

    private String descripcion;

    private Boolean esActivo;

}
