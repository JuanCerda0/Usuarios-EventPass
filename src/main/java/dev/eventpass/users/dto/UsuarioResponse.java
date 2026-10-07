package dev.eventpass.users.dto;

import dev.eventpass.users.model.Rol;
import dev.eventpass.users.model.Usuario;

public record UsuarioResponse(
    Long id,
    String nombre,
    String email,
    Rol rol,
    boolean activo
) {
    public static UsuarioResponse desde(Usuario usuario) {
        return new UsuarioResponse(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getEmail(),
            usuario.getRol(),
            usuario.isActivo()
        );
    }
}
