package mx.edu.utez.modules.users;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import mx.edu.utez.modules.areas.Area;
import mx.edu.utez.modules.areas.AreaRepository;
import mx.edu.utez.modules.roles.Role;
import mx.edu.utez.modules.roles.RoleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
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
    public ApiResponse findAll() {
        List<User> list = userRepository.findAll();
        return new ApiResponse("OK", list, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ApiResponse findById(Long id) {
        Optional<User> found = userRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Usuario no encontrado", true, HttpStatus.NOT_FOUND);
        return new ApiResponse("OK", found.get(), HttpStatus.OK);
    }

    @Transactional
    public ApiResponse save(UserDTO dto) {
        if (userRepository.existsByCorreo(dto.getCorreo()))
            return new ApiResponse("Ya existe un usuario con ese correo", true, HttpStatus.CONFLICT);
        Optional<Role> role = roleRepository.findById(dto.getIdRol());
        if (role.isEmpty())
            return new ApiResponse("Rol no encontrado", true, HttpStatus.NOT_FOUND);
        Optional<Area> area = areaRepository.findById(dto.getIdArea());
        if (area.isEmpty())
            return new ApiResponse("Área no encontrada", true, HttpStatus.NOT_FOUND);

        User entity = new User();
        entity.setNombreCompleto(dto.getNombreCompleto());
        entity.setCorreo(dto.getCorreo());
        entity.setCurp(dto.getCurp());
        entity.setFechaNacimiento(LocalDate.parse(dto.getFechaNacimiento()));
        entity.setNumeroEmpleado(dto.getNumeroEmpleado());
        entity.setRole(role.get());
        entity.setArea(area.get());
        entity.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        entity.setPrimerLogin(true);
        entity.setEsActivo(true);
        userRepository.save(entity);
        return new ApiResponse("Usuario registrado", entity, HttpStatus.CREATED);
    }

    @Transactional
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
        entity.setNumeroEmpleado(dto.getNumeroEmpleado());
        entity.setRole(role.get());
        entity.setArea(area.get());
        if (dto.getPassword() != null && !dto.getPassword().isBlank())
            entity.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        if (dto.getPrimerLogin() != null) entity.setPrimerLogin(dto.getPrimerLogin());
        if (dto.getEsActivo() != null) entity.setEsActivo(dto.getEsActivo());
        userRepository.save(entity);
        return new ApiResponse("Usuario actualizado", entity, HttpStatus.OK);
    }

    @Transactional
    public ApiResponse toggleStatus(Long id) {
        Optional<User> found = userRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Usuario no encontrado", true, HttpStatus.NOT_FOUND);
        User entity = found.get();
        entity.setEsActivo(!entity.getEsActivo());
        userRepository.save(entity);
        return new ApiResponse("Estado actualizado", entity, HttpStatus.OK);
    }

}
