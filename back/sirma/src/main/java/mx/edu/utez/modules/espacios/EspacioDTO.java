package mx.edu.utez.modules.espacios;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EspacioDTO {

    private Long id;

    @NotNull
    private Long idEdificio;

    @NotBlank
    @Size(max = 100)
    private String nombreEspacio;

    @Size(max = 50)
    private String tipoEspacio;

    @Size(max = 255)
    private String descripcion;

    private Boolean esActivo;

}

