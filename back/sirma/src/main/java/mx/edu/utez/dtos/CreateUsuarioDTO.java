package mx.edu.utez.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.model.Rol;

@Getter
@Setter
@NoArgsConstructor
public class CreateUsuarioDTO {

    private Rol rol;
    private String matriculaEmpleado;
    private String nombreCompleto;
    private String apellidos;
    private String curp;
    private Character estatus;
    private String correo;
    private String contrasenia;
    private String area;
}
