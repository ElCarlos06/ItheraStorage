package mx.edu.utez.modules.tipo_activos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private Boolean esActivo;

}

