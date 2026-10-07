package dev.eventpass.users.exception;

public class EmailYaRegistradoException extends RuntimeException {

    public EmailYaRegistradoException() {
        super("Ya existe una cuenta registrada con ese correo");
    }
}
