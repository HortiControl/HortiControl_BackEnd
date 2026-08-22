package sptech.horticontrol.security;

import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// Mantém em memória os tokens que foram revogados via logout
// Como o JWT já expira sozinho em 24h, a lista não cresce indefinidamente
// além desse período
@Service
public class TokenBlocklistService {

    private final Set<String> tokensRevogados = ConcurrentHashMap.newKeySet();

    // Adiciona um token à lista de revogados (chamado no logout)
    public void revogar(String token) {
        tokensRevogados.add(token);
    }

    // Verifica se um token já foi revogado (chamado no JwtFilter)
    public boolean estaRevogado(String token) {
        return tokensRevogados.contains(token);
    }
}