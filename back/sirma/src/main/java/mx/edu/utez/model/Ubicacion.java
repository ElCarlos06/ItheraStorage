package mx.edu.utez.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ubicacion")
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ubicacion")
    private Integer idUbicacion;

    @Column(name = "campus", nullable = false, length = 30)
    private String campus;

    @Column(name = "edificio", nullable = false, length = 20)
    private String edificio;

    @Column(name = "aula", length = 20)
    private String aula;

    @Column(name = "laboratorio", length = 20)
    private String laboratorio;

    @Column(name = "descripcion", length = 200)
    private String descripcion;
}
