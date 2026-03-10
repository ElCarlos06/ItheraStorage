package mx.edu.utez.modules.bitacora;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.kernel.BaseEntity;
import mx.edu.utez.modules.assets.Assets;
import mx.edu.utez.modules.users.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "BITACORA")
@AttributeOverride(name = "id", column = @Column(name = "id_bitacora"))
@Getter
@Setter
@NoArgsConstructor
public class Bitacora extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_activo", nullable = false)
    private Assets activo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private User usuario;

    @Column(name = "tipo_evento", nullable = false)
    private String tipoEvento;

    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "estado_custodia_anterior", length = 50)
    private String estadoCustodiaAnterior;

    @Column(name = "estado_custodia_nuevo", length = 50)
    private String estadoCustodiaNuevo;

    @Column(name = "estado_operativo_anterior", length = 50)
    private String estadoOperativoAnterior;

    @Column(name = "estado_operativo_nuevo", length = 50)
    private String estadoOperativoNuevo;

    @Column(name = "fecha_evento", nullable = false, insertable = false, updatable = false)
    private LocalDateTime fechaEvento;

}

