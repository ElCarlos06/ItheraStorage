package mx.edu.utez.modules.core.resguardos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.kernel.BaseEntity;
import mx.edu.utez.modules.core.assets.Assets;
import mx.edu.utez.modules.security.users.User;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa un Resguardo (asignación formal de un activo a un usuario) en SIRMA.
 * Mapea a la tabla RESGUARDO.
 *
 * @author Ithera Team
 */
@Entity
@Table(name = "RESGUARDO")
@AttributeOverride(name = "id", column = @Column(name = "id_resguardo"))
@Getter
@Setter
@NoArgsConstructor
public class Resguardo extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_activo", nullable = false)
    private Assets activo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario_empleado", nullable = false)
    private User usuarioEmpleado;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario_admin", nullable = false)
    private User usuarioAdmin;

    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDateTime fechaAsignacion;

    @Column(name = "fecha_confirmacion")
    private LocalDateTime fechaConfirmacion;

    @Column(name = "fecha_devolucion")
    private LocalDateTime fechaDevolucion;

    @Column(name = "observaciones_asig", columnDefinition = "TEXT")
    private String observacionesAsig;

    @Column(name = "observaciones_conf", columnDefinition = "TEXT")
    private String observacionesConf;

    @Column(name = "observaciones_dev", columnDefinition = "TEXT")
    private String observacionesDev;

    @Column(name = "estado_resguardo", nullable = false)
    private String estadoResguardo = "Pendiente"; // Pendiente | Confirmado | Devuelto

    // Columna generada por MySQL — solo lectura en JPA
    @Column(name = "centinela_activo", insertable = false, updatable = false)
    private Integer centinelaActivo;

}
