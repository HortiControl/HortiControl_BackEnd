package sptech.horticontrol.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import sptech.horticontrol.dtos.LoginDto;
import sptech.horticontrol.dtos.request.UsuarioPerfilRequestDTO;
import sptech.horticontrol.dtos.request.UsuarioSenhaRequestDTO;
import sptech.horticontrol.dtos.response.UsuarioResponseDTO;
import sptech.horticontrol.entity.Usuario;
import sptech.horticontrol.security.JwtService;
import sptech.horticontrol.service.UsuarioService;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuários", description = "Gerenciamento de usuários e autenticação JWT")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public UsuarioController(UsuarioService usuarioService,
                             JwtService jwtService,
                             AuthenticationManager authenticationManager) {
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Operation(
            summary = "Rota protegida (teste JWT)",
            description = "Endpoint de teste para validar se o token JWT está funcionando",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token válido"),
            @ApiResponse(responseCode = "401", description = "Token inválido ou ausente")
    })
    @GetMapping("/protegido")
    public ResponseEntity<String> rotaProtegida() {
        return ResponseEntity.ok("Sucesso! Você acessou uma rota protegida usando um Token JWT válido.");
    }

    // POST /usuarios — cadastro de novo usuário (rota pública)
    @Operation(
            summary = "Cadastrar usuário",
            description = "Cadastra um novo usuário (rota pública)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) {
        return ResponseEntity.status(201).body(usuarioService.cadastrar(usuario));
    }

    // POST /usuarios/login — autenticação (rota pública)
    // Recebe email e senha no corpo JSON: { "email": "...", "senha": "..." }
    // Retorna o token JWT se as credenciais forem válidas
    @Operation(
            summary = "Login",
            description = "Autentica o usuário e retorna um token JWT (rota pública)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
        try {
            // Delega a validação das credenciais para o AuthenticationManager
            // Ele chama o UsuarioDetailsService e compara a senha com BCrypt
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.senha())
            );
        } catch (BadCredentialsException e) {
            // Credenciais inválidas — retorna 401 sem detalhes (não diga se foi o e-mail ou a senha)
            return ResponseEntity.status(401).body("Credenciais inválidas");
        }

        // Se chegou aqui, as credenciais são válidas — gera e retorna o token
        String token = jwtService.gerarToken(loginDto.email());
        return ResponseEntity.ok(token);
    }

    @Operation(
            summary = "Listar usuários",
            description = "Retorna todos os usuários cadastrados",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios(){
        return ResponseEntity.status(200).body(usuarioService.listarUsuarios());
    }

    @Operation(
            summary = "Buscar usuário por ID",
            description = "Retorna os dados do perfil de um usuário específico (usado para carregar o Perfil)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @Operation(
            summary = "Atualizar perfil",
            description = "Atualiza nome, email e telefone do usuário",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil atualizado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PutMapping("/perfil/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizarPerfil(@PathVariable Long id, @RequestBody UsuarioPerfilRequestDTO dto) {

        return ResponseEntity.ok(usuarioService.atualizarPerfil(id, dto));

    }

    @Operation(
            summary = "Atualizar senha",
            description = "Altera a senha do usuário",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Senha atualizada"),
            @ApiResponse(responseCode = "400", description = "Senha atual incorreta"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PatchMapping("/senha/{id}")
    public ResponseEntity<Void> atualizarSenha(@PathVariable Long id, @RequestBody UsuarioSenhaRequestDTO dto) {

        usuarioService.atualizarSenha(id, dto);
        return ResponseEntity.status(204).build();

    }

}
