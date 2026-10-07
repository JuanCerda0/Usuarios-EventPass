package dev.eventpass.users.service;

import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.eventpass.users.dto.LoginRequest;
import dev.eventpass.users.dto.LoginResponse;
import dev.eventpass.users.dto.UsuarioResponse;
import dev.eventpass.users.exception.CredencialesInvalidasException;
import dev.eventpass.users.model.Usuario;
import dev.eventpass.users.repository.UsuarioRepository;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
        UsuarioRepository usuarioRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public LoginResponse iniciarSesion(LoginRequest solicitud) {
        String emailNormalizado = solicitud.email().trim().toLowerCase(Locale.ROOT);
        Usuario usuario = usuarioRepository.findByEmail(emailNormalizado)
            .filter(Usuario::isActivo)
            .filter(encontrado -> passwordEncoder.matches(solicitud.contrasena(), encontrado.getPasswordHash()))
            .orElseThrow(CredencialesInvalidasException::new);

        return new LoginResponse(
            jwtService.generarToken(usuario),
            "Bearer",
            jwtService.getDuracionSegundos(),
            UsuarioResponse.desde(usuario)
        );
    }
}
