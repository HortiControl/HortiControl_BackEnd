package sptech.horticontrol.config;

import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.data.redis.RedisConnectionFailureException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CacheErrorConfigTest {

    // Se o Redis cair, o cache é ignorado e a consulta segue para o banco
    @Test
    void naoDevePropagarFalhasDoRedis() {

        CacheErrorHandler handler = new CacheErrorConfig().errorHandler();
        Cache cache = mock(Cache.class);
        when(cache.getName()).thenReturn("mercados");
        RuntimeException falha = new RedisConnectionFailureException("Unable to connect to Redis");

        assertDoesNotThrow(() -> handler.handleCacheGetError(falha, cache, "chave"));
        assertDoesNotThrow(() -> handler.handleCachePutError(falha, cache, "chave", "valor"));
        assertDoesNotThrow(() -> handler.handleCacheEvictError(falha, cache, "chave"));
        assertDoesNotThrow(() -> handler.handleCacheClearError(falha, cache));
    }
}
