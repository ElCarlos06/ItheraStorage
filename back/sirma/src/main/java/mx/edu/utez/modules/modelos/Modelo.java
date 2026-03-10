package mx.edu.utez.modules.modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.kernel.BaseEntity;
import mx.edu.utez.modules.marcas.Marca;

@Entity
@Table(name = "MODELO", uniqueConstraints = @UniqueConstraint(columnNames = {"id_marca", "nombre"}))
@AttributeOverride(name = "id", column = @Column(name = "id_modelo"))
@Getter
@Setter
@NoArgsConstructor
public class Modelo extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_marca", nullable = false)
    private Marca marca;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

}

