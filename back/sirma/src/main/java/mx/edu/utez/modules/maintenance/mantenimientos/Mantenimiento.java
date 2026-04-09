package mx.edu.utez.modules.maintenance.mantenimientos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.kernel.BaseEntity;
import mx.edu.utez.modules.core.assets.Assets;
import mx.edu.utez.modules.maintenance.prioridades.Prioridad;
import mx.edu.utez.modules.reporting.reportes.Reporte;
import mx.edu.utez.modules.security.users.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que registra los Mantenimientos realizados a los activos.
 * Vincula un reporte de falla con la ejecución técnica de la solución.
 *
 * @author Ithera Team
 */
@Entity
@Table(name = "MANTENIMIENTO")
@AttributeOverride(name = "id", column = @Column(name = "id_mantenimiento"))
@Getter
@Setter
@NoArgsConstructor
public class Mantenimiento extends BaseEntity {

    /** Reporte origen que detonó el mantenimiento. */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_reporte", nullable = false, unique = true)
    private Reporte reporte;

    /** Activo al que se le realiza el mantenimiento. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_activo", nullable = false)
    private Assets activo;

    /** Técnico asignado para ejecutar el mantenimiento. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario_tecnico", nullable = false)
    private User usuarioTecnico;

    /** Administrador que autorizó o supervisó el mantenimiento. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario_admin", nullable = false)
    private User usuarioAdmin;

    /** Prioridad de ejecución asignada. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_prioridad", nullable = false)
    private Prioridad prioridad;

    /** Tipo de mantenimiento programado (Correctivo/Preventivo). */
    @Column(name = "tipo_asignado", nullable = false)
    private String tipoAsignado; // Correctivo | Preventivo

    /** Tipo de mantenimiento que realmente se ejecutó. */
    @Column(name = "tipo_ejecutado")
    private String tipoEjecutado; // Correctivo | Preventivo (null hasta cierre)

    /** Diagnóstico técnico del problema. */
    @Column(name = "diagnostico", columnDefinition = "TEXT")
    private String diagnostico;

    /** Acciones realizadas durante el mantenimiento. */
    @Column(name = "acciones_realizadas", columnDefinition = "TEXT")
    private String accionesRealizadas;

    /** Piezas utilizadas o refacciones que se requirieron en el mantenimiento. */
    @Column(name = "piezas_utilizadas", columnDefinition = "TEXT")
    private String piezasUtilizadas;

    /** Conclusión determinando la vida del activo (Reparado/Irreparable). */
    @Column(name = "conclusion")
    private String conclusion; // Reparado | Irreparable (null hasta cierre)

    /** Observaciones adicionales relativas al proceso y las maniobras. */
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    /** Costo monetario estimado e insumido asociado al mantenimiento llevado a cabo. */
    @Column(name = "costo", precision = 10, scale = 2)
    private BigDecimal costo;

    /** Instante de tiempo o inicio explícito en que se arrancaron gestiones del mantenimiento. */
    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    /** Instante de tiempo marcando el cierre o la culminación y resolución del mantenimiento. */
    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    /** Estado de progresión general de esta reparación (Asignado, En Proceso, Finalizado). */
    @Column(name = "estado_mantenimiento", nullable = false)
    private String estadoMantenimiento = "Asignado"; // Asignado  En Proceso  Finalizado

}
