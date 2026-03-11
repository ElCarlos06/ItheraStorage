package mx.edu.utez.modules.register;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.areas.Area;
import mx.edu.utez.modules.areas.AreaRepository;
import mx.edu.utez.modules.roles.Role;
import mx.edu.utez.modules.roles.RoleRepository;
import mx.edu.utez.modules.users.User;
import mx.edu.utez.modules.users.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;
import java.util.regex.Pattern;

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

    // ======================================================================
    // REGEX — Formato oficial CURP mexicano (18 caracteres)
    // Estructura: AAAA######SSEEAAAA##
    //   4 letras (apellidos + nombre) + 6 dígitos (fecha yyMMdd) +
    //   1 letra (sexo H/M) + 2 letras (entidad federativa) +
    //   3 consonantes internas + 1 alfanumérico (homoclave) + 1 dígito (verificador)
    // ======================================================================
    private static final Pattern CURP_PATTERN = Pattern.compile(
            "^[A-Z]{4}\\d{6}[HM][A-Z]{2}[B-DF-HJ-NP-TV-Z]{3}[A-Z0-9]\\d$"
    );

    // Nombre: solo letras (con acentos), espacios y apóstrofos. No inicia con espacio, punto, coma o carácter especial.
    private static final Pattern NOMBRE_PATTERN = Pattern.compile(
            "^[A-ZÁÉÍÓÚÜÑa-záéíóúüñ][A-ZÁÉÍÓÚÜÑa-záéíóúüñ ']*$"
    );

    // Caracteres para generación de contraseña segura
    private static final String PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%&*!";
    private static final int PASSWORD_LENGTH = 12;
    private static final SecureRandom RANDOM = new SecureRandom();

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

        // ── 3. Validar unicidad de correo ──────────────────────────────────
        String correo = dto.getCorreo().trim().toLowerCase();
        if (userRepository.existsByCorreo(correo)) {
            return new ApiResponse("El correo electrónico ya está registrado", true, HttpStatus.CONFLICT);
        }

        // ── 4. Validar unicidad de CURP ────────────────────────────────────
        if (userRepository.existsByCurp(curp)) {
            return new ApiResponse("La CURP ya está registrada", true, HttpStatus.CONFLICT);
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

        // ── 8. Generar número de empleado ──────────────────────────────────
        //   Formato: [2 últimos chars CURP] + [prefijo rol] + [consecutivo 4 dígitos]
        //   Ejemplo CURP "GARC900101HMNRRR09" → "09ADM0025"
        //   Prefijo: Administrador=ADM, Empleado=EMP, Técnico=TEC
        String numeroEmpleado = generarNumeroEmpleado(curp, role);

        // ── 9. Generar contraseña temporal segura ──────────────────────────
        String passwordPlana = generarPasswordTemporal();

        // ── 10. Construir y persistir el usuario ───────────────────────────
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

        // ── 11. Enviar contraseña temporal por correo ──────────────────────
        try {
            mailService.enviarCredenciales(correo, nombre, passwordPlana);
        } catch (Exception e) {
            // El usuario ya fue guardado — registramos el error pero no hacemos rollback
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
    private String obtenerPrefijoRol(String nombreRol) {
        String rolUpper = nombreRol.toUpperCase();
        if (rolUpper.contains("ADMIN")) return "ADM";
        if (rolUpper.contains("TECN") || rolUpper.contains("TÉC")) return "TEC";
        return "EMP"; // Por defecto: Empleado
    }

    /**
     * Genera una contraseña temporal segura de {@value PASSWORD_LENGTH} caracteres
     * que incluye mayúsculas, minúsculas, dígitos y caracteres especiales.
     */
    private String generarPasswordTemporal() {
        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);
        // Garantizar al menos 1 mayúscula, 1 minúscula, 1 dígito y 1 especial
        sb.append(PASSWORD_CHARS.charAt(RANDOM.nextInt(26)));        // A-Z
        sb.append(PASSWORD_CHARS.charAt(26 + RANDOM.nextInt(26)));   // a-z
        sb.append(PASSWORD_CHARS.charAt(52 + RANDOM.nextInt(10)));   // 0-9
        sb.append(PASSWORD_CHARS.charAt(62 + RANDOM.nextInt(PASSWORD_CHARS.length() - 62))); // especial

        // Rellenar el resto
        for (int i = 4; i < PASSWORD_LENGTH; i++) {
            sb.append(PASSWORD_CHARS.charAt(RANDOM.nextInt(PASSWORD_CHARS.length())));
        }

        // Mezclar para que los primeros 4 no siempre sean en el mismo orden
        char[] chars = sb.toString().toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char tmp = chars[i];
            chars[i] = chars[j];
            chars[j] = tmp;
        }

        return new String(chars);
    }

}
