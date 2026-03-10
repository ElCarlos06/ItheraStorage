package mx.edu.utez.modules.reportes;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReporteDTO {

    private Long id;

    @NotNull
    private Long idActivo;

    @NotNull
    private Long idUsuarioReporta;

    @NotNull
    private Long idTipoFalla;

    @NotNull
    private Long idPrioridad;

    @NotBlank
    private String descripcionFalla;

    private String estadoReporte;

}

