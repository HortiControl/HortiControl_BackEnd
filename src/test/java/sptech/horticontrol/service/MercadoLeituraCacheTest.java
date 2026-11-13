package sptech.horticontrol.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import sptech.horticontrol.cache.PaginaCache;
import sptech.horticontrol.dto.response.MercadoResponseDTO;
import sptech.horticontrol.entity.Mercado;
import sptech.horticontrol.enums.TipoMercado;
import sptech.horticontrol.repository.MercadoRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MercadoLeituraCacheTest {

    @Mock
    private MercadoRepository mercadoRepository;

    @InjectMocks
    private MercadoLeituraCache mercadoLeituraCache;

    private Mercado mercado(String nome, TipoMercado tipo) {
        Mercado mercado = new Mercado();
        mercado.setId(1L);
        mercado.setNome(nome);
        mercado.setTipoMercado(tipo);
        return mercado;
    }

    @Test
    void deveListarTodosConvertendoParaDto() {

        Pageable pageable = PageRequest.of(0, 10);
        when(mercadoRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(mercado("Mercado MJ4", TipoMercado.NORMAL)), pageable, 1));

        PaginaCache<MercadoResponseDTO> resultado = mercadoLeituraCache.listarTodos(pageable);

        assertEquals(1, resultado.content().size());
        assertEquals("Mercado MJ4", resultado.content().getFirst().getNome());
        assertEquals(1, resultado.totalElements());

        verify(mercadoRepository).findAll(pageable);
    }

    @Test
    void deveListarConsignados() {

        Pageable pageable = PageRequest.of(0, 10);
        when(mercadoRepository.findByTipoMercado(TipoMercado.CONSIGNADO, pageable))
                .thenReturn(new PageImpl<>(List.of(mercado("Consignado", TipoMercado.CONSIGNADO)), pageable, 1));

        PaginaCache<MercadoResponseDTO> resultado = mercadoLeituraCache.listarConsignados(pageable);

        assertEquals(TipoMercado.CONSIGNADO, resultado.content().getFirst().getTipoMercado());

        verify(mercadoRepository).findByTipoMercado(TipoMercado.CONSIGNADO, pageable);
    }

    @Test
    void deveListarNormais() {

        Pageable pageable = PageRequest.of(0, 10);
        when(mercadoRepository.findByTipoMercado(TipoMercado.NORMAL, pageable))
                .thenReturn(new PageImpl<>(List.of(mercado("Normal", TipoMercado.NORMAL)), pageable, 1));

        PaginaCache<MercadoResponseDTO> resultado = mercadoLeituraCache.listarNormais(pageable);

        assertEquals(TipoMercado.NORMAL, resultado.content().getFirst().getTipoMercado());

        verify(mercadoRepository).findByTipoMercado(TipoMercado.NORMAL, pageable);
    }
}
