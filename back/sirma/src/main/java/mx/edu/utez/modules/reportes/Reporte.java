package mx.edu.utez.modules.reportes;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.kernel.BaseEntity;
import mx.edu.utez.modules.assets.Assets;
import mx.edu.utez.modules.prioridades.Prioridad;
import mx.edu.utez.modules.tipo_fallas.TipoFalla;
import mx.edu.utez.modules.users.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "REPORTE")
@AttributeOverride(name = "id", column = @Column(name = "id_reporte"))
@Getter
@Setter
@NoArgsConstructor
public class Reporte extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_activo", nullable = false)
    private Assets activo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario_reporta", nullable = false)
    private User usuarioReporta;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_falla", nullable = false)
    private TipoFalla tipoFalla;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_prioridad", nullable = false)
    private Prioridad prioridad;

    @Column(name = "descripcion_falla", nullable = false, columnDefinition = "TEXT")
    private String descripcionFalla;

    @Column(name = "estado_reporte", nullable = false)
    private String estadoReporte = "Pendiente"; // Pendiente | En Proceso | Resuelto | Cancelado

    @Column(name = "fecha_reporte", insertable = false, updatable = false)
    private LocalDateTime fechaReporte;

    // Columna generada por MySQL — solo lectura
    @Column(name = "centinela_activo", insertable = false, updatable = false)
    private Integer centinelaActivo;

}

