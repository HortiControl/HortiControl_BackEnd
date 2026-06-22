package sptech.horticontrol.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

// Responsável por gerar, validar e ler tokens JWT.
// Usa a biblioteca JJWT 0.12.x — API diferente das versões anteriores.
@Service
public class JwtService {

    // Chave secreta lida do application.properties.
    // DEVE ter pelo menos 32 bytes (256 bits) em Base64 para o algoritmo HS256.
    // Nunca coloque a chave real diretamente no código — use variável de ambiente em produção.
    @Value("${jwt.secret}")
    private String secret;

    // Validade do token em milissegundos (lido do application.properties).
    // Exemplo: 86400000 = 24 horas
    @Value("${jwt.expiration}")
    private long expiration;

    // Gera um token JWT assinado para o e-mail informado
    public String gerarToken(String email) {
        return Jwts.builder()
                .subject(email)                                          // quem é o usuário
                .issuedAt(new Date())                                    // quando foi criado
                .expiration(new Date(System.currentTimeMillis() + expiration)) // quando expira
                .signWith(parseSecret())                                 // assina com HS256
                .compact();
    }

    // Extrai o e-mail do claim "sub" do token
    public String getEmail(String token) {
        return getClaims(token).getSubject();
    }

    // Valida o token: verifica assinatura e expiração
    // Retorna true se válido, false se inválido/expirado/adulterado
    public boolean validarToken(String token) {
        try {
            getClaims(token); // lança exceção se inválido
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Parseia o token e retorna os claims (dados do payload)
    // O JJWT verifica a assinatura automaticamente aqui
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(parseSecret())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Converte a chave Base64 do application.properties em um SecretKey para HMAC-SHA256
    private SecretKey parseSecret() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }
}
