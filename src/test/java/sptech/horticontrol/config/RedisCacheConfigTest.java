package sptech.horticontrol.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import sptech.horticontrol.cache.PaginaCache;
import sptech.horticontrol.dto.response.MercadoResponseDTO;
import sptech.horticontrol.enums.TipoMercado;
import tools.jackson.databind.json.JsonMapper;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class RedisCacheConfigTest {

    private RedisCache cacheMercados;

    @BeforeEach
    void setUp() {
        RedisCacheManager manager = new RedisCacheConfig()
                .redisCacheManager(mock(RedisConnectionFactory.class), JsonMapper.builder().build());
        manager.afterPropertiesSet();

        cacheMercados = (RedisCache) manager.getCache("mercados");
        assertNotNull(cacheMercados);
    }

    // Regressão: a desserialização de um cache HIT devia devolver os tipos
    // corretos, e não LinkedHashMap.
    @Test
    @SuppressWarnings("unchecked")
    void deveSerializarEDesserializarPaginaDeMercadosComTiposCorretos() {

        SerializationPair<Object> par = (SerializationPair<Object>) cacheMercados
                .getCacheConfiguration().getValueSerializationPair();

        PaginaCache<MercadoResponseDTO> original = new PaginaCache<>(
                List.of(new MercadoResponseDTO(1L, "Mercado MJ4", TipoMercado.CONSIGNADO, "01234-567", "100")),
                1);

        ByteBuffer bytes = par.write(original);
        String json = StandardCharsets.UTF_8.decode(bytes.duplicate()).toString();
        assertTrue(json.contains("\"nome\":\"Mercado MJ4\""));
        assertFalse(json.contains("@class"));

        Object lido = par.read(bytes);

        PaginaCache<MercadoResponseDTO> pagina = assertInstanceOf(PaginaCache.class, lido);
        assertEquals(1, pagina.totalElements());
        MercadoResponseDTO dto = assertInstanceOf(MercadoResponseDTO.class, pagina.content().getFirst());
        assertEquals(1L, dto.getId());
        assertEquals("Mercado MJ4", dto.getNome());
        assertEquals(TipoMercado.CONSIGNADO, dto.getTipoMercado());
    }

    @Test
    void deveAplicarPrefixoETtlDeDezMinutosAoCacheDeMercados() {

        assertEquals("app::mercados::", cacheMercados.getCacheConfiguration().getKeyPrefixFor("mercados"));
        assertEquals(Duration.ofMinutes(10),
                cacheMercados.getCacheConfiguration().getTtlFunction().getTimeToLive("chave", "valor"));
        assertFalse(cacheMercados.getCacheConfiguration().getAllowCacheNullValues());
    }
}
