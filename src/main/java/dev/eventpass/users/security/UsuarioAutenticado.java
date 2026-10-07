package dev.eventpass.users.security;

import dev.eventpass.users.model.Rol;

public record UsuarioAutenticado(Long id, String email, Rol rol) {
}
