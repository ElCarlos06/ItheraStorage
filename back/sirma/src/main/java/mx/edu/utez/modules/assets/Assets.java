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

@Entity
@Table(name = "ACTIVO")
@AttributeOverride(name = "id", column = @Column(name = "id_activo"))
@Getter
@Setter
@NoArgsConstructor
public class Assets extends BaseEntity {

    @Column(name = "etiqueta", nullable = false, length = 50, unique = true)
    private String etiqueta;

    @Column(name = "numero_serie", nullable = false, length = 100, unique = true)
    private String numeroSerie;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_activo", nullable = false)
    private TipoActivo tipoActivo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_modelo", nullable = false)
    private Modelo modelo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_espacio", nullable = false)
    private Espacio espacio;

    @Column(name = "estado_custodia", nullable = false)
    private String estadoCustodia = "Disponible";

    @Column(name = "estado_operativo", nullable = false)
    private String estadoOperativo = "OK";

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
