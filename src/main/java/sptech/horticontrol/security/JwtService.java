package sptech.horticontrol.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

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

    /*
     * Nome do sistema que emite o JWT.
     */
    @Value("${jwt.issuer:hortcontrol-api}")
    private String issuer;

    /*
     * Aplicação para a qual o JWT foi criado.
     */
    @Value("${jwt.audience:hortcontrol-web}")
    private String audience;

    /*
     * Pequena tolerância para diferenças entre
     * os relógios dos servidores.
     */
    @Value("${jwt.clock-skew-seconds:30}")
    private long clockSkewSeconds;




    // Gera um JWT para o usuário autenticado.
    public String gerarToken(String email) {
        /*
         * Momento atual usado nos claims de tempo.
         */
        Date agora = new Date();

        return Jwts.builder()

                /*
                 * iss: identifica quem criou o JWT.
                 */
                .issuer(issuer)

                /*
                 * sub: identifica o usuário.
                 */
                .subject(email)

                /*
                 * aud: identifica para qual aplicação
                 * o JWT foi criado.
                 */
                .audience()
                .add(audience)
                .and()

                /*
                 * iat: momento em que o JWT foi criado.
                 */
                .issuedAt(agora)

                /*
                 * nbf: o JWT só pode ser usado
                 * a partir desse momento.
                 */
                .notBefore(agora)

                /*
                 * exp: momento em que o JWT expira.
                 */
                .expiration(
                        new Date(
                                agora.getTime() + expiration
                        )
                )

                /*
                 * jti: identificador único do JWT.
                 * Ele é utilizado para revogação.
                 */
                .id(UUID.randomUUID().toString())

                /*
                 * Assina o JWT com a chave secreta
                 * e o algoritmo HS256.
                 */
                .signWith(
                        parseSecret(),
                        Jwts.SIG.HS256
                )

                /*
                 * Converte o JWT para o formato:
                 * header.payload.signature
                 */
                .compact();
    }

    /*
     * Valida o JWT e retorna seus claims.
     *
     * Se qualquer validação falhar, a biblioteca
     * lança uma JwtException.
     */
    public Claims validarEObterClaims(String token) {

        Jws<Claims> jwt = Jwts.parser()

                /*
                 * Define a chave que será utilizada
                 * para verificar a assinatura.
                 */
                .verifyWith(parseSecret())

                /*
                 * Exige que o emissor seja exatamente
                 * o configurado para esta aplicação.
                 */
                .requireIssuer(issuer)

                /*
                 * Exige que a audiência contenha
                 * a aplicação configurada.
                 */
                .requireAudience(audience)

                /*
                 * Permite uma pequena diferença
                 * entre os relógios dos servidores.
                 */
                .clockSkewSeconds(clockSkewSeconds)

                /*
                 * Finaliza a configuração do parser.
                 */
                .build()

                /*
                 * Processa um JWT assinado.
                 *
                 * Esse método também verifica:
                 * assinatura, expiração e notBefore.
                 */
                .parseSignedClaims(token);

        /*
         * Mesmo após validar a assinatura,
         * confirma que o algoritmo é exatamente HS256.
         */
        if (!"HS256".equals(
                jwt.getHeader().getAlgorithm()
        )) {
            throw new UnsupportedJwtException(
                    "Algoritmo JWT não permitido"
            );
        }

        /*
         * Payload validado do JWT.
         */
        Claims claims = jwt.getPayload();

        /*
         * Confirma a presença dos claims
         * obrigatórios para esta aplicação.
         */
        if (claims.getSubject() == null
                || claims.getSubject().isBlank()
                || claims.getId() == null
                || claims.getId().isBlank()
                || claims.getIssuedAt() == null
                || claims.getNotBefore() == null
                || claims.getExpiration() == null) {

            throw new JwtException(
                    "JWT sem claims obrigatórios"
            );
        }

        return claims;
    }
    /*
     * Converte a chave armazenada em Base64
     * para uma SecretKey utilizada pelo algoritmo HMAC.
     *
     * Keys.hmacShaKeyFor também rejeita chaves fracas.
     */
    private SecretKey parseSecret() {

        byte[] secretBytes =
                Decoders.BASE64.decode(secret);

        return Keys.hmacShaKeyFor(secretBytes);
    }
}
