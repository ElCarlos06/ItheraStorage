package mx.edu.utez.modules.users;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.kernel.BaseEntity;
import mx.edu.utez.modules.areas.Area;
import mx.edu.utez.modules.roles.Role;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "USUARIO")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {

    @Column(name = "numero_empleado", nullable = false, length = 30)
    private String numeroEmpleado;

    @Column(name = "nombres", nullable = false, length = 50)
    private String nombres;

    @Column(name = "apellido_paterno", nullable = false, length = 50)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", nullable = false, length = 50)
    private String apellidoMaterno;

    @Column(nullable = false, length = 150)
    private String correo;

    @Column(name = "fecha_nacimiento")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;

    @Column(name = "curp", length = 18)
    private String curp;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_role", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_area", nullable = false)
    private Area area;

}
