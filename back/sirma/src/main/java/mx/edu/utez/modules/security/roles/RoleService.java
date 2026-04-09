package mx.edu.utez.modules.security.roles;

import lombok.AllArgsConstructor;
import mx.edu.utez.kernel.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de negocio para gestión de roles en SIRMA.
 * Maneja operaciones CRUD con validaciones de unicidad.
 *
 * @author Ithera Team
 */
@Service
@AllArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public ApiResponse findAll() {
        List<Role> list = roleRepository.findAll();
        return new ApiResponse("OK", list, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ApiResponse findById(Long id) {
        Optional<Role> found = roleRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Rol no encontrado", true, HttpStatus.NOT_FOUND);
        return new ApiResponse("OK", found.get(), HttpStatus.OK);
    }

    @Transactional
    public ApiResponse save(RoleDTO dto) {
        if (roleRepository.existsByNombre(dto.getNombre()))
            return new ApiResponse("Ya existe un rol con ese nombre", true, HttpStatus.CONFLICT);
        Role entity = new Role();
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        roleRepository.save(entity);
        return new ApiResponse("Rol registrado", entity, HttpStatus.CREATED);
    }

    @Transactional
    public ApiResponse update(Long id, RoleDTO dto) {
        Optional<Role> found = roleRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Rol no encontrado", true, HttpStatus.NOT_FOUND);
        Role entity = found.get();
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        roleRepository.save(entity);
        return new ApiResponse("Rol actualizado", entity, HttpStatus.OK);
    }

    @Transactional
    public ApiResponse deleteById(Long id) {
        Optional<Role> found = roleRepository.findById(id);
        if (found.isEmpty())
            return new ApiResponse("Rol no encontrado", true, HttpStatus.NOT_FOUND);
        roleRepository.deleteById(id);
        return new ApiResponse("Rol eliminado", HttpStatus.OK);
    }

}
