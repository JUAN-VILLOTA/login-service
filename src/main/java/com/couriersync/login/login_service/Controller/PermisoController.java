package com.couriersync.login.login_service.Controller;

import com.couriersync.login.login_service.Model.entity.Permiso;
import com.couriersync.login.login_service.Service.PermisoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/permisos")
// CORS configurado globalmente en SecurityConfig - No necesario aquí
public class PermisoController {

    private final PermisoService permisoService;

    public PermisoController(PermisoService permisoService) {
        this.permisoService = permisoService;
    }

    // ✅ GET: listar todos los permisos
    @GetMapping
    public List<Permiso> getAllPermisos() {
        return permisoService.getAllPermisos();
    }

    // ✅ GET: obtener un permiso por ID
    @GetMapping("/{id}")
    public Optional<Permiso> getPermisoById(@PathVariable Long id) {
        return permisoService.getPermisoById(id);
    }

    // ✅ POST: crear un nuevo permiso
    @PostMapping
    public Permiso createPermiso(@RequestBody Permiso permiso) {
        return permisoService.savePermiso(permiso);
    }

    // ✅ PUT: actualizar un permiso existente
    @PutMapping("/{id}")
    public Permiso updatePermiso(@PathVariable Long id, @RequestBody Permiso permiso) {
        permiso.setIdPermiso(id);
        return permisoService.savePermiso(permiso);
    }

    // ✅ DELETE: eliminar un permiso por ID
    @DeleteMapping("/{id}")
    public void deletePermiso(@PathVariable Long id) {
        permisoService.deletePermiso(id);
    }
}
