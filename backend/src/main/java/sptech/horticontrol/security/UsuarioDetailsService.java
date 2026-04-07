package sptech.horticontrol.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sptech.horticontrol.entity.Usuario;
import sptech.horticontrol.repository.UsuarioRepository;

import java.util.List;

// Implementa UserDetailsService — interface obrigatória do Spring Security.
// O Spring usa isso para carregar o usuário do banco durante o login e em cada requisição autenticada.
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        // Retorna um UserDetails com e-mail, senha (hash BCrypt) e roles
        return new User(
                usuario.getEmail(),
                usuario.getSenha(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
