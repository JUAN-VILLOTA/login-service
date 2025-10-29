package com.couriersync.login.login_service.Service;

import com.couriersync.login.login_service.Model.entity.Permiso;
import com.couriersync.login.login_service.Repository.PermisoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PermisoService {

    private final PermisoRepository permisoRepository;

    public PermisoService(PermisoRepository permisoRepository) {
        this.permisoRepository = permisoRepository;
    }

    public List<Permiso> getAllPermisos() {
        return permisoRepository.findAll();
    }

    public Optional<Permiso> getPermisoById(Long id) {
        return permisoRepository.findById(id);
    }

    public Permiso savePermiso(Permiso permiso) {
        return permisoRepository.save(permiso);
    }

    public void deletePermiso(Long id) {
        permisoRepository.deleteById(id);
    }
}
