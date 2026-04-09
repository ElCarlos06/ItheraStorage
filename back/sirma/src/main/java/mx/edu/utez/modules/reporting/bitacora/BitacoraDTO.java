package mx.edu.utez.modules.reporting.bitacora;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) correspondiente a la entidad Bitácora.
 * Encapsula los campos enviados durante la creación y consulta de eventos
 * de forma simplificada a fin de no exponer entidades enteras JPA.
 *
 * @author Ithera Team
 */
@Getter
@Setter
@NoArgsConstructor
public class BitacoraDTO {

    /** Identificador único del evento (nulo para creación). */
    private Long id;

    /** Referencia de id del activo manipulado. */
    @NotNull
    private Long idActivo;

    /** Referencia de id del usuario responsable del evento. */
    @NotNull
    private Long idUsuario;

    /** Clasificación del evento registrado. */
    @NotBlank
    private String tipoEvento;

    /** Nivel de descripción del cambio realizado sobre el estado. */
    @NotBlank
    private String descripcion;

    /** Valor del estado custodia antes de la ejecución. */
    private String estadoCustodiaAnterior;
    /** Valor del estado custodia luego de ejecutar el movimiento. */
    private String estadoCustodiaNuevo;
    /** Valor del estado operativo antes del requerimiento. */
    private String estadoOperativoAnterior;
    /** Valor del estado operativo ya finalizada la inyección. */
    private String estadoOperativoNuevo;

}
