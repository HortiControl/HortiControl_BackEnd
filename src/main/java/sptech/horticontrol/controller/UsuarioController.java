package sptech.horticontrol.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sptech.horticontrol.dtos.request.UsuarioPerfilRequestDTO;
import sptech.horticontrol.dtos.request.UsuarioSenhaRequestDTO;
import sptech.horticontrol.dtos.response.UsuarioResponseDTO;
import sptech.horticontrol.entity.Usuario;
import sptech.horticontrol.service.UsuarioService;

import java.util.List;

/*
 * Responsável pelo CRUD de usuários e pelo autoatendimento de perfil/senha.
 *
 * O login, logout e a sessão atual ficam em AuthController. Essa separação
 * segue o princípio de responsabilidade única: mudanças na regra de
 * autenticação não precisam tocar esta classe, e vice-versa.
 */
@RestController
@RequestMapping("/usuarios")
@Tag(
        name = "Usuários",
        description = "Cadastro, listagem, perfil e senha de usuários"
)
public class UsuarioController {

    /*
     * A dependência é declarada como final porque deve ser definida
     * somente durante a criação do controller.
     *
     * O Spring injeta essa dependência por meio do construtor.
     */
    private final UsuarioService usuarioService;

    public UsuarioController(
            UsuarioService usuarioService
    ) {
        this.usuarioService = usuarioService;
    }

    /*
     * Cadastro de usuário.
     *
     * A anotação @Valid executa as validações declaradas na entidade ou DTO
     * antes que o método seja executado.
     */
    @Operation(
            summary = "Cadastrar usuário",
            description = "Cadastra um novo usuário"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuário cadastrado"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos"
            )
    })
    @PostMapping
    public ResponseEntity<Usuario> cadastrar(
            @RequestBody @Valid Usuario usuario
    ) {
        Usuario usuarioCadastrado = usuarioService.cadastrar(usuario);

        return ResponseEntity
                .status(201)
                .body(usuarioCadastrado);
    }

    /*
     * Retorna a lista de usuários já convertida para UsuarioResponseDTO.
     *
     * O hash da senha nunca é incluído na resposta, pois a entidade Usuario
     * não é mais devolvida diretamente pelo controller.
     *
     * Observação: este endpoint ainda não possui restrição por papel de
     * administrador (não há esse conceito implementado no sistema). Qualquer
     * usuário autenticado pode listar os demais usuários.
     */
    @Operation(
            summary = "Listar usuários",
            description = "Retorna os usuários cadastrados",
            security = @SecurityRequirement(name = "cookieAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista retornada"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado"
            )
    })
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {
        return ResponseEntity.ok(
                usuarioService.listarUsuarios()
        );
    }

    /*
     * Rota legada baseada em ID.
     *
     * Estar autenticado não significa que o usuário pode consultar qualquer
     * ID. O service ou uma regra de autorização precisa verificar se o
     * usuário possui permissão sobre o recurso solicitado.
     */
    @Operation(
            summary = "Buscar usuário por ID",
            description = "Busca um usuário específico",
            security = @SecurityRequirement(name = "cookieAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário encontrado"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário sem acesso ao recurso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                usuarioService.buscarPorId(id)
        );
    }

    /*
     * Rota legada baseada em ID.
     *
     * Antes de delegar ao service, confirmamos que o ID da URL pertence ao
     * próprio usuário autenticado. Sem essa verificação, qualquer usuário
     * autenticado poderia modificar o perfil de outro apenas trocando o ID
     * (IDOR/BOLA).
     *
     * O frontend corrigido deve usar PUT /usuarios/me/perfil.
     */
    @Operation(
            summary = "Atualizar perfil por ID",
            description = "Rota legada para atualização de perfil",
            security = @SecurityRequirement(name = "cookieAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Perfil atualizado"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário sem acesso ao recurso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado"
            )
    })
    @PutMapping("/perfil/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizarPerfil(
            @PathVariable Long id,
            @RequestBody @Valid UsuarioPerfilRequestDTO dto,
            Authentication authentication
    ) {
        Usuario usuarioAutenticado =
                usuarioService.buscarPorEmail(authentication.getName());

        if (!usuarioAutenticado.getIdUsuario().equals(id)) {
            throw new AccessDeniedException(
                    "Você não tem permissão para modificar o perfil de outro usuário."
            );
        }

        return ResponseEntity.ok(
                usuarioService.atualizarPerfil(id, dto)
        );
    }

    /*
     * Atualiza o perfil do próprio usuário autenticado.
     *
     * A identidade não é recebida do navegador. Ela vem do Authentication
     * criado pelo JwtFilter.
     */
    @Operation(
            summary = "Atualizar o próprio perfil",
            description = "Atualiza o perfil associado à sessão atual",
            security = @SecurityRequirement(name = "cookieAuth")
    )
    @PutMapping("/me/perfil")
    public ResponseEntity<UsuarioResponseDTO> atualizarMeuPerfil(
            Authentication authentication,
            @RequestBody @Valid UsuarioPerfilRequestDTO dto
    ) {
        UsuarioResponseDTO usuarioAtualizado =
                usuarioService.atualizarPerfilPorEmail(
                        authentication.getName(),
                        dto
                );

        return ResponseEntity.ok(usuarioAtualizado);
    }

    /*
     * Rota legada baseada em ID.
     *
     * Assim como a atualização de perfil, ela exige autorização de
     * propriedade. O frontend corrigido deve usar /usuarios/me/senha.
     */
    @Operation(
            summary = "Atualizar senha por ID",
            description = "Rota legada para alteração de senha",
            security = @SecurityRequirement(name = "cookieAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Senha atualizada"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Senha atual incorreta"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário sem acesso ao recurso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado"
            )
    })
    @PutMapping("/senha/{id}")
    public ResponseEntity<Void> atualizarSenha(
            @PathVariable Long id,
            @RequestBody @Valid UsuarioSenhaRequestDTO dto
    ) {
        usuarioService.atualizarSenha(id, dto);

        return ResponseEntity
                .noContent()
                .build();
    }

    /*
     * Atualiza a senha do usuário da sessão atual.
     *
     * A identidade vem do SecurityContext, e não de um ID controlado pelo
     * navegador.
     */
    @Operation(
            summary = "Atualizar a própria senha",
            description = "Altera a senha do usuário autenticado",
            security = @SecurityRequirement(name = "cookieAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Senha atualizada"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Senha atual incorreta ou dados inválidos"
            )
    })
    @PutMapping("/me/senha")
    public ResponseEntity<Void> atualizarMinhaSenha(
            Authentication authentication,
            @RequestBody @Valid UsuarioSenhaRequestDTO dto
    ) {
        usuarioService.atualizarSenhaPorEmail(
                authentication.getName(),
                dto
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}