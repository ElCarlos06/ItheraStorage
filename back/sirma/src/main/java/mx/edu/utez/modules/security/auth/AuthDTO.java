package mx.edu.utez.modules.security.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de entrada para realizar login en SIRMA.
 * Contiene las credenciales necesarias para autenticación.
 *
 * @author Ithera Team
 */
@Getter
@Setter
@NoArgsConstructor
public class AuthDTO {

    /** Correo institucional del usuario. */
    @NotBlank
    @Email
    private String correo;

    /** Contraseña plana enviada para autenticación. */
    @NotBlank
    private String password;
}
