package dev.eventpass.users.config;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import dev.eventpass.users.security.UsuarioAutenticado;
import dev.eventpass.users.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest solicitud,
        HttpServletResponse respuesta,
        FilterChain cadena
    ) throws ServletException, IOException {
        String encabezado = solicitud.getHeader("Authorization");

        if (encabezado != null && encabezado.startsWith("Bearer ")) {
            String token = encabezado.substring(7);
            try {
                UsuarioAutenticado usuario = jwtService.validarYLeer(token);
                var autenticacion = new UsernamePasswordAuthenticationToken(
                    usuario,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + usuario.rol().name()))
                );
                autenticacion.setDetails(new WebAuthenticationDetailsSource().buildDetails(solicitud));
                SecurityContextHolder.getContext().setAuthentication(autenticacion);
            } catch (JwtException | IllegalArgumentException excepcion) {
                SecurityContextHolder.clearContext();
            }
        }

        cadena.doFilter(solicitud, respuesta);
    }
}
