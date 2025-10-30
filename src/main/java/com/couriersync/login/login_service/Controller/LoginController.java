package com.couriersync.login.login_service.Controller;

import com.couriersync.login.login_service.Model.dto.LoginRequestDTO;
import com.couriersync.login.login_service.Model.dto.LoginResponseDTO;
import com.couriersync.login.login_service.Model.dto.TokenValidationDTO;
import com.couriersync.login.login_service.Service.LoginService;
import com.couriersync.login.login_service.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
@CrossOrigin(origins = "*")
public class LoginController {

    private final LoginService loginService;
    private final JwtUtil jwtUtil;

    @Autowired
    public LoginController(LoginService loginService, JwtUtil jwtUtil) {
        this.loginService = loginService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Endpoint principal de autenticación.
     * Valida las credenciales de un usuario y devuelve su rol y permisos.
     */
    @PostMapping
    public LoginResponseDTO login(@RequestBody LoginRequestDTO request) {
        return loginService.login(request);
    }

    /**
     * Endpoint para validar un token JWT.
     * Retorna información sobre la validez del token y datos del usuario.
     */
    @PostMapping("/validate")
    public ResponseEntity<TokenValidationDTO> validateToken(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        // Missing header
        if (authHeader == null || authHeader.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new TokenValidationDTO(null, false, null, null));
        }

        // Wrong format
        if (!authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest()
                    .body(new TokenValidationDTO(null, false, null, null));
        }

        String token = authHeader.substring(7);
        
        try {
            String username = jwtUtil.extractUsername(token);
            boolean isValid = jwtUtil.validateToken(token, username);
            String role = jwtUtil.extractRole(token);

            TokenValidationDTO response = new TokenValidationDTO(token, isValid, username, role);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(new TokenValidationDTO(token, false, null, null));
        }
    }
}
