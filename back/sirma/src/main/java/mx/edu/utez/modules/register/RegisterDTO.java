package mx.edu.utez.modules.register;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de entrada para el registro de nuevos usuarios en SIRMA.
 * Las validaciones de formato de CURP, mayoría de edad y estándares de escritura
 * se realizan en el servicio para dar mensajes de error más descriptivos.
 *
 * @author Ithera Team
 */
@Getter
@Setter
@NoArgsConstructor
public class RegisterDTO {

    /** Nombre completo del usuario (sin caracteres especiales al inicio, sin puntos ni comas). */
    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(min = 3, max = 150, message = "El nombre completo debe tener entre 3 y 150 caracteres")
    private String nombreCompleto;

    /** Correo electrónico institucional — debe ser único. */
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato de correo no es válido")
    @Size(max = 100, message = "El correo no debe exceder 100 caracteres")
    private String correo;

    /** Fecha de nacimiento en formato yyyy-MM-dd — solo mayores de 18 años. */
    @NotBlank(message = "La fecha de nacimiento es obligatoria")
    private String fechaNacimiento;

    /** CURP con formato oficial mexicano (18 caracteres alfanuméricos). */
    @NotBlank(message = "La CURP es obligatoria")
    @Size(min = 18, max = 18, message = "La CURP debe tener exactamente 18 caracteres")
    private String curp;

    /** ID del rol asignado (Administrador, Empleado, Técnico). */
    @NotNull(message = "El rol es obligatorio")
    private Long idRol;

    /** ID del área institucional. */
    @NotNull(message = "El área es obligatoria")
    private Long idArea;

}
