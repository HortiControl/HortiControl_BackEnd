package sptech.horticontrol.controller;

import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sptech.horticontrol.dtos.LoginDto;
import sptech.horticontrol.dtos.response.LoginResponseDTO;
import sptech.horticontrol.dtos.response.UsuarioResponseDTO;
import sptech.horticontrol.entity.Usuario;
import sptech.horticontrol.security.AuthCookieService;
import sptech.horticontrol.security.JwtService;
import sptech.horticontrol.security.TokenBlocklistService;
import sptech.horticontrol.service.UsuarioService;

/*
 * Responsável apenas por autenticação e sessão (login, logout, sessão
 * atual e verificação de rota protegida).
 *
 * O CRUD de usuários (cadastro, listagem, perfil e senha) permanece em
 * UsuarioController. Essa separação segue o princípio de responsabilidade
 * única: mudanças na regra de autenticação não precisam tocar a classe
 * responsável pelo gerenciamento de usuários, e vice-versa.
 */
@RestController
@RequestMapping("/usuarios")
@Tag(
        name = "Autenticação",
        description = "Login, logout e sessão via cookie JWT"
)
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenBlocklistService tokenBlocklistService;
    private final AuthCookieService authCookieService;

    public AuthController(
            UsuarioService usuarioService,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            TokenBlocklistService tokenBlocklistService,
            AuthCookieService authCookieService
    ) {
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.tokenBlocklistService = tokenBlocklistService;
        this.authCookieService = authCookieService;
    }

    /*
     * Endpoint utilizado para confirmar se uma rota protegida está
     * reconhecendo corretamente o usuário autenticado.
     *
     * A autorização efetiva desta rota deve estar configurada no
     * SecurityConfig. A anotação SecurityRequirement serve apenas para
     * documentar a autenticação no Swagger.
     */
    @Operation(
            summary = "Rota protegida para teste",
            description = "Confirma se o cookie JWT está sendo validado",
            security = @SecurityRequirement(name = "cookieAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário autenticado"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Credencial ausente, inválida ou expirada"
            )
    })
    @GetMapping("/protegido")
    public ResponseEntity<String> rotaProtegida() {
        return ResponseEntity.ok(
                "Sucesso! A rota protegida reconheceu uma sessão válida."
        );
    }

    /*
     * Endpoint de login.
     *
     * Antes da correção, o JWT era devolvido no corpo JSON. Isso permitia
     * que o frontend armazenasse a credencial no localStorage.
     *
     * Agora o JWT é enviado somente no cabeçalho Set-Cookie. O navegador
     * armazena o cookie, mas o atributo HttpOnly impede que o JavaScript
     * leia o token.
     */
    @Operation(
            summary = "Login",
            description = "Autentica o usuário e cria um cookie de sessão HttpOnly"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Login realizado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Credenciais inválidas"
            )
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody @Valid LoginDto loginDto
    ) {
        try {
            /*
             * O AuthenticationManager delega a validação para a
             * configuração do Spring Security.
             *
             * Normalmente ele carrega o usuário por meio de um
             * UserDetailsService e compara a senha usando BCrypt.
             */
            UsernamePasswordAuthenticationToken credenciais =
                    new UsernamePasswordAuthenticationToken(
                            loginDto.email(),
                            loginDto.senha()
                    );

            authenticationManager.authenticate(credenciais);

        } catch (AuthenticationException exception) {
            /*
             * Não informamos se o erro ocorreu no e-mail ou na senha.
             *
             * Uma mensagem diferente para cada caso permitiria que um
             * atacante descobrisse quais e-mails estão cadastrados.
             */
            return ResponseEntity
                    .status(401)
                    .build();
        }

        Usuario usuario = usuarioService.buscarPorEmail(loginDto.email());

        /*
         * O e-mail é utilizado como subject do JWT.
         *
         * O JwtService também deve adicionar e validar claims como:
         * iss, aud, iat, nbf, exp e jti.
         */
        String token = jwtService.gerarToken(usuario.getEmail());

        /*
         * O corpo possui apenas informações de exibição.
         *
         * O JWT não deve fazer parte do LoginResponseDTO.
         */
        LoginResponseDTO resposta = new LoginResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone()
        );

        return ResponseEntity
                .ok()

                /*
                 * Impede que respostas contendo dados de autenticação sejam
                 * armazenadas por caches do navegador ou intermediários.
                 */
                .cacheControl(CacheControl.noStore())

                /*
                 * O AuthCookieService cria o cookie com atributos como:
                 *
                 * HttpOnly;
                 * Secure em produção;
                 * SameSite;
                 * Path=/;
                 * Max-Age.
                 */
                .header(
                        HttpHeaders.SET_COOKIE,
                        authCookieService.criarCookie(token).toString()
                )

                .body(resposta);
    }

    /*
     * Retorna o usuário da sessão atual.
     *
     * O objeto Authentication é construído pelo JwtFilter depois que o
     * cookie JWT é validado.
     *
     * O cliente não envia um ID para escolher o usuário. A identidade vem
     * do subject validado do JWT, disponibilizado por getName().
     */
    @Operation(
            summary = "Obter sessão atual",
            description = "Retorna os dados do usuário autenticado",
            security = @SecurityRequirement(name = "cookieAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Sessão válida"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Sessão ausente ou inválida"
            )
    })
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> me(
            Authentication authentication
    ) {
        UsuarioResponseDTO usuario =
                usuarioService.buscarPerfilPorEmail(
                        authentication.getName()
                );

        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.noStore())
                .body(usuario);
    }

    /*
     * Logout precisa usar POST porque altera o estado da sessão.
     *
     * A operação também deve ser protegida por CSRF. Um endpoint GET não
     * seria adequado porque poderia ser acionado por links, crawlers ou
     * mecanismos de pré-carregamento.
     */
    @Operation(
            summary = "Logout",
            description = "Revoga o JWT atual e remove o cookie de autenticação",
            security = @SecurityRequirement(name = "cookieAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Logout realizado"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Prova CSRF ausente ou inválida"
            )
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request
    ) {
        /*
         * O token é extraído do cookie HORTCONTROL_AUTH.
         *
         * O controller não procura mais o JWT no cabeçalho Authorization.
         */
        String token = authCookieService.extrairToken(request);

        if (token != null) {
            try {
                /*
                 * A validação devolve os claims somente quando assinatura,
                 * algoritmo, emissor, audiência e período de validade são
                 * aceitos.
                 */
                var claims = jwtService.validarEObterClaims(token);

                /*
                 * A blocklist guarda apenas o jti e sua expiração.
                 *
                 * Não é necessário manter o JWT completo na memória.
                 */
                tokenBlocklistService.revogar(
                        claims.getId(),
                        claims.getExpiration()
                );

            } catch (JwtException | IllegalArgumentException ignored) {
                /*
                 * Mesmo que o cookie esteja expirado ou adulterado, ele
                 * ainda deve ser removido do navegador.
                 *
                 * Não devemos devolver detalhes criptográficos ao cliente.
                 */
            }
        }

        /*
         * Remove qualquer autenticação associada à requisição atual.
         */
        SecurityContextHolder.clearContext();

        return ResponseEntity
                .noContent()
                .cacheControl(CacheControl.noStore())

                /*
                 * O cookie é removido usando o mesmo nome, Path e demais
                 * configurações utilizadas durante sua criação.
                 *
                 * O AuthCookieService configura Max-Age=0.
                 */
                .header(
                        HttpHeaders.SET_COOKIE,
                        authCookieService.removerCookie().toString()
                )

                .build();
    }
}
