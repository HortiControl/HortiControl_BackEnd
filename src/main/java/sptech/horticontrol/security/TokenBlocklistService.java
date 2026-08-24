package sptech.horticontrol.security;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// Mantém em memória os tokens que foram revogados via logout
// Como o JWT já expira sozinho em 24h, a lista não cresce indefinidamente
// além desse período
@Service
public class TokenBlocklistService {

    /*
     * A chave do Map é o jti do JWT.
     * O valor é o momento em que o JWT expira.
     *
     * ConcurrentHashMap permite acesso seguro
     * por múltiplas requisições simultâneas.
     */
    private final Map<String, Instant> tokensRevogados =
            new ConcurrentHashMap<>();

    // Adiciona um token à lista de revogados (chamado no logout)
    public void revogar(
            String tokenId,
            Date expiracao) {

        /*
         * Se os dados obrigatórios não existirem,
         * não há como registrar a revogação.
         */
        if (tokenId == null
                || tokenId.isBlank()
                || expiracao == null) {
            return;
        }

        Instant expiraEm = expiracao.toInstant();

        /*
         * Só armazena o jti se o JWT ainda
         * não tiver expirado naturalmente.
         */
        if (expiraEm.isAfter(Instant.now())) {
            tokensRevogados.put(
                    tokenId,
                    expiraEm
            );
        }
    }


    // Verifica se um token já foi revogado (chamado no JwtFilter)
    public boolean estaRevogado(String tokenId) {

        /*
         * Antes da consulta, remove as entradas
         * que já perderam a utilidade.
         */
        limparExpirados();

        Instant expiraEm =
                tokensRevogados.get(tokenId);

        /*
         * Se o jti não estiver no Map,
         * o JWT não foi revogado.
         */
        if (expiraEm == null) {
            return false;
        }

        /*
         * Proteção adicional caso a entrada tenha
         * expirado entre a limpeza e esta verificação.
         */
        if (!expiraEm.isAfter(Instant.now())) {

            tokensRevogados.remove(
                    tokenId,
                    expiraEm
            );

            return false;
        }

        return true;
    }

    /*
     * Remove da memória os identificadores
     * de JWTs que já expiraram naturalmente.
     */
    private void limparExpirados() {

        Instant agora = Instant.now();

        tokensRevogados
                .entrySet()
                .removeIf(
                        entry ->
                                !entry.getValue()
                                        .isAfter(agora)
                );
    }

}