package mx.edu.utez.modules.assets;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.kernel.BaseEntity;
import mx.edu.utez.modules.modelos.Modelo;
import mx.edu.utez.modules.tipo_activos.TipoActivo;
import mx.edu.utez.modules.espacios.Espacio;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad que representa un Activo Fijo dentro de la organización.
 * Extiende de BaseEntity para heredar el ID autogenerado.
 *
 * @author Ithera Team
 */
@Entity
@Table(name = "ACTIVO")
@AttributeOverride(name = "id", column = @Column(name = "id_activo"))
@Getter
@Setter
@NoArgsConstructor
public class Assets extends BaseEntity {

    /** Etiqueta única de identificación interna del activo. */
    @Column(name = "etiqueta", nullable = false, length = 50, unique = true)
    private String etiqueta;

    /** Número de serie del fabricante. */
    @Column(name = "numero_serie", nullable = false, length = 100, unique = true)
    private String numeroSerie;

    /** Tipo de activo (ej. Computadora, Mobiliario, Vehículo). */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_activo", nullable = false)
    private TipoActivo tipoActivo;

    /** Modelo del activo. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_modelo", nullable = false)
    private Modelo modelo;

    /** Ubicación física actual del activo. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_espacio", nullable = false)
    private Espacio espacio;

    /** Estado de custodia (ej. Disponible, Asignado, En Mantenimiento). */
    @Column(name = "estado_custodia", nullable = false)
    private String estadoCustodia = "Disponible";

    /** Condición operativa del activo (ej. OK, Falla, Obsoleto). */
    @Column(name = "estado_operativo", nullable = false)
    private String estadoOperativo = "OK";

    /** Descripción detallada del activo. */
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "costo", precision = 10, scale = 2)
    private BigDecimal costo;

    @Column(name = "qr_codigo", length = 255, unique = true)
    private String qrCodigo;

    @Column(name = "fecha_alta", nullable = false)
    private LocalDate fechaAlta;

    @Column(name = "es_activo", nullable = false)
    private Boolean esActivo = true;

}
