package mx.edu.utez.modules.security.auth.user_details;

import mx.edu.utez.modules.security.users.User;
import mx.edu.utez.modules.security.users.UserRepository;

import lombok.AllArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servicio de carga de usuarios para Spring Security.
 * Implementa UserDetailsService para integrar con JPA y la base de datos.
 *
 * @author Ithera Team
 */
@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Carga un usuario por su correo para construir el principal de Spring Security.
     * @param email correo institucional recibido en el login
     * @return detalles de usuario para el contexto de seguridad
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByCorreoIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return new UserDetailsImp(user);
    }
}
