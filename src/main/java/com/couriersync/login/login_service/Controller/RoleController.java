package com.couriersync.login.login_service.Controller;

import com.couriersync.login.login_service.Model.Role;
import com.couriersync.login.login_service.Service.RoleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*") // permite peticiones desde cualquier origen (útil durante desarrollo)
public class RoleController {

    private final RoleService roleService;

    // Inyección del servicio por constructor
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    // ✅ GET: listar todos los roles
    @GetMapping
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    // ✅ GET: obtener un rol por su ID
    @GetMapping("/{id}")
    public Optional<Role> getRoleById(@PathVariable Long id) {
        return roleService.getRoleById(id);
    }

    // ✅ POST: crear un nuevo rol
    @PostMapping
    public Role createRole(@RequestBody Role role) {
        return roleService.saveRole(role);
    }

    // ✅ PUT: actualizar un rol existente
    @PutMapping("/{id}")
    public Role updateRole(@PathVariable Long id, @RequestBody Role role) {
        role.setId(id);
        return roleService.saveRole(role);
    }

    // ✅ DELETE: eliminar un rol por ID
    @DeleteMapping("/{id}")
    public void deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
    }
}

