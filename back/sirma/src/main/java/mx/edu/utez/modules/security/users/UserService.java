package mx.edu.utez.modules.security.users;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.location.areas.Area;
import mx.edu.utez.modules.location.areas.AreaRepository;
import mx.edu.utez.modules.security.roles.Role;
import mx.edu.utez.modules.security.roles.RoleRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Servicio de negocio para gestión de usuarios en SIRMA.
 * Maneja operaciones CRUD con validaciones de integridad y encriptación de contraseñas.
 *
 * @author Ithera Team
 */
@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AreaRepository areaRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public ApiResponse findAll(Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);
        return new ApiResponse("OK", page, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ApiResponse findById(Long id) {
        Optional<User> found = userRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Usuario no encontrado", true, HttpStatus.NOT_FOUND);
        return new ApiResponse("OK", found.get(), HttpStatus.OK);
    }

    /**
     * Busca un usuario por su correo electrónico.
     * <p>
     * Usado por el front para validar que el correo existe antes de continuar
     * con un flujo (ej. recuperación de contraseña, pre-validación de registro).
     * Si no existe, retorna 404 con un mensaje descriptivo para que la UI lo muestre
     * directamente al usuario sin necesidad de parseo adicional.
     * </p>
     *
     * @param correo correo institucional a buscar
     * @return {@link ApiResponse} con el usuario encontrado o error descriptivo 404
     */
    @Transactional(readOnly = true)
    public ApiResponse findByCorreo(String correo) {
        Optional<User> found = userRepository.findByCorreoIgnoreCase(correo.trim());
        if (found.isEmpty())
            return new ApiResponse(
                    "No existe ningún usuario registrado con el correo: " + correo,
                    true, HttpStatus.NOT_FOUND);
        return new ApiResponse("Usuario encontrado", found.get(), HttpStatus.OK);
    }

    /**
     * Metodo que efectua la actualización de información de un usuario.
     * @param id ID del usuario al que sse le van a aplicar los cambios.
     * @param dto DTO que contiene la información que se puede actualizar de un usuario.
     * @return ApiReponse con la información del usuario actualizado o un mensaje de error si no se encuentra el usuario, rol o área.
     */
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse update(Long id, UserDTO dto) {
        Optional<User> found = userRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Usuario no encontrado", true, HttpStatus.NOT_FOUND);
        Optional<Role> role = roleRepository.findById(dto.getIdRol());
        if (role.isEmpty())
            return new ApiResponse("Rol no encontrado", true, HttpStatus.NOT_FOUND);
        Optional<Area> area = areaRepository.findById(dto.getIdArea());
        if (area.isEmpty())
            return new ApiResponse("Área no encontrada", true, HttpStatus.NOT_FOUND);

        User entity = found.get();
        entity.setNombreCompleto(dto.getNombreCompleto());
        entity.setCorreo(dto.getCorreo());
        entity.setCurp(dto.getCurp());
        entity.setFechaNacimiento(LocalDate.parse(dto.getFechaNacimiento()));
        String numeroEmpleado = (dto.getNumeroEmpleado() != null && !dto.getNumeroEmpleado().isBlank())
                ? dto.getNumeroEmpleado()
                : entity.getNumeroEmpleado();
        if (numeroEmpleado == null || numeroEmpleado.isBlank())
            numeroEmpleado = generarNumeroEmpleado(entity.getCurp(), role.get());
        entity.setNumeroEmpleado(numeroEmpleado);
        entity.setRole(role.get());
        entity.setArea(area.get());
        if (dto.getPassword() != null && !dto.getPassword().isBlank())
            entity.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        if (dto.getPrimerLogin() != null) entity.setPrimerLogin(dto.getPrimerLogin());
        if (dto.getEsActivo() != null) entity.setEsActivo(dto.getEsActivo());
        userRepository.save(entity);
        return new ApiResponse("Usuario actualizado", entity, HttpStatus.OK);
    }

    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse toggleStatus(Long id) {
        Optional<User> found = userRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Usuario no encontrado", true, HttpStatus.NOT_FOUND);
        User entity = found.get();
        entity.setEsActivo(!entity.getEsActivo());
        userRepository.save(entity);
        return new ApiResponse("Estado actualizado", entity, HttpStatus.OK);
    }

    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse delete(Long id) {
        Optional<User> found = userRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Usuario no encontrado", true, HttpStatus.NOT_FOUND);
        try {
            userRepository.delete(found.get());
            return new ApiResponse("Usuario eliminado", null, HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            return new ApiResponse(
                    "No se puede eliminar: el usuario tiene registros asociados (mantenimientos, reportes, resguardos, etc.)",
                    true, HttpStatus.CONFLICT);
        }
    }

    private String generarNumeroEmpleado(String curp, Role role) {
        String sufijoCurp = curp != null && curp.length() >= 18 ? curp.substring(16) : "00";
        String prefijoRol = obtenerPrefijoRol(role.getNombre());
        long consecutivo = userRepository.countByRoleId(role.getId()) + 1;
        String numeroEmpleado;
        do {
            numeroEmpleado = sufijoCurp + prefijoRol + String.format("%04d", consecutivo);
            consecutivo++;
        } while (userRepository.existsByNumeroEmpleado(numeroEmpleado));
        return numeroEmpleado;
    }

    private String obtenerPrefijoRol(String nombreRol) {
        if (nombreRol == null) return "EMP";
        String rolUpper = nombreRol.toUpperCase();
        if (rolUpper.contains("ADMIN")) return "ADM";
        if (rolUpper.contains("TECN") || rolUpper.contains("TÉC")) return "TEC";
        return "EMP";
    }

}
