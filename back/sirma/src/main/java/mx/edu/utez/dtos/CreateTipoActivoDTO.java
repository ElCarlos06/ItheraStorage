package mx.edu.utez.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateTipoActivoDTO {

    private String nombre;
    private String marca;
    private String modelo;
}
