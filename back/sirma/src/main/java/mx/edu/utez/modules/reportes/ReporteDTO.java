package mx.edu.utez.modules.reportes;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para la capa de presentación que recibe y transfiere información de reportes de daños o fallas.
 * Empleado para aislar la entidad Reporte de la petición web.
 *
 * @author Ithera Team
 */
@Getter
@Setter
@NoArgsConstructor
public class ReporteDTO {

    /** Identificador general numérico. */
    private Long id;

    /** Referencia del activo físico sobre el que gira el percance. */
    @NotNull
    private Long idActivo;

    /** Remitente o usuario que experimentó y levantó este incidente. */
    @NotNull
    private Long idUsuarioReporta;

    /** Tipología que clasifica los síntomas del problema. */
    @NotNull
    private Long idTipoFalla;

    /** Rango de emergencia para estipular la atención al evento. */
    @NotNull
    private Long idPrioridad;

    /** Detalles extra aportados por el usuario respecto a lo sucedido. */
    @NotBlank
    private String descripcionFalla;

    /** Estatus o ciclo de vida de este reporte. */
    private String estadoReporte;

}
