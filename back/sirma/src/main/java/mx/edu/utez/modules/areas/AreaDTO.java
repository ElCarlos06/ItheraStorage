package mx.edu.utez.modules.areas;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para operaciones CRUD de áreas en SIRMA.
 * Contiene validaciones básicas para asegurar integridad de datos.
 *
 * @author Ithera Team
 */
@Getter
@Setter
@NoArgsConstructor
public class AreaDTO {

    private Long id;

    @NotBlank(message = "El nombre del área es obligatorio")
    @Size(max = 100, message = "El nombre del área no debe exceder 100 caracteres")
    private String nombre;

    @Size(max = 255, message = "La descripción no debe exceder 255 caracteres")
    private String descripcion;

}
