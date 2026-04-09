package mx.edu.utez.modules.security.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.kernel.BaseEntity;
import mx.edu.utez.modules.location.areas.Area;
import mx.edu.utez.modules.security.roles.Role;

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
@Getter @Setter
@NoArgsConstructor
public class User extends BaseEntity {

    /** Nombre completo del usuario administrativo o personal. */
    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String nombreCompleto;

    /** Correo institucional usado para login y notificaciones. */
    @Column(name = "correo", nullable = false, length = 100, unique = true)
    private String correo;

    /** Clave Única de Registro de Población. */
    @Column(name = "curp", nullable = false, length = 18, unique = true)
    private String curp;

    /** Fecha de nacimiento del usuario. */
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    /** Número de empleado o control interno. */
    @Column(name = "numero_empleado", nullable = false, length = 20, unique = true)
    private String numeroEmpleado;

    /** Rol asignado que define los permisos en el sistema. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol", nullable = false)
    private Role role;

    /** Área o departamento de adscripción. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_area", nullable = false)
    private Area area;

    @JsonIgnore
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "primer_login", nullable = false)
    private Boolean primerLogin = true;

    @Column(name = "es_activo", nullable = false)
    private Boolean esActivo = true;

    @Column(name = "creado_en", updatable = false, insertable = false)
    private LocalDateTime creadoEn;

}
