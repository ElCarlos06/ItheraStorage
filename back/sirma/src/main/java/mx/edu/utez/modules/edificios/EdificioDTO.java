package mx.edu.utez.modules.edificios;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object de configuración para la entidad Edificio.
 * Retiene la lógica de captura para validaciones de nombre, asociación y estado.
 *
 * @author Ithera Team
 */
@Getter
@Setter
@NoArgsConstructor
public class EdificioDTO {

    /** Identificador general subyacente. */
    private Long id;

    /** Relación a nivel numérico del ID originado de Campus. */
    @NotNull
    private Long idCampus;

    /** Título textual o letra del edificio establecido. */
    @NotBlank
    @Size(max = 100)
    private String nombre;

    /** Nivel de disponibilidad del objeto. */
    private Boolean esActivo;

}
