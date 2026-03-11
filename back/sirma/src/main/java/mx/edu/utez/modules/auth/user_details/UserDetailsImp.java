package mx.edu.utez.modules.auth.user_details;

import mx.edu.utez.modules.users.User;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Implementación de UserDetails que adapta la entidad User al contexto de Spring Security.
 * Proporciona los detalles necesarios para autenticación y autorización.
 *
 * @author Ithera Team
 */
public class UserDetailsImp implements UserDetails {

    private final String email;
    private final String password;
    private final boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImp(User user) {
        this.email = user.getCorreo();
        this.password = user.getPasswordHash();
        this.enabled = user.getEsActivo() != null && user.getEsActivo();
        String roleName = user.getRole().getNombre();
        this.authorities = Collections.singleton(
                new SimpleGrantedAuthority(roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
