package dev.eventpass.users.exception;

public class SesionNoValidaException extends RuntimeException {

    public SesionNoValidaException() {
        super("La sesión ya no es válida");
    }
}
