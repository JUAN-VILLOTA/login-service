package com.couriersync.login.login_service.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.couriersync.login.login_service.Model.entity.RolPermiso;

@Repository
public interface RolePermisoRepository extends JpaRepository<RolPermiso, Long> {

    // Traer todas las relaciones por ID del rol
    @Query("SELECT rp FROM RolPermiso rp WHERE rp.rol.idRol = :roleId")
    List<RolPermiso> findAllByRoleId(@Param("roleId") Long roleId);

    // Traer todas las relaciones por ID del permiso
    @Query("SELECT rp FROM RolPermiso rp WHERE rp.permiso.idPermiso = :permisoId")
    List<RolPermiso> findAllByPermisoId(@Param("permisoId") Long permisoId);

    // Método para obtener solo los IDs de permisos por rol
    @Query("SELECT rp.permiso.idPermiso FROM RolPermiso rp WHERE rp.rol.idRol = :roleId")
    List<Long> findPermisosByRolId(@Param("roleId") Long roleId);
}
