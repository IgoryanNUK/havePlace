package vk.haveplace.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import vk.haveplace.services.objects.dto.AdminDTO;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class AdminDetails implements UserDetails {
    private final AdminDTO admin;

    public AdminDetails(AdminDTO admin) {
        this.admin = admin;
    }

    @Override
    public Collection getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + admin.getRole()));
        return authorities;
    }

    @Override
    public String getPassword() {
        return admin.getVkId() + "";
    }

    @Override
    public String getUsername() {
        return admin.getName();
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
        return true;
    }
}
