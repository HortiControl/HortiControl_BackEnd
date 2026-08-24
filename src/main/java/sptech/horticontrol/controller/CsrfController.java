package sptech.horticontrol.controller;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CsrfController {

    /*
     * CsrfToken é fornecido automaticamente
     * pelo Spring Security.
     */
    @GetMapping("/csrf")
    public ResponseEntity<Map<String, String>> csrf(
            CsrfToken csrfToken) {

        /*
         * Retorna:
         *
         * {
         *   "headerName": "X-XSRF-TOKEN",
         *   "token": "valor-gerado"
         * }
         */
        return ResponseEntity.ok()

                /*
                 * Impede que a resposta com o CSRF
                 * seja armazenada em cache.
                 */
                .cacheControl(CacheControl.noStore())

                .body(Map.of(
                        "headerName",
                        csrfToken.getHeaderName(),

                        "token",
                        csrfToken.getToken()
                ));
    }
}
