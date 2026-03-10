package mx.edu.utez.modules.modelos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ModeloDTO {

    private Long id;

    @NotNull
    private Long idMarca;

    @NotBlank
    @Size(max = 100)
    private String nombre;

}

