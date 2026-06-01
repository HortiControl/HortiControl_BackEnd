package sptech.horticontrol.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import sptech.horticontrol.dtos.request.UsuarioPerfilRequestDTO;
import sptech.horticontrol.dtos.request.UsuarioSenhaRequestDTO;
import sptech.horticontrol.dtos.response.UsuarioResponseDTO;
import sptech.horticontrol.entity.Usuario;
import sptech.horticontrol.exceptions.RecursoNaoEncontradoException;
import sptech.horticontrol.exceptions.RegraNegocioException;
import sptech.horticontrol.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    // public Usuario cadastrar(Usuario usuario)
    @Test
    void deveCadastrarUsuarioComSenhaCriptografada() {

        Usuario usuario = new Usuario();
        usuario.setNome("Joao");
        usuario.setEmail("joao@horticontrol.com");
        usuario.setSenha("12345");

        when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("12345")).thenReturn("senha_criptografada");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario resultado = usuarioService.cadastrar(usuario);

        assertEquals("senha_criptografada", resultado.getSenha());

        verify(usuarioRepository).findByEmail("joao@horticontrol.com");
        verify(passwordEncoder).encode("12345");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    // public List<Usuario> listarUsuarios()
    @Test
    void deveListarUsuarios() {

        Usuario u = new Usuario();
        u.setNome("Joao");

        when(usuarioRepository.findAll()).thenReturn(List.of(u));

        List<Usuario> resultado = usuarioService.listarUsuarios();

        assertEquals(1, resultado.size());
        verify(usuarioRepository).findAll();
    }

    // public UsuarioResponseDTO buscarPorId(Long id)
    @Test
    void deveBuscarPorId() {

        Long id = 1L;
        Usuario u = new Usuario();
        u.setIdUsuario(id);
        u.setNome("Maria");

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(u));

        UsuarioResponseDTO resultado = usuarioService.buscarPorId(id);

        assertEquals("Maria", resultado.getNome());
        verify(usuarioRepository).findById(id);
    }

    // public Usuario buscarPorEmail(String email)
    @Test
    void deveBuscarPorEmail() {

        String email = "maria@horticontrol.com";
        Usuario u = new Usuario();
        u.setEmail(email);

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(u));

        Usuario resultado = usuarioService.buscarPorEmail(email);

        assertEquals(email, resultado.getEmail());
        verify(usuarioRepository).findByEmail(email);
    }

    // public UsuarioResponseDTO atualizarPerfil(Long id, UsuarioPerfilRequestDTO dto)
    @Test
    void deveAtualizarPerfil() {

        Long id = 1L;

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(id);
        usuario.setNome("Nome Antigo");

        UsuarioPerfilRequestDTO dto = new UsuarioPerfilRequestDTO();
        dto.setNome("Nome Novo");
        dto.setEmail("novo@email.com");

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioResponseDTO resultado = usuarioService.atualizarPerfil(id, dto);

        assertEquals("Nome Novo", resultado.getNome());

        verify(usuarioRepository).findById(id);
        verify(usuarioRepository).findByEmail(dto.getEmail());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    // public void atualizarSenha(Long id, UsuarioSenhaRequestDTO dto)
    @Test
    void deveAtualizarSenha() {

        Long id = 1L;
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(id);
        usuario.setSenha("senha_hash_antiga");

        UsuarioSenhaRequestDTO dto = new UsuarioSenhaRequestDTO();
        dto.setSenhaAtual("senha123");
        dto.setNovaSenha("senha456");

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha123", "senha_hash_antiga")).thenReturn(true);
        when(passwordEncoder.encode("senha456")).thenReturn("nova_senha_hash");

        usuarioService.atualizarSenha(id, dto);

        assertEquals("nova_senha_hash", usuario.getSenha());
        verify(usuarioRepository).save(usuario);
    }


    // PARTE DAS EXCEÇÕES

    // cria exceção quando tenta cadastrar com email existente
    @Test
    void deveLancarExcecaoAoCadastrarEmailJaExistente() {

        Usuario usuario = new Usuario();
        usuario.setEmail("admin@horticontrol.com");

        when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));

        assertThrows(
                RegraNegocioException.class,
                () -> usuarioService.cadastrar(usuario)
        );

        verify(usuarioRepository).findByEmail(usuario.getEmail());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    // cria exceção quando usuário não existe na busca por Id
    @Test
    void deveLancarExcecaoAoBuscarPorIdInexistente() {

        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> usuarioService.buscarPorId(99L)
        );
    }

    // cria exceção quando atualiza perfil e e-mail já pertence a outro usuário
    @Test
    void deveLancarExcecaoAoAtualizarPerfilComEmailEmUsoPorOutro() {

        Long id = 1L;
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(id);

        UsuarioPerfilRequestDTO dto = new UsuarioPerfilRequestDTO();
        dto.setEmail("outro@email.com");

        Usuario outroUsuario = new Usuario();
        outroUsuario.setIdUsuario(2L); // ID diferente significa que é de outra pessoa

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(outroUsuario));

        assertThrows(
                RegraNegocioException.class,
                () -> usuarioService.atualizarPerfil(id, dto)
        );

        verify(usuarioRepository, never()).save(any());
    }

    // cria exceção quando tenta atualizar a senha com a senha atual incorreta
    @Test
    void deveLancarExcecaoAoAtualizarSenhaIncorreta() {

        Long id = 1L;
        UsuarioSenhaRequestDTO dto = new UsuarioSenhaRequestDTO();
        dto.setSenhaAtual("senha_errada");

        Usuario usuario = new Usuario();
        usuario.setSenha("hash_correto");

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(dto.getSenhaAtual(), usuario.getSenha())).thenReturn(false);

        assertThrows(
                RegraNegocioException.class,
                () -> usuarioService.atualizarSenha(id, dto)
        );

        verify(usuarioRepository, never()).save(any());
    }
}