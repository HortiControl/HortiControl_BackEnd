package sptech.horticontrol.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Set;


// Classe responsável por centralizar as configurações dos cookies
@Component
public class CookieSecurityProperties {

    /*
     * Valores aceitos pelo atributo SameSite.
     * Strict:
     * O cookie não é enviado em requisições vindas de outros sites.
     *
     * Lax:
     * Permite alguns tipos de navegação externa, principalmente GET.
     *
     * None:
     * Permite o envio entre sites diferentes, mas exige Secure=true.
     */
    private static final Set<String> SAME_SITE_VALUES =
            Set.of("Strict", "Lax", "None");

    /*
     * Indica se o cookie pode ser enviado apenas por HTTPS.
     *
     * Em produção deve ser true.
     * Em desenvolvimento local usando HTTP precisa ser false.
     */
    private final boolean secure;

    /*
     * Define o comportamento do cookie em requisições entre sites.
     */
    private final String sameSite;

    /*
     * @Value lê uma propriedade do application.properties
     * ou de uma variável de ambiente.
     *
     * O valor depois de ":" é o valor padrão.
     *
     * Exemplo:
     * ${app.auth.cookie.secure:true}
     *
     * Significa:
     * Procure a propriedade app.auth.cookie.secure.
     * Se ela não existir, utilize true.
     */
    public CookieSecurityProperties(
            @Value("${app.auth.cookie.secure:true}")
            boolean secure,

            @Value("${app.auth.cookie.same-site:Strict}")
            String sameSite) {

        this.secure = secure;
        this.sameSite = normalizarSameSite(sameSite);

        /*
         * Os navegadores exigem Secure quando SameSite=None.
         *
         * Em vez de permitir uma configuração insegura,
         * a aplicação recusa sua inicialização.
         */
        if ("None".equals(this.sameSite) && !secure) {
            throw new IllegalStateException(
                    "SameSite=None exige cookie Secure"
            );
        }
    }

    public boolean isSecure() {
        return secure;
    }

    public String getSameSite() {
        return sameSite;
    }

    /*
     * Padroniza o valor recebido.
     *
     * Por exemplo:
     * strict -> Strict
     * NONE   -> None
     */
    private static String normalizarSameSite(String valor) {

        if (valor == null || valor.isBlank()) {
            return "Strict";
        }

        String normalizado =
                valor.substring(0, 1)
                        .toUpperCase(Locale.ROOT)
                        + valor.substring(1)
                        .toLowerCase(Locale.ROOT);

        /*
         * Impede que um valor inválido seja usado.
         */
        if (!SAME_SITE_VALUES.contains(normalizado)) {
            throw new IllegalArgumentException(
                    "app.auth.cookie.same-site deve ser Strict, Lax ou None"
            );
        }

        return normalizado;
    }
}