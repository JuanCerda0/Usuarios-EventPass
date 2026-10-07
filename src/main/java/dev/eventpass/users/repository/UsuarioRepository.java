package dev.eventpass.users.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.eventpass.users.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmail(String email);

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByIdAndActivoTrue(Long id);
}
