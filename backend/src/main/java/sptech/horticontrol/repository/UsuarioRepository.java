package sptech.horticontrol.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sptech.horticontrol.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
