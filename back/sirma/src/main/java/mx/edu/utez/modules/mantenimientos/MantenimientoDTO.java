package mx.edu.utez.modules.mantenimientos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO que canaliza la información transaccional y los requerimientos o resoluciones de un trabajo de Mantenimiento.
 *
 * @author Ithera Team
 */
@Getter
@Setter
@NoArgsConstructor
public class MantenimientoDTO {

    /** Identificador del propio mantenimiento. */
    private Long id;

    /** Relación directa al reporte u orden que disparó el proceso. */
    @NotNull
    private Long idReporte;

    /** Clave del activo manipulado o en reparación. */
    @NotNull
    private Long idActivo;

    /** Trabajador al que se le comisiona la compostura. */
    @NotNull
    private Long idUsuarioTecnico;

    /** Clave del superior que da el alta y seguimiento. */
    @NotNull
    private Long idUsuarioAdmin;

    /** Urgencia escalada. */
    @NotNull
    private Long idPrioridad;

    /** Naturaleza de mantenimiento instruida de antemano. */
    @NotBlank
    private String tipoAsignado; // Correctivo  Preventivo

    /** Naturaleza del mantenimiento concluido o efectuado cabalmente. */
    private String tipoEjecutado;

    /** Nivel de diagnóstico y exploración final del aparato. */
    private String diagnostico;

    /** Descripción de composturas logradas. */
    private String accionesRealizadas;

    /** Repuestos adquiridos. */
    private String piezasUtilizadas;

    /** Veredicto final del producto. */
    private String conclusion;

    /** Notas extra o bitácora libre del operador técnico. */
    private String observaciones;

    /** Gasto general a nivel monetario de todo el arreglo. */
    private BigDecimal costo;

    /** Nivel de vida de la transacción de mantenimiento. */
    private String estadoMantenimiento;

}
