package com.couriersync.login.login_service.Controller;

import com.couriersync.login.login_service.Model.dto.UsuarioRequestDTO;
import com.couriersync.login.login_service.Model.entity.Usuario;
import com.couriersync.login.login_service.Service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*") // permite peticiones desde cualquier origen (útil durante desarrollo)
public class UsuarioController {

    private final UsuarioService usuarioService;

    // Inyección del servicio por constructor
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // ✅ GET: listar todos los usuarios
    @GetMapping
    public List<Usuario> getAllUsuarios() {
        return usuarioService.getAllUsuarios();
    }

    // ✅ GET: obtener un usuario por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.getUsuarioById(id);
        return usuario.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ GET: obtener un usuario por username
    @GetMapping("/username/{username}")
    public ResponseEntity<Usuario> getUsuarioByUsername(@PathVariable String username) {
        Optional<Usuario> usuario = usuarioService.getUsuarioByUsername(username);
        return usuario.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ POST: crear un nuevo usuario (solo requiere username, password e idRol)
    @PostMapping
    public ResponseEntity<?> createUsuario(@RequestBody UsuarioRequestDTO usuarioRequest) {
        try {
            Usuario nuevoUsuario = usuarioService.createUsuario(usuarioRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // ✅ PUT: actualizar un usuario existente (solo requiere username, password e idRol)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(@PathVariable Long id, @RequestBody UsuarioRequestDTO usuarioRequest) {
        try {
            Usuario usuarioActualizado = usuarioService.updateUsuario(id, usuarioRequest);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // ✅ DELETE: eliminar un usuario por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        if (!usuarioService.getUsuarioById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        usuarioService.deleteUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
