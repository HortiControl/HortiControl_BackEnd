package sptech.horticontrol.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/*
 * Protege as rotas /internal/**, usadas apenas na comunicação
 * serviço-a-serviço com o microsserviço de exportação em PDF.
 *
 * Essas rotas não passam pelo fluxo de login do navegador: quem
 * chama é outro servidor, que não possui cookie nem sessão. A
 * autorização acontece por uma chave compartilhada entre os dois.
 */
@Component
public class InternalApiKeyFilter extends OncePerRequestFilter {

    public static final String CABECALHO_CHAVE_INTERNA = "X-Internal-Api-Key";

    private static final String PREFIXO_ROTAS_INTERNAS = "/internal/";

    private final String chaveEsperada;

    public InternalApiKeyFilter(
            @Value("${horticontrol.internal.api-key:}") String chaveEsperada) {

        this.chaveEsperada = chaveEsperada;
    }

    /*
     * O filtro só atua nas rotas internas. As demais seguem
     * pelo JwtFilter, sem qualquer alteração de comportamento.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith(PREFIXO_ROTAS_INTERNAS);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        if (!chaveConfere(request.getHeader(CABECALHO_CHAVE_INTERNA))) {

            /*
             * Responde 401 sem corpo: para quem não tem a chave,
             * a rota não revela sequer que existe.
             */
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }

    /*
     * Comparação em tempo constante. Um equals() comum retorna mais
     * cedo no primeiro caractere diferente, e essa diferença de tempo
     * permitiria descobrir a chave caractere a caractere.
     */
    private boolean chaveConfere(String chaveRecebida) {

        if (chaveRecebida == null
                || chaveEsperada == null
                || chaveEsperada.isBlank()) {

            return false;
        }

        return MessageDigest.isEqual(
                chaveRecebida.getBytes(StandardCharsets.UTF_8),
                chaveEsperada.getBytes(StandardCharsets.UTF_8));
    }
}
