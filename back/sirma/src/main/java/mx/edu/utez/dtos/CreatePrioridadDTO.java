package mx.edu.utez.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreatePrioridadDTO {

    private Character nivel;
    private String descripcion;
}
