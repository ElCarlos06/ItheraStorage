package mx.edu.utez.modules.mantenimientos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.kernel.BaseEntity;
import mx.edu.utez.modules.assets.Assets;
import mx.edu.utez.modules.prioridades.Prioridad;
import mx.edu.utez.modules.reportes.Reporte;
import mx.edu.utez.modules.users.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "MANTENIMIENTO")
@AttributeOverride(name = "id", column = @Column(name = "id_mantenimiento"))
@Getter
@Setter
@NoArgsConstructor
public class Mantenimiento extends BaseEntity {

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_reporte", nullable = false, unique = true)
    private Reporte reporte;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_activo", nullable = false)
    private Assets activo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario_tecnico", nullable = false)
    private User usuarioTecnico;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario_admin", nullable = false)
    private User usuarioAdmin;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_prioridad", nullable = false)
    private Prioridad prioridad;

    @Column(name = "tipo_asignado", nullable = false)
    private String tipoAsignado; // Correctivo | Preventivo

    @Column(name = "tipo_ejecutado")
    private String tipoEjecutado; // Correctivo | Preventivo (null hasta cierre)

    @Column(name = "diagnostico", columnDefinition = "TEXT")
    private String diagnostico;

    @Column(name = "acciones_realizadas", columnDefinition = "TEXT")
    private String accionesRealizadas;

    @Column(name = "piezas_utilizadas", columnDefinition = "TEXT")
    private String piezasUtilizadas;

    @Column(name = "conclusion")
    private String conclusion; // Reparado | Irreparable (null hasta cierre)

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "costo", precision = 10, scale = 2)
    private BigDecimal costo;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(name = "estado_mantenimiento", nullable = false)
    private String estadoMantenimiento = "Asignado"; // Asignado | En Proceso | Finalizado

}

