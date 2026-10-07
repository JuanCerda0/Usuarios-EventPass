package dev.eventpass.users.service;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import dev.eventpass.users.model.Usuario;
import dev.eventpass.users.model.Rol;
import dev.eventpass.users.security.UsuarioAutenticado;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final String EMISOR = "eventpass-users";

    private final SecretKey claveFirma;
    private final long duracionSegundos;

    public JwtService(
        @Value("${app.jwt.secret-base64}") String claveBase64,
        @Value("${app.jwt.expiration-seconds}") long duracionSegundos
    ) {
        byte[] clave = Base64.getDecoder().decode(claveBase64);
        this.claveFirma = Keys.hmacShaKeyFor(clave);
        this.duracionSegundos = duracionSegundos;

        if (duracionSegundos <= 0) {
            throw new IllegalArgumentException("La duración del JWT debe ser mayor a cero");
        }
    }

    public String generarToken(Usuario usuario) {
        Instant ahora = Instant.now();
        Instant expiracion = ahora.plusSeconds(duracionSegundos);

        return Jwts.builder()
            .issuer(EMISOR)
            .subject(usuario.getId().toString())
            .claim("email", usuario.getEmail())
            .claim("rol", usuario.getRol().name())
            .issuedAt(Date.from(ahora))
            .expiration(Date.from(expiracion))
            .signWith(claveFirma)
            .compact();
    }

    public UsuarioAutenticado validarYLeer(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(claveFirma)
            .requireIssuer(EMISOR)
            .build()
            .parseSignedClaims(token)
            .getPayload();

        return new UsuarioAutenticado(
            Long.valueOf(claims.getSubject()),
            claims.get("email", String.class),
            Rol.valueOf(claims.get("rol", String.class))
        );
    }

    public long getDuracionSegundos() {
        return duracionSegundos;
    }
}
