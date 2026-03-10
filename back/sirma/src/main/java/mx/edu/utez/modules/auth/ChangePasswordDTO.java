package mx.edu.utez.modules.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para el flujo de cambio de contraseña en el primer acceso del usuario.
 * <p>
 * Se utiliza en el endpoint {@code POST /api/auth/change-password}.
 * El usuario debe proporcionar su correo, la contraseña temporal recibida por correo
 * y la nueva contraseña que desea establecer.
 * </p>
 *
 * @author Ithera Team
 */
@Getter
@Setter
@NoArgsConstructor
public class ChangePasswordDTO {

    /** Correo institucional del usuario que cambia su contraseña. */
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe tener un formato válido")
    private String correo;

    /** Contraseña temporal generada automáticamente y enviada al correo. */
    @NotBlank(message = "La contraseña actual es obligatoria")
    private String passwordActual;

    /** Nueva contraseña elegida por el usuario (debe cumplir estándares de seguridad). */
    @NotBlank(message = "La nueva contraseña es obligatoria")
    private String passwordNueva;
}

