package mx.edu.utez.modules.tipo_activos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Objeto de Transferencia de Datos (DTO) para la entidad TipoActivo.
 *
 * @author Ithera Team
 */
@Getter
@Setter
@NoArgsConstructor
public class TipoActivoDTO {

    private Long id;

    @NotBlank
    @Size(max = 100)
    private String nombre;

    @NotBlank
    private String tipoBien; // 'Mueble' | 'Inmueble'

    @Size(max = 255)
    private String descripcion;

    @Size(max = 100)
    private String marca;

    @Size(max = 100)
    private String modelo;

    private Boolean esActivo;

    /** Cantidad de activos registrados de este tipo (solo lectura para API). */
    private Long cantidad;

}
