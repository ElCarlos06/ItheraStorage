package mx.edu.utez.modules.edificios;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EdificioDTO {

    private Long id;

    @NotNull
    private Long idCampus;

    @NotBlank
    @Size(max = 100)
    private String nombre;

    private Boolean esActivo;

}

