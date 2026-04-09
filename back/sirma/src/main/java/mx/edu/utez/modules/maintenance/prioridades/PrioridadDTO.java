package mx.edu.utez.modules.maintenance.prioridades;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para operaciones CRUD de prioridades en SIRMA.
 * Contiene validaciones básicas para asegurar integridad de datos.
 *
 * @author Ithera Team
 */
@Getter
@Setter
@NoArgsConstructor
public class PrioridadDTO {

    /** Identificador general numérico subyacente. */
    private Long id;

    /** Valor descriptivo del nivel de prioridad. */
    @NotBlank(message = "El nivel de prioridad es obligatorio")
    @Size(max = 50, message = "El nivel de prioridad no debe exceder 50 caracteres")
    private String nivel;

    /** Cantidad límite en horas que el sistema tomará como tiempo de respuesta ideal. */
    private Integer tiempoRespuestaHoras;

}
