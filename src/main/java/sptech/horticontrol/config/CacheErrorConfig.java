package sptech.horticontrol.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Configuration;

// Falhas do Redis não podem derrubar os endpoints: em caso de erro o cache é
// ignorado e a consulta segue direto para o banco. Fica em uma classe separada
// porque CachingConfigurer é instanciado cedo pelo Spring.
@Configuration
public class CacheErrorConfig implements CachingConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(CacheErrorConfig.class);

    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {

            @Override
            public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
                logger.warn("Falha ao ler o cache '{}' (chave '{}'): {}", cache.getName(), key, e.getMessage());
            }

            @Override
            public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
                logger.warn("Falha ao gravar no cache '{}' (chave '{}'): {}", cache.getName(), key, e.getMessage());
            }

            @Override
            public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
                logger.error("Falha ao invalidar o cache '{}' (chave '{}'); dados podem ficar defasados até o TTL: {}",
                        cache.getName(), key, e.getMessage());
            }

            @Override
            public void handleCacheClearError(RuntimeException e, Cache cache) {
                logger.error("Falha ao limpar o cache '{}'; dados podem ficar defasados até o TTL: {}",
                        cache.getName(), e.getMessage());
            }
        };
    }
}
