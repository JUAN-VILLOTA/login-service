package com.couriersync.login.login_service.Service;

import com.couriersync.login.login_service.Model.dto.UsuarioRequestDTO;
import com.couriersync.login.login_service.Model.entity.Role;
import com.couriersync.login.login_service.Model.entity.Usuario;
import com.couriersync.login.login_service.Repository.RoleRepository;
import com.couriersync.login.login_service.Repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // Inyección del repositorio por constructor
    public UsuarioService(UsuarioRepository usuarioRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Obtener todos los usuarios
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    // Obtener un usuario por ID
    public Optional<Usuario> getUsuarioById(Long id) {
        return usuarioRepository.findById(id);
    }

    // Buscar usuario por username
    public Optional<Usuario> getUsuarioByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    // Guardar o actualizar un usuario
    public Usuario saveUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    // Crear un nuevo usuario con solo el ID del rol
    public Usuario createUsuario(UsuarioRequestDTO usuarioRequest) {
        // Verificar si el username ya existe
        if (existsByUsername(usuarioRequest.getUsername())) {
            throw new RuntimeException("El username ya existe");
        }

        // Buscar el rol por ID
        Role rol = roleRepository.findById(usuarioRequest.getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + usuarioRequest.getIdRol()));

        // Crear el usuario
        Usuario usuario = new Usuario();
        usuario.setUsername(usuarioRequest.getUsername());
        usuario.setPassword(passwordEncoder.encode(usuarioRequest.getPassword()));
        usuario.setRol(rol);

        return usuarioRepository.save(usuario);
    }

    // Actualizar un usuario existente
    public Usuario updateUsuario(Long id, UsuarioRequestDTO usuarioRequest) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // Actualizar username si es diferente y no existe
        if (!usuario.getUsername().equals(usuarioRequest.getUsername())) {
            if (existsByUsername(usuarioRequest.getUsername())) {
                throw new RuntimeException("El username ya existe");
            }
            usuario.setUsername(usuarioRequest.getUsername());
        }

        // Actualizar contraseña si se proporciona
        if (usuarioRequest.getPassword() != null && !usuarioRequest.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(usuarioRequest.getPassword()));
        }

        // Actualizar rol
        Role rol = roleRepository.findById(usuarioRequest.getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + usuarioRequest.getIdRol()));
        usuario.setRol(rol);

        return usuarioRepository.save(usuario);
    }

    // Eliminar un usuario por ID
    public void deleteUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    // Verificar si existe un usuario por username
    public boolean existsByUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }
}
