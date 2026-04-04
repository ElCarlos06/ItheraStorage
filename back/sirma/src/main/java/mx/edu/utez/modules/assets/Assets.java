package mx.edu.utez.modules.assets;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.kernel.BaseEntity;
import mx.edu.utez.modules.tipo_activos.TipoActivo;
import mx.edu.utez.modules.espacios.Espacio;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    /** Ubicación física actual del activo. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_espacio", nullable = false)
    private Espacio espacio;

    /** estado_custodia: quién tiene el bien físico (no es lo mismo que operativo). */
    @Column(name = "estado_custodia", nullable = false, length = 50)
    private String estadoCustodia = "Disponible";

    /** estado_operativo: daño, taller o baja; independiente de custodia. */
    @Column(name = "estado_operativo", nullable = false, length = 50)
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
