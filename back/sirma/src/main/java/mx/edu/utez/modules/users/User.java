package mx.edu.utez.modules.users;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.kernel.BaseEntity;
import mx.edu.utez.modules.areas.Area;
import mx.edu.utez.modules.roles.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa a un usuario del sistema SIRMA.
 * Mapea a la tabla USUARIO con campos de autenticación y perfil institucional.
 *
 * @author Ithera Team
 */
@Entity
@Table(name = "USUARIO")
@AttributeOverride(name = "id", column = @Column(name = "id_usuario"))
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {

    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String nombreCompleto;

    @Column(name = "correo", nullable = false, length = 100, unique = true)
    private String correo;

    @Column(name = "curp", nullable = false, length = 18, unique = true)
    private String curp;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(name = "numero_empleado", nullable = false, length = 20, unique = true)
    private String numeroEmpleado;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_area", nullable = false)
    private Area area;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "primer_login", nullable = false)
    private Boolean primerLogin = true;

    @Column(name = "es_activo", nullable = false)
    private Boolean esActivo = true;

    @Column(name = "creado_en", updatable = false, insertable = false)
    private LocalDateTime creadoEn;

}
