package dev.eventpass.users.exception;

public class ClaveProvisionamientoInvalidaException extends RuntimeException {

    public ClaveProvisionamientoInvalidaException() {
        super("La clave de provisión STAFF es inválida");
    }
}
