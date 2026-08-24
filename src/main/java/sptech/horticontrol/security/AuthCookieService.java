package sptech.horticontrol.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

// Classe responsável por realizar o CRUD do cookie com o token JWT
@Service
public class AuthCookieService {

    /*
     * Nome do cookie que armazenará o JWT.
     *
     * O frontend não precisa conhecer esse nome porque
     * o navegador envia o cookie automaticamente.
     */
    public static final String AUTH_COOKIE_NAME =
            "HORTCONTROL_AUTH";

    private final CookieSecurityProperties cookieProperties;

    /*
     * Tempo de validade do JWT em milissegundos.
     *
     * O cookie utilizará a mesma duração.
     */
    private final long jwtExpirationMillis;

    /*
     * O Spring injeta automaticamente:
     *
     * 1. CookieSecurityProperties.
     * 2. A propriedade jwt.expiration.
     */
    public AuthCookieService(
            CookieSecurityProperties cookieProperties,

            @Value("${jwt.expiration}")
            long jwtExpirationMillis) {

        /*
         * Não permite uma expiração igual ou menor que zero.
         */
        if (jwtExpirationMillis <= 0) {
            throw new IllegalArgumentException(
                    "jwt.expiration deve ser maior que zero"
            );
        }

        this.cookieProperties = cookieProperties;
        this.jwtExpirationMillis = jwtExpirationMillis;
    }

    /*
     * Procura o JWT nos cookies enviados pelo navegador.
     *
     * HttpServletRequest representa a requisição HTTP
     * que chegou ao backend.
     */
    public String extrairToken(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        /*
         * Uma requisição pode não ter nenhum cookie.
         */
        if (cookies == null) {
            return null;
        }

        /*
         * Percorre os cookies procurando aquele
         * que possui o nome HORTCONTROL_AUTH.
         */
        for (Cookie cookie : cookies) {

            if (AUTH_COOKIE_NAME.equals(cookie.getName())
                    && cookie.getValue() != null
                    && !cookie.getValue().isBlank()) {

                return cookie.getValue();
            }
        }

        /*
         * Retorna null quando o JWT não foi encontrado.
         */
        return null;
    }

    /*
     * Cria o cookie usado depois do login.
     */
    public ResponseCookie criarCookie(String token) {

        return cookieBase(token)

                /*
                 * A validade do cookie será igual
                 * à validade configurada para o JWT.
                 */
                .maxAge(
                        Duration.ofMillis(jwtExpirationMillis)
                )

                /*
                 * Finaliza a construção do cookie.
                 */
                .build();
    }

    /*
     * Cria um cookie vazio com validade zero.
     *
     * Quando o navegador recebe esse cookie,
     * ele remove o cookie anterior.
     */
    public ResponseCookie removerCookie() {

        return cookieBase("")
                .maxAge(Duration.ZERO)
                .build();
    }

    /*
     * Configura os atributos que são comuns tanto
     * na criação quanto na remoção do cookie.
     */
    private ResponseCookie.ResponseCookieBuilder cookieBase(
            String valor) {

        return ResponseCookie
                .from(AUTH_COOKIE_NAME, valor)

                /*
                 * HttpOnly impede que JavaScript acesse
                 * o JWT por document.cookie.
                 */
                .httpOnly(true)

                /*
                 * Quando true, o cookie só pode ser
                 * enviado por HTTPS.
                 */
                .secure(cookieProperties.isSecure())

                /*
                 * Controla o envio do cookie
                 * em requisições entre sites.
                 */
                .sameSite(cookieProperties.getSameSite())

                /*
                 * O cookie será enviado para todas
                 * as rotas da API.
                 */
                .path("/");
    }
}