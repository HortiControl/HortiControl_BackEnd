package sptech.horticontrol.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import sptech.horticontrol.cache.PaginaCache;
import sptech.horticontrol.dto.response.MercadoResponseDTO;
import sptech.horticontrol.service.MercadoLeituraCache;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Map;

@Configuration
public class RedisCacheConfig {

    private static final String PREFIXO = "app::";
    private static final Duration TTL_PADRAO = Duration.ofMinutes(5);
    private static final Duration TTL_MERCADOS = Duration.ofMinutes(10);

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory,
                                               ObjectMapper objectMapper) {

        // Serializer tipado (sem default typing): grava JSON puro e desserializa
        // sempre para PaginaCache<MercadoResponseDTO>, sem aceitar tipos arbitrários.
        JavaType paginaDeMercados = objectMapper.getTypeFactory()
                .constructParametricType(PaginaCache.class, MercadoResponseDTO.class);
        var serializerMercados =
                new JacksonJsonRedisSerializer<PaginaCache<MercadoResponseDTO>>(objectMapper, paginaDeMercados);

        // Novos caches devem registrar o próprio serializer em withInitialCacheConfigurations.
        var padrao = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .prefixCacheNameWith(PREFIXO)
                .entryTtl(TTL_PADRAO);

        var mercados = padrao
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializerMercados))
                .entryTtl(TTL_MERCADOS);

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(padrao)
                .withInitialCacheConfigurations(Map.of(MercadoLeituraCache.CACHE, mercados))
                .build();
    }
}
