package sptech.horticontrol.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Filtro executado UMA VEZ por requisição.
// Lê o token JWT do header Authorization e, se válido,
// autentica o usuário no contexto de segurança do Spring.
@Component
public class JwtFilter extends OncePerRequestFilter {


    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenBlocklistService tokenBlocklistService;
    private final AuthCookieService authCookieService;

    public JwtFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService,
            TokenBlocklistService tokenBlocklistService,
            AuthCookieService authCookieService) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.tokenBlocklistService =
                tokenBlocklistService;
        this.authCookieService =
                authCookieService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        /*
         * Procura o JWT exclusivamente no cookie.
         *
         * Não existe fallback para Authorization Bearer.
         */
        String token =
                authCookieService.extrairToken(request);

        /*
         * Só tenta autenticar quando:
         *
         * 1. Existe JWT.
         * 2. A requisição ainda não foi autenticada.
         */
        if (token != null
                && SecurityContextHolder
                .getContext()
                .getAuthentication() == null) {

            try {
                /*
                 * Valida assinatura, algoritmo,
                 * emissor, audiência e datas.
                 */
                Claims claims =
                        jwtService
                                .validarEObterClaims(token);

                /*
                 * Verifica se o JWT foi revogado.
                 */
                if (!tokenBlocklistService
                        .estaRevogado(claims.getId())) {

                    /*
                     * Busca o usuário no banco.
                     *
                     * Um JWT válido não deve autenticar
                     * um usuário que já foi excluído.
                     */
                    UserDetails userDetails =
                            userDetailsService
                                    .loadUserByUsername(
                                            claims.getSubject()
                                    );

                    /*
                     * Objeto utilizado pelo Spring
                     * para representar uma autenticação.
                     *
                     * O segundo parâmetro é null porque
                     * não precisamos manter a senha.
                     */
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    /*
                     * Adiciona detalhes da requisição,
                     * como endereço remoto.
                     */
                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    /*
                     * Cria um contexto de segurança vazio.
                     */
                    SecurityContext context =
                            SecurityContextHolder
                                    .createEmptyContext();

                    /*
                     * Coloca o usuário autenticado
                     * dentro do contexto.
                     */
                    context.setAuthentication(authentication);

                    /*
                     * Disponibiliza a autenticação para
                     * o restante da requisição.
                     */
                    SecurityContextHolder
                            .setContext(context);
                }

            } catch (
                    JwtException
                    | IllegalArgumentException
                    | UsernameNotFoundException ignored) {

                /*
                 * Não coloca JWTs ou informações internas
                 * nos logs.
                 *
                 * Também não encerra a requisição aqui.
                 *
                 * Rotas públicas continuam funcionando.
                 * Rotas privadas serão recusadas depois
                 * pela SecurityFilterChain.
                 */
                SecurityContextHolder.clearContext();
            }
        }

        /*
         * Entrega a requisição para o próximo filtro.
         *
         * Sem esta linha a requisição não chegaria
         * aos controllers.
         */
        filterChain.doFilter(request, response);
    }
}