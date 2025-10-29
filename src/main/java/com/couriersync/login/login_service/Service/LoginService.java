package com.couriersync.login.login_service.Service;

import com.couriersync.login.login_service.Repository.RoleRepository;
import com.couriersync.login.login_service.Repository.PermisoRepository;
import com.couriersync.login.login_service.Repository.RolePermisoRepository;
import com.couriersync.login.login_service.Repository.UsuarioRepository;
import com.couriersync.login.login_service.Model.dto.LoginRequestDTO;
import com.couriersync.login.login_service.Model.dto.LoginResponseDTO;
import com.couriersync.login.login_service.Model.entity.Permiso;
import com.couriersync.login.login_service.Model.entity.Role;
import com.couriersync.login.login_service.Model.entity.Usuario;
import com.couriersync.login.login_service.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoginService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RolePermisoRepository rolePermisoRepository;

    @Autowired
    private PermisoRepository permisoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponseDTO login(LoginRequestDTO request) {

        // Buscar usuario por username
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        // Verificar contraseña
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // Obtener el rol del usuario
        Role role = usuario.getRol();

        // Obtener permisos del rol
        List<Long> idsPermisos = rolePermisoRepository.findPermisosByRolId(role.getIdRol());
        List<String> permisos = idsPermisos.stream()
                .map(id -> permisoRepository.findById(id)
                        .map(Permiso::getNombre)
                        .orElse("Desconocido"))
                .collect(Collectors.toList());

        // Generar token JWT
        String token = jwtUtil.generateToken(usuario.getUsername(), role.getNombre(), permisos);

        return new LoginResponseDTO(token, role.getNombre(), permisos,
                "Inicio de sesión exitoso");
    }
}
