package dev.eventpass.users.dto;

public record LoginResponse(
    String token,
    String tipo,
    long expiraEnSegundos,
    UsuarioResponse usuario
) {
}
