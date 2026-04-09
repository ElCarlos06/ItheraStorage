package mx.edu.utez.modules.location.areas;

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

    /**
     * Identificador único del área (puede ser nulo en caso de creación).
     */
    private Long id;

    /**
     * Nombre del área, es requerido y tiene límite de 100 caracteres.
     */
    @NotBlank(message = "El nombre del área es obligatorio")
    @Size(max = 100, message = "El nombre del área no debe exceder 100 caracteres")
    private String nombre;

    /**
     * Descripción opcional del área, con un límite de 255 caracteres.
     */
    @Size(max = 255, message = "La descripción no debe exceder 255 caracteres")
    private String descripcion;

}
