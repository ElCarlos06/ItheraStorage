package mx.edu.utez.modules.security.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para solicitar el restablecimiento de contraseña.
 * El backend genera una nueva contraseña temporal y la envía por correo.
 */
@Getter
@Setter
@NoArgsConstructor
public class RequestPasswordResetDTO {

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe tener un formato válido")
    private String correo;
}
