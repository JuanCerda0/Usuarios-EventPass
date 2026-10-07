package dev.eventpass.users.exception;

import java.util.Map;

public record ApiError(String codigo, String mensaje, Map<String, String> errores) {
}
