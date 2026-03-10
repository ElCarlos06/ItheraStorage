package mx.edu.utez.modules.mantenimientos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class MantenimientoDTO {

    private Long id;

    @NotNull
    private Long idReporte;

    @NotNull
    private Long idActivo;

    @NotNull
    private Long idUsuarioTecnico;

    @NotNull
    private Long idUsuarioAdmin;

    @NotNull
    private Long idPrioridad;

    @NotBlank
    private String tipoAsignado; // Correctivo | Preventivo

    private String tipoEjecutado;
    private String diagnostico;
    private String accionesRealizadas;
    private String piezasUtilizadas;
    private String conclusion;
    private String observaciones;
    private BigDecimal costo;
    private String estadoMantenimiento;

}

