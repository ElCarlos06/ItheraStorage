package mx.edu.utez.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "prioridad")
public class Prioridad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prioridad")
    private Integer idPrioridad;

    @Column(name = "nivel", nullable = false, columnDefinition = "CHAR(1)")
    private Character nivel;

    @Column(name = "descripcion", length = 200)
    private String descripcion;
}
