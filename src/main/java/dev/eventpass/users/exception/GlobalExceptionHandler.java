package dev.eventpass.users.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailYaRegistradoException.class)
    public ResponseEntity<ApiError> manejarEmailDuplicado(EmailYaRegistradoException excepcion) {
        ApiError error = new ApiError("EMAIL_YA_REGISTRADO", excepcion.getMessage(), Map.of());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ApiError> manejarCredencialesInvalidas(CredencialesInvalidasException excepcion) {
        ApiError error = new ApiError("CREDENCIALES_INVALIDAS", excepcion.getMessage(), Map.of());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(SesionNoValidaException.class)
    public ResponseEntity<ApiError> manejarSesionNoValida(SesionNoValidaException excepcion) {
        ApiError error = new ApiError("SESION_NO_VALIDA", excepcion.getMessage(), Map.of());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(ClaveProvisionamientoInvalidaException.class)
    public ResponseEntity<ApiError> manejarClaveProvisionamientoInvalida(
        ClaveProvisionamientoInvalidaException excepcion
    ) {
        ApiError error = new ApiError("CLAVE_PROVISIONAMIENTO_INVALIDA", excepcion.getMessage(), Map.of());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> manejarValidacion(MethodArgumentNotValidException excepcion) {
        Map<String, String> errores = new LinkedHashMap<>();
        excepcion.getBindingResult().getFieldErrors().forEach(error ->
            errores.putIfAbsent(error.getField(), error.getDefaultMessage())
        );

        ApiError respuesta = new ApiError("SOLICITUD_INVALIDA", "Revisa los datos enviados", errores);
        return ResponseEntity.badRequest().body(respuesta);
    }

    @ExceptionHandler(SolicitudPerfilInvalidaException.class)
    public ResponseEntity<ApiError> manejarSolicitudPerfilInvalida(SolicitudPerfilInvalidaException excepcion) {
        ApiError error = new ApiError("SOLICITUD_INVALIDA", excepcion.getMessage(), Map.of());
        return ResponseEntity.badRequest().body(error);
    }
}
