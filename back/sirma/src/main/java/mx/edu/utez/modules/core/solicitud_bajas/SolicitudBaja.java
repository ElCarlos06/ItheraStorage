package mx.edu.utez.modules.core.solicitud_bajas;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.kernel.BaseEntity;
import mx.edu.utez.modules.core.assets.Assets;
import mx.edu.utez.modules.maintenance.mantenimientos.Mantenimiento;
import mx.edu.utez.modules.security.users.User;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa una solicitud de baja definitiva o temporal de un activo.
 * Mapea a la tabla SOLICITUD_BAJA.
 *
 * @author Ithera Team
 */
@Entity
@Table(name = "SOLICITUD_BAJA")
@AttributeOverride(name = "id", column = @Column(name = "id_solicitud_baja"))
@Getter
@Setter
@NoArgsConstructor
public class SolicitudBaja extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_activo", nullable = false)
    private Assets activo;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_mantenimiento", nullable = false, unique = true)
    private Mantenimiento mantenimiento;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario_admin")
    private User usuarioAdmin;

    @Column(name = "estado", nullable = false)
    private String estado = "Pendiente"; // Pendiente | Aprobada | Rechazada

    @Column(name = "justificacion", columnDefinition = "TEXT")
    private String justificacion;

    @Column(name = "observaciones_admin", columnDefinition = "TEXT")
    private String observacionesAdmin;

    @Column(name = "fecha_solicitud", insertable = false, updatable = false)
    private LocalDateTime fechaSolicitud;

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

    // Columna generada por MySQL — solo lectura
    @Column(name = "centinela_pendiente", insertable = false, updatable = false)
    private Integer centinelaPendiente;

}
