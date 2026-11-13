package sptech.horticontrol.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import sptech.horticontrol.cache.PaginaCache;
import sptech.horticontrol.dto.response.MercadoResponseDTO;
import sptech.horticontrol.enums.TipoMercado;
import sptech.horticontrol.repository.MercadoRepository;

// As leituras ficam em um bean separado porque o @Cacheable só funciona
// quando o método é chamado por outro bean (via proxy do Spring).
// A chave inclui o tipo da listagem, a página, o tamanho e a ordenação,
// evitando que listagens diferentes compartilhem a mesma entrada.
@Component
public class MercadoLeituraCache {

    public static final String CACHE = "mercados";

    private final MercadoRepository mercadoRepository;

    public MercadoLeituraCache(MercadoRepository mercadoRepository) {
        this.mercadoRepository = mercadoRepository;
    }

    @Cacheable(cacheNames = CACHE,
            key = "'todos:' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort")
    public PaginaCache<MercadoResponseDTO> listarTodos(Pageable pageable) {
        return PaginaCache.de(mercadoRepository.findAll(pageable)
                .map(MercadoService::converterParaResponse));
    }

    @Cacheable(cacheNames = CACHE,
            key = "'consignados:' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort")
    public PaginaCache<MercadoResponseDTO> listarConsignados(Pageable pageable) {
        return PaginaCache.de(mercadoRepository.findByTipoMercado(TipoMercado.CONSIGNADO, pageable)
                .map(MercadoService::converterParaResponse));
    }

    @Cacheable(cacheNames = CACHE,
            key = "'normais:' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort")
    public PaginaCache<MercadoResponseDTO> listarNormais(Pageable pageable) {
        return PaginaCache.de(mercadoRepository.findByTipoMercado(TipoMercado.NORMAL, pageable)
                .map(MercadoService::converterParaResponse));
    }
}
