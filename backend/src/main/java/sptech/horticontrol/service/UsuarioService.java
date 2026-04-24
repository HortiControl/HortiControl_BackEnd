package sptech.horticontrol.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sptech.horticontrol.dtos.request.UsuarioPerfilRequestDTO;
import sptech.horticontrol.dtos.request.UsuarioSenhaRequestDTO;
import sptech.horticontrol.dtos.response.UsuarioResponseDTO;
import sptech.horticontrol.entity.Usuario;
import sptech.horticontrol.exceptions.RecursoNaoEncontradoException;
import sptech.horticontrol.exceptions.RegraNegocioException;
import sptech.horticontrol.repository.UsuarioRepository;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Cadastra um novo usuário com a senha em hash BCrypt — NUNCA em texto puro
    public Usuario cadastrar(Usuario usuario) {
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        return converterParaResponse(usuario);
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
    }

    public UsuarioResponseDTO atualizarPerfil(Long id, UsuarioPerfilRequestDTO dto) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefone(dto.getTelefone());

        return converterParaResponse(usuarioRepository.save(usuario));
    }

    public void atualizarSenha(Long id, UsuarioSenhaRequestDTO dto) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        if (!passwordEncoder.matches(dto.getSenhaAtual(), usuario.getSenha())) {
            throw new RegraNegocioException("Senha atual incorreta");
        }

        usuario.setSenha(passwordEncoder.encode(dto.getNovaSenha()));
        usuarioRepository.save(usuario);
    }

    private UsuarioResponseDTO converterParaResponse(Usuario u) {
        return new UsuarioResponseDTO(u.getIdUsuario(), u.getNome(), u.getEmail(), u.getTelefone());
    }
}
