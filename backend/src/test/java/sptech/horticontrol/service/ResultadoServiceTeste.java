package sptech.horticontrol.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sptech.horticontrol.dtos.resultados.ResultadosResponseDTO;
import sptech.horticontrol.entity.ItemPedido;
import sptech.horticontrol.entity.Mercado;
import sptech.horticontrol.entity.Pedido;
import sptech.horticontrol.entity.Produto;
import sptech.horticontrol.enums.TipoEmbalagem;
import sptech.horticontrol.enums.TipoProduto;
import sptech.horticontrol.repository.PedidoRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResultadoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private ResultadoService resultadoService;

    // public ResultadosResponseDTO gerarResultados(String periodo)
    // Testando o caminho "feliz" com pedidos existentes para não quebrar
    @Test
    void deveGerarResultadosComDadosExistentes() {

        Mercado mercado = new Mercado();
        mercado.setNome("Mercado Teste");

        Produto produto = new Produto();
        produto.setNome("Alface");
        produto.setTipoEmbalagem(TipoEmbalagem.BANDEJA);
        produto.setTipoProduto(TipoProduto.PRE_LAVADO);

        ItemPedido item = new ItemPedido();
        item.setProduto(produto);
        item.setQuantidade(10);

        Pedido pedido = new Pedido();
        pedido.setMercado(mercado);
        pedido.setValorTotal(new BigDecimal("150.00"));
        pedido.setDataSolicitacao(LocalDate.now());
        pedido.setItens(List.of(item));

        when(pedidoRepository.findByDataSolicitacaoBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(pedido));

        ResultadosResponseDTO resultado = resultadoService.gerarResultados("HOJE");

        assertNotNull(resultado);
        assertEquals(new BigDecimal("150.00"), resultado.getFaturado());
        assertEquals(1L, resultado.getQuantidadePedidos());
        assertEquals(10, resultado.getEmbalagensVendidas().getBandejas());
        assertEquals(1, resultado.getRankingClientes().size());
        assertEquals(1, resultado.getRankingProdutos().size());

        verify(pedidoRepository).findByDataSolicitacaoBetween(any(LocalDate.class), any(LocalDate.class));
    }

    // Testando o comportamento quando não há nenhum pedido
    @Test
    void deveGerarResultadosZeradosQuandoNaoHouverPedidos() {

        when(pedidoRepository.findByDataSolicitacaoBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new ArrayList<>());

        ResultadosResponseDTO resultado = resultadoService.gerarResultados("ESTE_MES");

        assertNotNull(resultado);
        assertEquals(BigDecimal.ZERO, resultado.getFaturado());
        assertEquals(0L, resultado.getQuantidadePedidos());
        assertEquals(0, resultado.getEmbalagensVendidas().getBandejas());
        assertTrue(resultado.getRankingClientes().isEmpty());
        assertTrue(resultado.getRankingProdutos().isEmpty());
    }

    // Testando a inteligência da escolha de intervalo pelo switch case
    @Test
    void deveGerarResultadosParaAnoInteiro() {

        when(pedidoRepository.findByDataSolicitacaoBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new ArrayList<>());

        // A ação força o método calcularIntervalo a cair no 'case "ANO"'
        ResultadosResponseDTO resultado = resultadoService.gerarResultados("ANO");

        assertNotNull(resultado);
        // Garante que pelo menos o gráfico de evolução criou os 12 meses, mesmo sem pedidos
        assertEquals(12, resultado.getEvolucao().size()); 
        assertEquals("Jan", resultado.getEvolucao().get(0).getPeriodo());

        verify(pedidoRepository, times(2)).findByDataSolicitacaoBetween(any(), any());
    }

    // Testando o fallback (default) do switch de datas
    @Test
    void deveGerarResultadosComPeriodoDefault() {

        when(pedidoRepository.findByDataSolicitacaoBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new ArrayList<>());

        // Ao enviar um nome de período que não está mapeado no switch, ele cai no default (últimos 30 dias)
        ResultadosResponseDTO resultado = resultadoService.gerarResultados("TEXTO_ALEATORIO");

        assertNotNull(resultado);
        assertEquals(BigDecimal.ZERO, resultado.getFaturado());
    }
}