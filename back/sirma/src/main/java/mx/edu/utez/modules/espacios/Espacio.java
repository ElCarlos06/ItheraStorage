package mx.edu.utez.modules.espacios;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.kernel.BaseEntity;
import mx.edu.utez.modules.edificios.Edificio;

@Entity
@Table(name = "ESPACIO", uniqueConstraints = @UniqueConstraint(columnNames = {"id_edificio", "nombre_espacio"}))
@AttributeOverride(name = "id", column = @Column(name = "id_espacio"))
@Getter
@Setter
@NoArgsConstructor
public class Espacio extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_edificio", nullable = false)
    private Edificio edificio;

    @Column(name = "nombre_espacio", nullable = false, length = 100)
    private String nombreEspacio;

    @Column(name = "tipo_espacio", length = 50)
    private String tipoEspacio;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "es_activo", nullable = false)
    private Boolean esActivo = true;

}

