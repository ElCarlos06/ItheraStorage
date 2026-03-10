package mx.edu.utez.modules.users;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para operaciones CRUD de usuarios en SIRMA.
 * Contiene validaciones para asegurar integridad de datos.
 *
 * @author Ithera Team
 */
@Getter
@Setter
@NoArgsConstructor
public class UserDTO {

    private Long id;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(min = 3, max = 150, message = "El nombre completo debe tener entre 3 y 150 caracteres")
    private String nombreCompleto;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato de correo no es válido")
    @Size(max = 100, message = "El correo no debe exceder 100 caracteres")
    private String correo;

    @NotBlank(message = "La CURP es obligatoria")
    @Size(min = 18, max = 18, message = "La CURP debe tener exactamente 18 caracteres")
    private String curp;

    @NotBlank(message = "La fecha de nacimiento es obligatoria")
    private String fechaNacimiento; // yyyy-MM-dd

    @Size(max = 20, message = "El número de empleado no debe exceder 20 caracteres")
    private String numeroEmpleado;

    @NotNull(message = "El rol es obligatorio")
    private Long idRol;

    @NotNull(message = "El área es obligatoria")
    private Long idArea;

    private String password;

    private Boolean primerLogin;

    private Boolean esActivo;

}
