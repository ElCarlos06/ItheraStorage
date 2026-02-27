package mx.edu.utez.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateUbicacionDTO {

    private String campus;
    private String edificio;
    private String aula;
    private String laboratorio;
    private String descripcion;
}
