package com.couriersync.login.login_service.config;

import com.couriersync.login.login_service.Model.entity.Role;
import com.couriersync.login.login_service.Model.entity.Usuario;
import com.couriersync.login.login_service.Repository.RoleRepository;
import com.couriersync.login.login_service.Repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepository, 
                                   RoleRepository roleRepository, 
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            // Verificar si ya existe el usuario admin
            if (!usuarioRepository.findByUsername("admin").isPresent()) {
                
                // Buscar el rol ADMIN
                Role adminRole = roleRepository.findByNombre("ADMIN")
                    .orElseThrow(() -> new RuntimeException("⚠️ Rol ADMIN no encontrado en la BD. Asegúrate de tener el rol ADMIN creado."));

                // Crear usuario admin
                Usuario admin = new Usuario();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123")); // Contraseña temporal
                admin.setRol(adminRole);

                usuarioRepository.save(admin);
                
                System.out.println("=====================================");
                System.out.println("✅ Usuario ADMIN creado exitosamente");
                System.out.println("📧 Username: admin");
                System.out.println("🔑 Password: admin123");
                System.out.println("⚠️  IMPORTANTE: Cambia esta contraseña en producción");
                System.out.println("=====================================");
            } else {
                System.out.println("ℹ️  Usuario admin ya existe en la base de datos");
            }
        };
    }
}
