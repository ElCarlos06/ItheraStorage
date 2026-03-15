package mx.edu.utez.modules.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para enviar la información del usuario autenticado al frontend de forma segura.
 * Evita exponer la entidad completa y sus relaciones internas.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {

    private Long id;
    private String nombreCompleto;
    private String correo;
    private String rol;
    private String area;
    private String numeroEmpleado;

}

