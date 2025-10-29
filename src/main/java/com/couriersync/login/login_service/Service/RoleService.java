package com.couriersync.login.login_service.Service;

import com.couriersync.login.login_service.Model.entity.Role;
import com.couriersync.login.login_service.Repository.RoleRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // Obtener todos los roles
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    // Obtener un rol específico por ID
    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    // Crear o actualizar un rol
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    // Eliminar un rol
    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }
    
}
