package sptech.horticontrol.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import sptech.horticontrol.dtos.LoginDto;
import sptech.horticontrol.entity.Usuario;
import sptech.horticontrol.security.JwtService;
import sptech.horticontrol.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
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

    @GetMapping("/protegido")
    public ResponseEntity<String> rotaProtegida() {
        return ResponseEntity.ok("Sucesso! Você acessou uma rota protegida usando um Token JWT válido.");
    }

    // POST /usuarios — cadastro de novo usuário (rota pública)
    @PostMapping
    public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) {
        return ResponseEntity.status(201).body(usuarioService.cadastrar(usuario));
    }

    // POST /usuarios/login — autenticação (rota pública)
    // Recebe email e senha no corpo JSON: { "email": "...", "senha": "..." }
    // Retorna o token JWT se as credenciais forem válidas
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
}
