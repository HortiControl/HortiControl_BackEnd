package sptech.horticontrol.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import sptech.horticontrol.security.CookieSecurityProperties;
import sptech.horticontrol.security.JwtFilter;

import java.util.List;
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CookieSecurityProperties cookieProperties;

    public SecurityConfig(
            JwtFilter jwtFilter,
            CookieSecurityProperties cookieProperties) {

        this.jwtFilter = jwtFilter;
        this.cookieProperties = cookieProperties;
    }
    // @Bean informa que o objeto retornado pelo método
    // deve ser gerenciado pelo Spring.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        /*
         * Armazena o valor esperado do CSRF em cookie.
         */
        CookieCsrfTokenRepository csrfRepository =
                new CookieCsrfTokenRepository();

        /*
         * O cookie CSRF poderá ser enviado
         * para todas as rotas.
         */
        csrfRepository.setCookiePath("/");

        /*
         * Personaliza os atributos do cookie CSRF.
         *
         * O frontend recebe o valor do token pelo
         * endpoint GET /csrf, portanto não precisa
         * ler diretamente o cookie CSRF.
         */
        csrfRepository.setCookieCustomizer(
                cookie -> cookie
                        .httpOnly(true)
                        .secure(
                                cookieProperties.isSecure()
                        )
                        .sameSite(
                                cookieProperties.getSameSite()
                        )
                        .path("/")
        );

        http
                /*
                 * Ativa o CORS com as regras
                 * declaradas mais abaixo.
                 */
                .cors(cors -> cors
                        .configurationSource(
                                corsConfigurationSource()
                        )
                )

                /*
                 * Ativa a proteção CSRF.
                 */
                .csrf(csrf -> csrf
                        .csrfTokenRepository(
                                csrfRepository
                        )
                )

                /*
                 * Impede que o Spring salve a URL
                 * solicitada em uma sessão HTTP.
                 */
                .requestCache(
                        cache -> cache.disable()
                )

                /*
                 * Desabilita a tela de login padrão
                 * do Spring Security.
                 */
                .formLogin(
                        form -> form.disable()
                )

                /*
                 * Desabilita autenticação HTTP Basic.
                 */
                .httpBasic(
                        basic -> basic.disable()
                )

                /*
                 * Desabilita o logout padrão porque
                 * existe um endpoint personalizado.
                 */
                .logout(
                        logout -> logout.disable()
                )

                /*
                 * Define que a API não manterá sessão HTTP.
                 *
                 * Cada requisição precisa apresentar
                 * um JWT válido.
                 */
                .sessionManagement(
                        session -> session
                                .sessionCreationPolicy(
                                        SessionCreationPolicy.STATELESS
                                )
                )

                /*
                 * Configura as respostas de erro.
                 */
                .exceptionHandling(
                        exceptions -> exceptions

                                /*
                                 * 401 significa que não existe
                                 * uma autenticação válida.
                                 */
                                .authenticationEntryPoint(
                                        (
                                                request,
                                                response,
                                                exception
                                        ) -> response.setStatus(401)
                                )

                                /*
                                 * 403 significa que a requisição
                                 * foi recusada por autorização
                                 * ou proteção CSRF.
                                 */
                                .accessDeniedHandler(
                                        (
                                                request,
                                                response,
                                                exception
                                        ) -> response.setStatus(403)
                                )
                )

                /*
                 * Define quais rotas são públicas
                 * e quais exigem autenticação.
                 */
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.GET, "/csrf").permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuarios/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuarios/logout").permitAll()

                        /*
                         * Todo o restante exige um JWT válido.
                         */
                        .anyRequest().authenticated()
                )


                /*
                 * Executa o JwtFilter antes do filtro
                 * padrão de usuário e senha.
                 */
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    // CONFIGURAÇÃO DAS REGRAS DO CORS
    // Configura quais frontends podem acessar a API através de um navegador
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Coloque aqui a URL exata do seu Front-end
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "https://horti-control-front-end.vercel.app" // <--- ADICIONE AQUI A URL DO SEU FRONTEND NO VERCEL
        ));
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE","PATCH", "OPTIONS")
        );
        // Permite o envio do header Authorization (seu Token!)
        configuration.setAllowedHeaders(List.of(
                "Accept",
                "Content-Type",
                "X-CSRF-TOKEN",
                "X-XSRF-TOKEN"
        ));

        /*
         * Permite que o navegador envie cookies
         * para a API.
         */
        configuration.setAllowCredentials(true);

        /*
         * O navegador pode guardar o resultado
         * do preflight CORS por uma hora.
         */
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Aplica essas regras para todas as rotas da sua API (/**)
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    // BCrypt é utilizado para criar hashes de senha.
    // Senhas não devem ser armazenadas em texto puro.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // AuthenticationManager executa a autenticação
    // usando o UserDetailsService e o PasswordEncoder.
    @Bean
    public AuthenticationManager authenticationManager(
            org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }
}