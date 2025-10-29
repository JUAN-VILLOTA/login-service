package com.couriersync.login.login_service.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.couriersync.login.login_service.Model.entity.Permiso;

@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Long> {

    // Buscar permiso por nombre (ejemplo: "CREAR_USUARIO")
    Optional<Permiso> findByNombre(String nombre);

    // Verificar si ya existe un permiso con ese nombre
    boolean existsByNombre(String nombre);
}
