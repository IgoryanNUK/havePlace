package vk.haveplace.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vk.haveplace.database.AdminRepository;
import vk.haveplace.database.entities.AdminEntity;
import vk.haveplace.services.mappers.AdminMapper;

@Service
public class AdminDetailsService implements UserDetailsService {
    private final AdminRepository adminRepository;

    @Autowired
    public AdminDetailsService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AdminEntity admin = adminRepository.findByName(username).orElseThrow(() -> new UsernameNotFoundException(username));

        return new AdminDetails(AdminMapper.getDTOFromEntity(admin));
    }
}
