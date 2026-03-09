package mx.edu.utez.modules.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de entrada para realizar login.
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
