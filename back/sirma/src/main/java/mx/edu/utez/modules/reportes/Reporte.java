package mx.edu.utez.modules.reportes;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.kernel.BaseEntity;
import mx.edu.utez.modules.assets.Assets;
import mx.edu.utez.modules.prioridades.Prioridad;
import mx.edu.utez.modules.tipo_fallas.TipoFalla;
import mx.edu.utez.modules.users.User;

import java.time.LocalDateTime;

/**
 * Entidad que representa un Reporte de incidencia o falla sobre un activo.
 * Inicia el flujo de atención para reparaciones.
 *
 * @author Ithera Team
 */
@Entity
@Table(name = "REPORTE")
@AttributeOverride(name = "id", column = @Column(name = "id_reporte"))
@Getter
@Setter
@NoArgsConstructor
public class Reporte extends BaseEntity {

    /** Activo sobre el cual se reporta la incidencia. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_activo", nullable = false)
    private Assets activo;

    /** Usuario que genera el reporte. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario_reporta", nullable = false)
    private User usuarioReporta;

    /** Categoría de la falla reportada. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_falla", nullable = false)
    private TipoFalla tipoFalla;

    /** Nivel de urgencia del reporte. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_prioridad", nullable = false)
    private Prioridad prioridad;

    /** Descripción detallada del problema. */
    @Column(name = "descripcion_falla", nullable = false, columnDefinition = "TEXT")
    private String descripcionFalla;

    /** Estado actual del reporte (Pendiente, En Proceso, Resuelto, etc.). */
    @Column(name = "estado_reporte", nullable = false)
    private String estadoReporte = "Pendiente"; // Pendiente | En Proceso | Resuelto | Cancelado

    /** Fecha de creación del reporte (automática). */
    @Column(name = "fecha_reporte", insertable = false, updatable = false)
    private LocalDateTime fechaReporte;

    /** Columna de control para optimización o lógica de negocio específica (solo lectura). */
    @Column(name = "centinela_activo", insertable = false, updatable = false)
    private Integer centinelaActivo;

    /** Nombre del técnico si ya existe mantenimiento asignado (solo respuesta API, no persistido). */
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Transient
    private String nombreTecnicoAsignado;

    @JsonProperty("nombreTecnicoAsignado")
    public String getNombreTecnicoAsignado() {
        return nombreTecnicoAsignado;
    }

    @JsonProperty("nombreTecnicoAsignado")
    public void setNombreTecnicoAsignado(String nombreTecnicoAsignado) {
        this.nombreTecnicoAsignado = nombreTecnicoAsignado;
    }

}
