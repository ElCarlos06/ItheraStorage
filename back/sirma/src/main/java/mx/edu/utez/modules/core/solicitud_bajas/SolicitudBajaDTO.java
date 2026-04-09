package mx.edu.utez.modules.core.solicitud_bajas;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Objeto de Transferencia de Datos (DTO) para la entidad SolicitudBaja.
 *
 * @author Ithera Team
 */
@Getter
@Setter
@NoArgsConstructor
public class SolicitudBajaDTO {

    private Long id;

    @NotNull
    private Long idActivo;

    @NotNull
    private Long idMantenimiento;

    private Long idUsuarioAdmin;
    private String estado;
    private String justificacion;
    private String observacionesAdmin;

}

