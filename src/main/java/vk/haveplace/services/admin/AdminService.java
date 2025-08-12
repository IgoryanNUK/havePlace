package vk.haveplace.services.admin;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import vk.haveplace.database.AdminRepository;
import vk.haveplace.database.entities.AdminEntity;
import vk.haveplace.database.entities.Role;
import vk.haveplace.exceptions.AdminNotFound;
import vk.haveplace.services.mappers.AdminMapper;
import vk.haveplace.services.objects.dto.AdminDTO;
import vk.haveplace.services.objects.requests.AdminRequest;
import vk.haveplace.services.objects.requests.AdminUpdateRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public AdminDTO add(AdminRequest request) {
        AdminEntity entity = adminRepository.save(AdminMapper.getEntityFromRequest(request));

        return AdminMapper.getDTOFromEntity(entity);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public AdminDTO update(AdminUpdateRequest req) {
        AdminEntity entity = AdminMapper.getEntityFromUpdateRequest(req);

        AdminEntity answer = adminRepository.save(entity);

        return AdminMapper.getDTOFromEntity(answer);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<AdminDTO> getAll() {
        List<AdminEntity> entityList = adminRepository.findAll();

        List<AdminDTO> list = new ArrayList<>(entityList.size());
        for (AdminEntity entity : entityList) {
            list.add(AdminMapper.getDTOFromEntity(entity));
        }

        return list;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Boolean removeById(int id) {
        if (adminRepository.findById(id).isPresent()) {
            adminRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }


    @Transactional(isolation =  Isolation.SERIALIZABLE)
    public AdminDTO updateRole(int adminId, Role role) {
        Optional<AdminEntity> opt = adminRepository.findById(adminId);
        if (opt.isPresent()) {
            AdminEntity entity = opt.get();
            entity.setRole(role);

            entity = adminRepository.saveAndFlush(entity);
            return AdminMapper.getDTOFromEntity(entity);
        } else {
            throw new AdminNotFound(adminId);
        }
    }
}
