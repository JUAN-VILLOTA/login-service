package com.couriersync.login.login_service.Controller;

import com.couriersync.login.login_service.Model.dto.LoginRequestDTO;
import com.couriersync.login.login_service.Model.dto.LoginResponseDTO;
import com.couriersync.login.login_service.Model.dto.RefreshTokenRequestDTO;
import com.couriersync.login.login_service.Model.dto.TokenValidationDTO;
import com.couriersync.login.login_service.Model.entity.RefreshToken;
import com.couriersync.login.login_service.Service.LoginService;
import com.couriersync.login.login_service.Service.RefreshTokenService;
import com.couriersync.login.login_service.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/login")
// CORS configurado globalmente en SecurityConfig - No necesario aquí
public class LoginController {

    private final LoginService loginService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public LoginController(LoginService loginService, JwtUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.loginService = loginService;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    /**
     * Endpoint principal de autenticación.
     * Valida las credenciales de un usuario y devuelve su rol y permisos.
     */
    @PostMapping
    public LoginResponseDTO login(@RequestBody LoginRequestDTO request, HttpServletRequest httpRequest) {
        return loginService.login(request, httpRequest);
    }

    /**
     * Endpoint para refrescar el access token usando un refresh token.
     */
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refreshToken(@RequestBody RefreshTokenRequestDTO request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUsuario)
                .map(usuario -> {
                    // Obtener permisos del usuario
                    List<String> permisos = usuario.getRol().getRolPermisos().stream()
                            .map(rp -> rp.getPermiso().getNombre())
                            .toList();
                    
                    // Generar nuevo access token
                    String newAccessToken = jwtUtil.generateToken(
                            usuario.getUsername(), 
                            usuario.getRol().getNombre(), 
                            permisos
                    );

                    LoginResponseDTO response = new LoginResponseDTO(
                            newAccessToken,
                            requestRefreshToken,
                            usuario.getRol().getNombre(),
                            permisos,
                            "Token renovado exitosamente"
                    );

                    return ResponseEntity.ok(response);
                })
                .orElseThrow(() -> new RuntimeException("Refresh token no encontrado"));
    }

    /**
     * Endpoint para revocar un refresh token.
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RefreshTokenRequestDTO request) {
        refreshTokenService.revokeToken(request.getRefreshToken());
        return ResponseEntity.ok("Sesión cerrada exitosamente");
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
