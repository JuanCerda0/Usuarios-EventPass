package dev.eventpass.users.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.eventpass.users.dto.RegistrarUsuarioRequest;
import dev.eventpass.users.dto.UsuarioResponse;
import dev.eventpass.users.security.UsuarioAutenticado;
import dev.eventpass.users.service.UsuarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistrarUsuarioRequest solicitud) {
        UsuarioResponse usuario = usuarioService.registrarComprador(solicitud);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> obtenerPerfilActual(
        @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado
    ) {
        return ResponseEntity.ok(usuarioService.obtenerPerfilActual(usuarioAutenticado));
    }
}
