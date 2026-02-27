package mx.edu.utez.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol")
    private Rol rol;

    @Column(name = "matricula_empleado", unique = true, nullable = false, length = 20)
    private String matriculaEmpleado;

    @Column(name = "nombre_completo", nullable = false, length = 50)
    private String nombreCompleto;

    @Column(name = "apellidos", nullable = false, length = 30)
    private String apellidos;

    @Column(name = "curp", unique = true, nullable = false, length = 18)
    private String curp;

    @Column(name = "estatus", nullable = false, columnDefinition = "CHAR(1)")
    private Character estatus;

    @Column(name = "correo", unique = true, nullable = false, length = 50)
    private String correo;

    @Column(name = "contrasenia", nullable = false, length = 255)
    private String contrasenia;

    @Column(name = "area", nullable = false, length = 50)
    private String area;
}
