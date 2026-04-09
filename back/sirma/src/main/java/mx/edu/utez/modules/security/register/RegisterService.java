package mx.edu.utez.modules.security.register;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.location.areas.Area;
import mx.edu.utez.modules.location.areas.AreaRepository;
import mx.edu.utez.modules.security.roles.Role;
import mx.edu.utez.modules.security.roles.RoleRepository;
import mx.edu.utez.modules.security.users.User;
import mx.edu.utez.modules.security.users.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;
import java.util.regex.Pattern;

import static mx.edu.utez.util.PasswordUtils.generarPasswordTemporal;

/**
 * Servicio de registro de usuarios.
 * Aplica todas las reglas de negocio definidas en el DFR §1.2:
 * <ul>
 *   <li>CURP con formato oficial mexicano</li>
 *   <li>Correo y CURP únicos</li>
 *   <li>Solo mayores de 18 años</li>
 *   <li>Estándares de escritura (sin puntos al inicio, comas, caracteres especiales)</li>
 *   <li>Generación automática de número de empleado</li>
 *   <li>Generación y envío de contraseña temporal por correo</li>
 * </ul>
 *
 * @author Ithera Team
 */
@Service
@AllArgsConstructor
public class RegisterService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AreaRepository areaRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    private static final Pattern CURP_PATTERN = Pattern.compile(
            "^[A-Z]{4}\\d{6}[HM][A-Z]{2}[B-DF-HJ-NP-TV-Z]{3}[A-Z0-9]\\d$"
    );

    // Nombre: solo letras (con acentos), espacios y apóstrofos. No inicia con espacio, punto, coma o carácter especial.
    private static final Pattern NOMBRE_PATTERN = Pattern.compile(
            "^[A-ZÁÉÍÓÚÜÑa-záéíóúüñ][A-ZÁÉÍÓÚÜÑa-záéíóúüñ ']*$"
    );

    /**
     * Registra un nuevo usuario aplicando todas las validaciones de negocio.
     *
     * @param dto datos del usuario a registrar
     * @return ApiResponse con resultado de la operación
     */
    @Transactional
    public ApiResponse register(RegisterDTO dto) {
        try {
            return doRegister(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(
                    "Error al registrar: " + (e.getMessage() != null ? e.getMessage() : "Intenta más tarde."),
                    true, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ApiResponse doRegister(RegisterDTO dto) {
        // ── 1. Validar estándares de escritura del nombre ──────────────────
        String nombre = dto.getNombreCompleto().trim();
        if (!NOMBRE_PATTERN.matcher(nombre).matches()) {
            return new ApiResponse(
                    "El nombre no cumple con los estándares de escritura: no debe iniciar con puntos, comas ni caracteres especiales, y solo puede contener letras y espacios",
                    true, HttpStatus.BAD_REQUEST);
        }

        // ── 2. Validar formato CURP ────────────────────────────────────────
        String curp = dto.getCurp().trim().toUpperCase();
        if (!CURP_PATTERN.matcher(curp).matches()) {
            return new ApiResponse(
                    "La CURP no cumple con el formato oficial mexicano (18 caracteres alfanuméricos con estructura válida)",
                    true, HttpStatus.BAD_REQUEST);
        }

        // ── 3. Validar unicidad de correo (activo) o reactivar inactivo ──────
        String correo = dto.getCorreo().trim().toLowerCase();
        Optional<User> existenteCorreo = userRepository.findByCorreoIgnoreCase(correo);
        if (existenteCorreo.isPresent() && Boolean.TRUE.equals(existenteCorreo.get().getEsActivo())) {
            return new ApiResponse("El correo electrónico ya está registrado", true, HttpStatus.CONFLICT);
        }

        // ── 4. Validar unicidad de CURP (activo) o mismo usuario inactivo ───
        if (userRepository.existsByCurp(curp)) {
            if (existenteCorreo.isEmpty() || !curp.equalsIgnoreCase(existenteCorreo.get().getCurp())) {
                return new ApiResponse("La CURP ya está registrada", true, HttpStatus.CONFLICT);
            }
            // mismo usuario inactivo reactivando: CURP coincide, continuar
        }

        // ── 5. Validar fecha de nacimiento (mayoría de edad) ───────────────
        LocalDate fechaNacimiento;
        try {
            fechaNacimiento = LocalDate.parse(dto.getFechaNacimiento());
        } catch (Exception e) {
            return new ApiResponse("Formato de fecha inválido, debe ser yyyy-MM-dd", true, HttpStatus.BAD_REQUEST);
        }

        int edad = Period.between(fechaNacimiento, LocalDate.now()).getYears();
        if (edad < 18) {
            return new ApiResponse("El usuario debe ser mayor de 18 años", true, HttpStatus.BAD_REQUEST);
        }

        // ── 6. Validar existencia de Rol ───────────────────────────────────
        Optional<Role> rolOpt = roleRepository.findById(dto.getIdRol());
        if (rolOpt.isEmpty()) {
            return new ApiResponse("El rol especificado no existe", true, HttpStatus.NOT_FOUND);
        }
        Role role = rolOpt.get();

        // ── 7. Validar existencia de Área ──────────────────────────────────
        Optional<Area> areaOpt = areaRepository.findById(dto.getIdArea());
        if (areaOpt.isEmpty()) {
            return new ApiResponse("El área especificada no existe", true, HttpStatus.NOT_FOUND);
        }

        // ── 8–11. Reactivar usuario inactivo o crear nuevo ─────────────────
        if (existenteCorreo.isPresent()) {
            User user = existenteCorreo.get();
            String passwordPlana = generarPasswordTemporal();
            user.setNombreCompleto(nombre);
            user.setCurp(curp);
            user.setFechaNacimiento(fechaNacimiento);
            user.setRole(role);
            user.setArea(areaOpt.get());
            user.setPasswordHash(passwordEncoder.encode(passwordPlana));
            user.setPrimerLogin(true);
            user.setEsActivo(true);
            userRepository.save(user);
            try {
                mailService.enviarCredenciales(correo, nombre, passwordPlana);
            } catch (Exception e) {
                return new ApiResponse(
                        "Usuario reactivado pero hubo un error al enviar el correo: " + e.getMessage(),
                        user, HttpStatus.OK);
            }
            return new ApiResponse("Usuario reactivado exitosamente. Se envió la nueva contraseña temporal al correo " + correo,
                    user, HttpStatus.OK);
        }

        String numeroEmpleado = generarNumeroEmpleado(curp, role);
        String passwordPlana = generarPasswordTemporal();

        User user = new User();
        user.setNombreCompleto(nombre);
        user.setCorreo(correo);
        user.setCurp(curp);
        user.setFechaNacimiento(fechaNacimiento);
        user.setNumeroEmpleado(numeroEmpleado);
        user.setRole(role);
        user.setArea(areaOpt.get());
        user.setPasswordHash(passwordEncoder.encode(passwordPlana));
        user.setPrimerLogin(true);
        user.setEsActivo(true);

        userRepository.save(user);

        try {
            mailService.enviarCredenciales(correo, nombre, passwordPlana);
        } catch (Exception e) {
            return new ApiResponse(
                    "Usuario registrado pero hubo un error al enviar el correo: " + e.getMessage(),
                    user, HttpStatus.CREATED);
        }

        return new ApiResponse("Usuario registrado exitosamente. Se envió la contraseña temporal al correo " + correo,
                user, HttpStatus.CREATED);
    }

    // ======================================================================
    // MÉTODOS PRIVADOS (helpers de doRegister)
    // ======================================================================

    /**
     * Genera el número de empleado con el formato:
     * [2 últimos chars CURP] + [prefijo rol] + [consecutivo 4 dígitos]
     * <p>
     * El consecutivo se basa en la cantidad de usuarios existentes para ese rol + 1.
     * Se valida que no exista ya un número de empleado duplicado.
     */
    private String generarNumeroEmpleado(String curp, Role role) {
        String sufijoCurp = curp.substring(16); // últimos 2 caracteres de la CURP
        String prefijoRol = obtenerPrefijoRol(role.getNombre());

        long consecutivo = userRepository.countByRoleId(role.getId()) + 1;
        String numeroEmpleado;

        // Asegurar unicidad del número generado
        do {
            numeroEmpleado = sufijoCurp + prefijoRol + String.format("%04d", consecutivo);
            consecutivo++;
        } while (userRepository.existsByNumeroEmpleado(numeroEmpleado));

        return numeroEmpleado;
    }

    /**
     * Obtiene el prefijo de 3 letras según el nombre del rol.
     */
    private static String obtenerPrefijoRol(String nombreRol) {
        String rolUpper = nombreRol.toUpperCase();
        if (rolUpper.contains("ADMIN")) return "ADM";
        if (rolUpper.contains("TECN") || rolUpper.contains("TÉC")) return "TEC";
        return "EMP"; // Por defecto: Empleado
    }



}
