package dev.eventpass.users.config;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import tools.jackson.databind.ObjectMapper;

import dev.eventpass.users.exception.ApiError;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
        HttpServletRequest solicitud,
        HttpServletResponse respuesta,
        AuthenticationException excepcion
    ) throws IOException, ServletException {
        respuesta.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        respuesta.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(
            respuesta.getWriter(),
            new ApiError("NO_AUTENTICADO", "Se requiere un token Bearer válido", Map.of())
        );
    }
}
