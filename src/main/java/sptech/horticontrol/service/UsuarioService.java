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
import java.util.Optional;

/*
 * @Service informa ao Spring que esta classe contém regras de negócio.
 *
 * O controller recebe as requisições HTTP, mas delega para este service
 * operações como cadastro, busca, atualização de perfil e alteração de senha.
 */
@Service
public class UsuarioService {

    /*
     * Responsável pela comunicação com o banco de dados.
     */
    private final UsuarioRepository usuarioRepository;

    /*
     * Responsável por criar e comparar hashes de senha.
     *
     * Normalmente o PasswordEncoder é configurado como BCryptPasswordEncoder
     * no SecurityConfig.
     */
    private final PasswordEncoder passwordEncoder;

    /*
     * As dependências são recebidas pelo construtor.
     *
     * O Spring localiza automaticamente as implementações e cria o service.
     */
    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /*
     * Cadastra um novo usuário.
     *
     * Antes de salvar, o método:
     *
     * 1. Confirma que o e-mail ainda não está sendo utilizado;
     * 2. Transforma a senha em um hash;
     * 3. Salva o usuário no banco de dados.
     */
    public Usuario cadastrar(Usuario usuario) {

        /*
         * Impede que dois usuários utilizem o mesmo e-mail.
         *
         * Essa verificação também deve ser acompanhada de uma constraint
         * UNIQUE na coluna de e-mail do banco de dados.
         */
        Optional<Usuario> usuarioComMesmoEmail =
                usuarioRepository.findByEmail(usuario.getEmail());

        if (usuarioComMesmoEmail.isPresent()) {
            throw new RegraNegocioException(
                    "Este e-mail já está em uso."
            );
        }

        /*
         * A senha nunca deve ser salva em texto puro.
         *
         * O encode produz um hash que será armazenado no banco.
         * Não é possível recuperar a senha original a partir desse hash.
         */
        String senhaProtegida =
                passwordEncoder.encode(usuario.getSenha());

        usuario.setSenha(senhaProtegida);

        return usuarioRepository.save(usuario);
    }

