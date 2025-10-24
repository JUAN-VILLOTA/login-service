package com.couriersync.login.login_service.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.couriersync.login.login_service.Model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Buscar un rol por su nombre (ejemplo: "ADMIN")
    Optional<Role> findByNombre(String nombre);

    // Verificar si ya existe un rol con ese nombre
    boolean existsByNombre(String nombre);
}
