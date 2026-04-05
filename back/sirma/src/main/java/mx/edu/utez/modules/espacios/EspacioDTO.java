package mx.edu.utez.modules.espacios;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Molde DTO empleado para la carga segura de datos sobre Espacios (Aulas/Laboratorios).
 * Centraliza validaciones a nivel de anotaciones Jakarta.
 *
 * @author Ithera Team
 */
@Getter
@Setter
@NoArgsConstructor
public class EspacioDTO {

    /** Identificador general subyacente. */
    private Long id;

    /** Relación a nivel numérico del ID originado de Edificio al que pertenece. */
    @NotNull
    private Long idEdificio;

    /** Título textual representativo del espacio establecido. */
    @NotBlank
    @Size(max = 100)
    private String nombreEspacio;

    /** Criterio general u homologado que agrupa en clase de aula o área común. */
    @Size(max = 50)
    private String tipoEspacio;

    /** Detalles extra o notas generales. */
    @Size(max = 255)
    private String descripcion;

    /** Nivel o estado lógico. */
    private Boolean esActivo;

}
