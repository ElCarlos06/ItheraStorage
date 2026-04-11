package mx.edu.utez.modules.core.assets;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.modules.location.espacios.Espacio;
import mx.edu.utez.modules.core.tipo_activos.TipoActivo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data Transfer Object (DTO) para la entidad Activo.
 * Se utiliza para transferir datos hacia y desde el cliente, realizando validaciones básicas.
 *
 * @author Ithera Team
 */
@Getter
@Setter
@NoArgsConstructor
public class AssetsDTO {

    /** Identificador único del activo (nulo al crear). */
    private Long id;

    /** Etiqueta generada automáticamente. */
    @Size(max = 100)
    private String etiqueta;

    /** Número de serie proveniente del fabricante. */
    @NotBlank
    @Size(max = 100)
    private String numeroSerie;

    /** Identificador del Tipo de Activo. */
    @NotNull
    private Long idTipoActivo;

    /** Identificador del Espacio físico o ubicación actual. */
    @NotNull
    private Long idEspacio;

    /** Estado en el que se encuentra la custodia de este activo. */
    private String estadoCustodia;

    /** Estado operativo reflejando la salud física/técnica del activo. */
    private String estadoOperativo;

    /** Descripción general u observaciones sobre el bien. */
    private String descripcion;

    /** Valor adquisitivo u actual del bien. */
    private BigDecimal costo;

    /** Cadena leída desde un código QR. */
    private String qrCodigo;

    /** Fecha de alta en memoria principal, formato esperado 'yyyy-MM-dd'. */
    private String fechaAlta; // yyyy-MM-dd

    /** Booleano que define si el activo no ha sido descartado. */
    private Boolean esActivo;

    /** Nombre o identificador del empleado al que se le ha resguardado el bien. */
    private String asignadoA;

    /** Identificador del resguardo activo asociado, si existe. */
    private Long idResguardo;

    /** Objeto TipoActivo incrustado para visualización en el cliente. */
    private TipoActivo tipoActivo;

    /** Objeto Espacio incrustado para visualización en el cliente. */
    private Espacio espacio;

    // Campos de visualización para detalle extendido
    /** URLs de imágenes del perfil del activo */
    private List<String> imagenesPerfil;

    /** URLs de imágenes de reportes asociados */
    private List<String> imagenesReportes;

    /** URLs de imágenes de mantenimientos asociados */
    private List<String> imagenesMantenimientos;

}
