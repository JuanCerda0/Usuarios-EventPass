package dev.eventpass.users.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.eventpass.users.dto.CrearStaffRequest;
import dev.eventpass.users.dto.ActualizarPerfilRequest;
import dev.eventpass.users.dto.RegistrarUsuarioRequest;
import dev.eventpass.users.dto.UsuarioResponse;
import dev.eventpass.users.exception.ClaveProvisionamientoInvalidaException;
import dev.eventpass.users.exception.EmailYaRegistradoException;
import dev.eventpass.users.exception.SolicitudPerfilInvalidaException;
import dev.eventpass.users.exception.SesionNoValidaException;
import dev.eventpass.users.model.Rol;
import dev.eventpass.users.model.Usuario;
import dev.eventpass.users.repository.UsuarioRepository;
import dev.eventpass.users.security.UsuarioAutenticado;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final byte[] claveProvisionamiento;

    public UsuarioService(
        UsuarioRepository usuarioRepository,
        PasswordEncoder passwordEncoder,
        @Value("${app.staff.provision-key}") String claveProvisionamiento
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        if (claveProvisionamiento.length() < 32) {
            throw new IllegalArgumentException("La clave de provisión STAFF debe tener al menos 32 caracteres");
        }
        this.claveProvisionamiento = claveProvisionamiento.getBytes(StandardCharsets.UTF_8);
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

    @Transactional
    public UsuarioResponse registrarStaff(CrearStaffRequest solicitud, String claveProporcionada) {
        if (claveProporcionada == null || !MessageDigest.isEqual(
            claveProvisionamiento,
            claveProporcionada.getBytes(StandardCharsets.UTF_8)
        )) {
            throw new ClaveProvisionamientoInvalidaException();
        }

        String emailNormalizado = solicitud.email().trim().toLowerCase(Locale.ROOT);
        if (usuarioRepository.existsByEmail(emailNormalizado)) {
            throw new EmailYaRegistradoException();
        }

        Usuario usuario = new Usuario(
            solicitud.nombre().trim(),
            emailNormalizado,
            passwordEncoder.encode(solicitud.contrasena()),
            Rol.STAFF
        );

        return UsuarioResponse.desde(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPerfilActual(UsuarioAutenticado usuarioAutenticado) {
        Usuario usuario = usuarioRepository.findByIdAndActivoTrue(usuarioAutenticado.id())
            .orElseThrow(SesionNoValidaException::new);

        return UsuarioResponse.desde(usuario);
    }

    @Transactional
    public UsuarioResponse actualizarPerfilActual(
        UsuarioAutenticado usuarioAutenticado,
        ActualizarPerfilRequest solicitud
    ) {
        if (solicitud.nombre() == null && solicitud.email() == null) {
            throw new SolicitudPerfilInvalidaException();
        }

        Usuario usuario = usuarioRepository.findByIdAndActivoTrue(usuarioAutenticado.id())
            .orElseThrow(SesionNoValidaException::new);

        if (solicitud.nombre() != null) {
            usuario.setNombre(solicitud.nombre().trim());
        }

        if (solicitud.email() != null) {
            String emailNormalizado = solicitud.email().trim().toLowerCase(Locale.ROOT);
            if (usuarioRepository.existsByEmailAndIdNot(emailNormalizado, usuario.getId())) {
                throw new EmailYaRegistradoException();
            }
            usuario.setEmail(emailNormalizado);
        }

        return UsuarioResponse.desde(usuarioRepository.save(usuario));
    }

    @Transactional
    public void desactivarPerfilActual(UsuarioAutenticado usuarioAutenticado) {
        Usuario usuario = usuarioRepository.findById(usuarioAutenticado.id())
            .orElseThrow(SesionNoValidaException::new);

        if (usuario.isActivo()) {
            usuario.setActivo(false);
            usuarioRepository.save(usuario);
        }
    }
}
