package dev.eventpass.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ActualizarPerfilRequest(
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    @Pattern(regexp = ".*\\S.*", message = "El nombre no puede estar vacío")
    String nombre,

    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 254, message = "El correo no puede superar los 254 caracteres")
    @Pattern(regexp = ".*\\S.*", message = "El correo no puede estar vacío")
    String email
) {
}
