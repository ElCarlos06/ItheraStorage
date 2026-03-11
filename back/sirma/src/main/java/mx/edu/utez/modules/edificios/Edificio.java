package mx.edu.utez.modules.edificios;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.kernel.BaseEntity;
import mx.edu.utez.modules.campus.Campus;

@Entity
@Table(name = "EDIFICIO")
@AttributeOverride(name = "id", column = @Column(name = "id_edificio"))
@Getter
@Setter
@NoArgsConstructor
public class Edificio extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_campus", nullable = false)
    private Campus campus;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "es_activo", nullable = false)
    private Boolean esActivo = true;

}

