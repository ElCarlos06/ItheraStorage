package mx.edu.utez.modules.bitacora;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.kernel.BaseEntity;
import mx.edu.utez.modules.assets.Assets;
import mx.edu.utez.modules.users.User;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa un registro en la Bitácora de movimientos.
 * Se utiliza para llevar un historial detallado de los cambios de estado
 * y eventos importantes relacionados con los activos y la intervención de los usuarios.
 *
 * @author Ithera Team
 */
@Entity
@Table(name = "BITACORA")
@AttributeOverride(name = "id", column = @Column(name = "id_bitacora"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bitacora extends BaseEntity {

    /** Activo (Assets) asociado a este evento de bitácora. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_activo", nullable = false)
    private Assets activo;

    /** Usuario responsable de realizar la acción que detonó el evento. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private User usuario;

    /** Título o clasificación del tipo de evento ocurrido (ej. 'Creación', 'Baja', etc). */
    @Column(name = "tipo_evento", nullable = false)
    private String tipoEvento;

    /** Descripción detallada o justificación del movimiento registrado. */
    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    /** Estado de custodia que poseía el bien antes del evento. */
    @Column(name = "estado_custodia_anterior", length = 50)
    private String estadoCustodiaAnterior;

    /** Nuevo estado de custodia asimilado tras el evento. */
    @Column(name = "estado_custodia_nuevo", length = 50)
    private String estadoCustodiaNuevo;

    /** Estado operativo anterior que conservaba el activo antes del evento. */
    @Column(name = "estado_operativo_anterior", length = 50)
    private String estadoOperativoAnterior;

    /** Diferente estado operativo resultante luego de efectuar el evento. */
    @Column(name = "estado_operativo_nuevo", length = 50)
    private String estadoOperativoNuevo;

    /** Fecha y hora de transacción estampada automáticamente por la BD. */
    @Column(name = "fecha_evento", nullable = false, insertable = false, updatable = false)
    private LocalDateTime fechaEvento;

}
