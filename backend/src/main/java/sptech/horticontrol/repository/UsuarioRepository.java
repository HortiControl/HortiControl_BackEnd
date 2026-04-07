package sptech.horticontrol.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sptech.horticontrol.entity.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Busca usuário pelo email (usado no login)
    Optional<Usuario> findByEmail(String email);

}