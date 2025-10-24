package com.couriersync.login.login_service.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.couriersync.login.login_service.Model.RolePermiso;

@Repository
public interface RolePermisoRepository extends JpaRepository<RolePermiso, Long> {

    // Traer todas las relaciones por ID del rol
    List<RolePermiso> findAllByRoleId(Long roleId);

    // Traer todas las relaciones por ID del permiso
    List<RolePermiso> findAllByPermisoId(Long permisoId);
}
