package mx.edu.utez.modules.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO unificado para cambio de contraseña.
 * <p>
 * Dos modos según {@code primer_login} en el usuario:
 * <ul>
 *   <li><b>Con token</b> (enlace del correo): token + passwordNueva. No requiere contraseña actual.</li>
 *   <li><b>Con correo</b> (primer acceso): correo + passwordActual (temporal) + passwordNueva.</li>
 * </ul>
 */
@Getter
@Setter
@NoArgsConstructor
public class ChangePasswordDTO {

    /** Token del enlace (cuando viene de "olvidé mi contraseña"). */
    private String token;

    /** Correo (cuando es primer acceso con contraseña temporal). */
    @Email(message = "El correo debe tener un formato válido")
    private String correo;

    /** Contraseña actual/temporal (solo cuando es primer acceso). */
    private String passwordActual;

    /** Nueva contraseña (siempre obligatoria). */
    @NotBlank(message = "La nueva contraseña es obligatoria")
    private String passwordNueva;
}

