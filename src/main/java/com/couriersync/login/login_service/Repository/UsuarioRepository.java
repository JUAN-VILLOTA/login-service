package com.couriersync.login.login_service.Repository;

import com.couriersync.login.login_service.Model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Buscar usuario por username
    Optional<Usuario> findByUsername(String username);
    
    // Verificar si existe un usuario con ese username
    boolean existsByUsername(String username);
}

