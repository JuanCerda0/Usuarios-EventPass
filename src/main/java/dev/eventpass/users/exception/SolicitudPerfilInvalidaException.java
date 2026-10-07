package dev.eventpass.users.exception;

public class SolicitudPerfilInvalidaException extends RuntimeException {

    public SolicitudPerfilInvalidaException() {
        super("Debes enviar al menos el nombre o el correo para actualizar el perfil");
    }
}
