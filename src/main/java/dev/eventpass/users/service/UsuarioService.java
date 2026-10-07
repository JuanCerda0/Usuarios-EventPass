package dev.eventpass.users.service;

import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.eventpass.users.dto.RegistrarUsuarioRequest;
import dev.eventpass.users.dto.UsuarioResponse;
import dev.eventpass.users.exception.EmailYaRegistradoException;
import dev.eventpass.users.exception.SesionNoValidaException;
import dev.eventpass.users.model.Rol;
import dev.eventpass.users.model.Usuario;
import dev.eventpass.users.repository.UsuarioRepository;
import dev.eventpass.users.security.UsuarioAutenticado;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponse registrarComprador(RegistrarUsuarioRequest solicitud) {
        String emailNormalizado = solicitud.email().trim().toLowerCase(Locale.ROOT);

        if (usuarioRepository.existsByEmail(emailNormalizado)) {
            throw new EmailYaRegistradoException();
        }

        Usuario usuario = new Usuario(
            solicitud.nombre().trim(),
            emailNormalizado,
            passwordEncoder.encode(solicitud.contrasena()),
            Rol.COMPRADOR
        );

        return UsuarioResponse.desde(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPerfilActual(UsuarioAutenticado usuarioAutenticado) {
        Usuario usuario = usuarioRepository.findByIdAndActivoTrue(usuarioAutenticado.id())
            .orElseThrow(SesionNoValidaException::new);

        return UsuarioResponse.desde(usuario);
    }
}