    /*
     * Retorna todos os usuários.
     *
     * Atenção: esse método devolve entidades Usuario. Se a entidade permitir
     * a serialização do campo senha, o hash poderá aparecer na resposta HTTP.
     *
     * O recomendado é restringir essa operação a administradores e retornar
     * uma lista de UsuarioResponseDTO.
     */
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    /*
     * Busca um usuário pelo ID e converte a entidade para DTO.
     *
     * O DTO impede que campos internos, como o hash da senha, sejam devolvidos
     * na resposta.
     */
    public UsuarioResponseDTO buscarPorId(Long id) {

        Usuario usuario = usuarioRepository
                .findById(id)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado"
                        )
                );

        return converterParaResponse(usuario);
    }

    /*
     * Busca a entidade completa pelo e-mail.
     *
     * Esse método é utilizado internamente durante o login e nas operações
     * que precisam localizar o usuário autenticado.
     *
     * A entidade não deve ser devolvida diretamente pelo controller.
     */
    public Usuario buscarPorEmail(String email) {

        return usuarioRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado"
                        )
                );
    }

    /*
     * Retorna o perfil do usuário autenticado.
     *
     * O e-mail recebido neste método vem do Authentication do Spring
     * Security, e não de um parâmetro controlado diretamente pelo navegador.
     *
     * No controller:
     *
     * authentication.getName()
     *
     * Esse nome foi obtido do subject do JWT depois de sua validação.
     */
    public UsuarioResponseDTO buscarPerfilPorEmail(
            String emailAutenticado
    ) {
        Usuario usuario = buscarPorEmail(emailAutenticado);

        return converterParaResponse(usuario);
    }

    /*
     * Atualiza um perfil a partir de um ID.
     *
     * Este método foi mantido para compatibilidade com as rotas antigas.
     * Se o ID vier diretamente do navegador, é necessário verificar se o
     * usuário autenticado possui autorização para modificar esse perfil.
     *
     * Caso contrário, poderá existir uma vulnerabilidade IDOR/BOLA.
     */
    public UsuarioResponseDTO atualizarPerfil(
            Long id,
            UsuarioPerfilRequestDTO dto
    ) {
        Usuario usuario = usuarioRepository
                .findById(id)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado"
                        )
                );

        /*
         * Confirma se o novo e-mail já pertence a outro usuário.
         */
        Optional<Usuario> usuarioComMesmoEmail =
                usuarioRepository.findByEmail(dto.getEmail());

        /*
         * O e-mail pode aparecer na consulta quando pertence ao próprio
         * usuário que está sendo atualizado.
         *
         * O erro acontece somente quando o e-mail pertence a um ID diferente.
         */
        boolean emailPertenceAOutroUsuario =
                usuarioComMesmoEmail.isPresent()
                        && !usuarioComMesmoEmail
                        .get()
                        .getIdUsuario()
                        .equals(id);

        if (emailPertenceAOutroUsuario) {
            throw new RegraNegocioException(
                    "Este e-mail já está em uso por outro usuário."
            );
        }

        /*
         * Atualiza somente os campos permitidos pelo DTO.
         *
         * Não usamos a entidade completa enviada pelo cliente, pois isso
         * poderia permitir alteração de campos que não deveriam ser editáveis.
         */
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefone(dto.getTelefone());

        Usuario usuarioAtualizado =
                usuarioRepository.save(usuario);

        return converterParaResponse(usuarioAtualizado);
    }

    /*
     * Atualiza o perfil do próprio usuário autenticado.
     *
     * Este é o método recomendado para PUT /usuarios/me/perfil.
     *
     * A identidade vem do e-mail presente no SecurityContext, reduzindo o
     * risco de o cliente escolher o perfil de outro usuário modificando um ID.
     */
    public UsuarioResponseDTO atualizarPerfilPorEmail(
            String emailAutenticado,
            UsuarioPerfilRequestDTO dto
    ) {
        /*
         * Primeiro localiza o usuário associado à sessão.
         */
        Usuario usuarioAutenticado =
                buscarPorEmail(emailAutenticado);

        /*
         * Depois reutiliza a regra de atualização já existente.
         *
         * O ID utilizado foi obtido internamente, e não do navegador.
         */
        return atualizarPerfil(
                usuarioAutenticado.getIdUsuario(),
                dto
        );
    }

    /*
     * Atualiza a senha a partir de um ID.
     *
     * O método exige a senha atual antes de permitir a alteração.
     *
     * Esta versão baseada em ID é considerada legada. Se for exposta pelo
     * controller, deverá possuir uma verificação de propriedade ou permissão.
     */
    public void atualizarSenha(
            Long id,
            UsuarioSenhaRequestDTO dto
    ) {
        Usuario usuario = usuarioRepository
                .findById(id)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado"
                        )
                );

        /*
         * passwordEncoder.matches não descriptografa a senha.
         *
         * Ele aplica o algoritmo sobre a senha informada e verifica se o
         * resultado corresponde ao hash armazenado.
         */
        boolean senhaAtualCorreta =
                passwordEncoder.matches(
                        dto.getSenhaAtual(),
                        usuario.getSenha()
                );

        if (!senhaAtualCorreta) {
            throw new RegraNegocioException(
                    "Senha atual incorreta"
            );
        }

        /*
         * A nova senha também precisa ser convertida em hash antes de ser
         * salva no banco de dados.
         */
        String novaSenhaProtegida =
                passwordEncoder.encode(dto.getNovaSenha());

        usuario.setSenha(novaSenhaProtegida);

        usuarioRepository.save(usuario);
    }

    /*
     * Atualiza a senha do próprio usuário autenticado.
     *
     * Este é o método recomendado para PUT /usuarios/me/senha.
     *
     * O usuário não informa o ID que terá a senha alterada. A identidade vem
     * do e-mail validado e armazenado no SecurityContext.
     */
    public void atualizarSenhaPorEmail(
            String emailAutenticado,
            UsuarioSenhaRequestDTO dto
    ) {
        Usuario usuarioAutenticado =
                buscarPorEmail(emailAutenticado);

        atualizarSenha(
                usuarioAutenticado.getIdUsuario(),
                dto
        );
    }

    /*
     * Converte a entidade Usuario para UsuarioResponseDTO.
     *
     * O DTO contém somente os campos que podem aparecer na resposta:
     *
     * id;
     * nome;
     * e-mail;
     * telefone.
     *
     * O hash da senha não é incluído.
     */
    private UsuarioResponseDTO converterParaResponse(
            Usuario usuario
    ) {
        return new UsuarioResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone()
        );
    }
}