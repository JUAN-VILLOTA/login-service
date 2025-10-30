package com.couriersync.login.login_service.Service;

import com.couriersync.login.login_service.Model.entity.RefreshToken;
import com.couriersync.login.login_service.Model.entity.Usuario;
import com.couriersync.login.login_service.Repository.RefreshTokenRepository;
import com.couriersync.login.login_service.Repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${app.jwt.refresh-expiration-ms:604800000}") // 7 días por defecto
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UsuarioRepository usuarioRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, 
                              UsuarioRepository usuarioRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public RefreshToken createRefreshToken(Long usuarioId, HttpServletRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Revocar tokens anteriores del mismo usuario
        revokeUserTokens(usuario);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsuario(usuario);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        
        if (request != null) {
            refreshToken.setIpAddress(getClientIP(request));
            refreshToken.setUserAgent(request.getHeader("User-Agent"));
        }

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expirado. Por favor inicia sesión nuevamente");
        }
        
        if (token.isRevoked()) {
            throw new RuntimeException("Refresh token revocado. Por favor inicia sesión nuevamente");
        }

        return token;
    }

    @Transactional
    public void revokeToken(String token) {
        refreshTokenRepository.findByToken(token)
                .ifPresent(rt -> {
                    rt.setRevoked(true);
                    refreshTokenRepository.save(rt);
                });
    }

    /**
     * Revoca todos los refresh tokens de un usuario.
     * Este método necesita @Transactional porque llama a un método @Modifying del repository.
     */
    @Transactional
    public void revokeUserTokens(Usuario usuario) {
        refreshTokenRepository.revokeAllByUsuario(usuario);
    }

    /**
     * Elimina tokens expirados de la base de datos.
     * Operación de limpieza para mantener la tabla optimizada.
     */
    @Transactional
    public int deleteExpiredTokens() {
        // Eliminar tokens expirados y revocados
        return refreshTokenRepository.findAll().stream()
                .filter(token -> token.isExpired() || token.isRevoked())
                .mapToInt(token -> {
                    refreshTokenRepository.delete(token);
                    return 1;
                })
                .sum();
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
