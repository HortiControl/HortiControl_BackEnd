package sptech.horticontrol.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sptech.horticontrol.dtos.request.ItemPedidoRequestDTO;
import sptech.horticontrol.dtos.request.PedidoRequestDTO;
import sptech.horticontrol.dtos.response.PedidoResponseDTO;
import sptech.horticontrol.entity.ItemPedido;
import sptech.horticontrol.entity.Mercado;
import sptech.horticontrol.entity.Pedido;
import sptech.horticontrol.entity.Produto;
import sptech.horticontrol.enums.StatusPedido;
import sptech.horticontrol.enums.TipoProduto;
import sptech.horticontrol.exceptions.RecursoNaoEncontradoException;
import sptech.horticontrol.exceptions.RegraNegocioException;
import sptech.horticontrol.repository.MercadoRepository;
import sptech.horticontrol.repository.PedidoRepository;
import sptech.horticontrol.repository.ProdutoRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private MercadoRepository mercadoRepository;

    @InjectMocks
    private PedidoService pedidoService;

    // public PedidoResponseDTO criarPedido(PedidoRequestDTO dto)
    @Test
    void deveCriarPedido() {

        Mercado mercado = new Mercado();
        mercado.setId(1L);
        mercado.setNome("Mercado Central");

        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Alface");
        produto.setPreco(new BigDecimal("10.00"));
        produto.setTipoProduto(TipoProduto.PRE_LAVADO);

        ItemPedidoRequestDTO itemDTO = new ItemPedidoRequestDTO();
        itemDTO.setProdutoId(1L);
        itemDTO.setQuantidade(2);

        PedidoRequestDTO pedidoDTO = new PedidoRequestDTO();
        pedidoDTO.setMercadoId(1L);
        pedidoDTO.setItens(List.of(itemDTO));

        ItemPedido item = new ItemPedido();
        item.setId(1L);
        item.setProduto(produto);
        item.setQuantidade(2);
        item.setPrecoUnitario(new BigDecimal("10.00"));

        Pedido pedidoSalvo = new Pedido();
        pedidoSalvo.setId(1L);
        pedidoSalvo.setMercado(mercado);
        pedidoSalvo.setStatusPedido(StatusPedido.ATIVO);
        pedidoSalvo.setValorTotal(new BigDecimal("20.00"));
        pedidoSalvo.setValorPago(BigDecimal.ZERO);
        pedidoSalvo.setItens(List.of(item));

        when(mercadoRepository.findById(1L))
                .thenReturn(Optional.of(mercado));

        when(produtoRepository.findById(1L))
                .thenReturn(Optional.of(produto));

        when(pedidoRepository.save(any(Pedido.class)))
                .thenReturn(pedidoSalvo);

        PedidoResponseDTO resultado =
                pedidoService.criarPedido(pedidoDTO);

        assertNotNull(resultado);
        assertEquals(StatusPedido.ATIVO, resultado.getStatusPedido());
        assertEquals(new BigDecimal("20.00"), resultado.getValorTotal());

        verify(mercadoRepository).findById(1L);
        verify(produtoRepository).findById(1L);
        verify(pedidoRepository).save(any(Pedido.class));
    }

    // public List<PedidoResponseDTO> listarPedidosAtivos(Long mercadoId)
    @Test
    void deveListarPedidosAtivos() {

        Mercado mercado = new Mercado();
        mercado.setId(1L);

        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setMercado(mercado);
        pedido.setStatusPedido(StatusPedido.ATIVO);
        pedido.setValorTotal(new BigDecimal("50.00"));
        pedido.setValorPago(BigDecimal.ZERO);
        pedido.setItens(new ArrayList<>());

        when(pedidoRepository.findByStatusPedido(StatusPedido.ATIVO))
                .thenReturn(List.of(pedido));

        List<PedidoResponseDTO> resultado =
                pedidoService.listarPedidosAtivos(null);

        assertEquals(1, resultado.size());

        verify(pedidoRepository)
                .findByStatusPedido(StatusPedido.ATIVO);
    }

    // aqui lista pedidos ativos -> por mercado
    @Test
    void deveListarPedidosAtivosPorMercado() {

        Mercado mercado = new Mercado();
        mercado.setId(1L);

        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setMercado(mercado);
        pedido.setStatusPedido(StatusPedido.ATIVO);
        pedido.setValorTotal(new BigDecimal("50.00"));
        pedido.setValorPago(BigDecimal.ZERO);
        pedido.setItens(new ArrayList<>());

        when(pedidoRepository.findByMercadoIdAndStatusPedido(
                1L,
                StatusPedido.ATIVO
        )).thenReturn(List.of(pedido));

        List<PedidoResponseDTO> resultado =
                pedidoService.listarPedidosAtivos(1L);

        assertEquals(1, resultado.size());

        verify(pedidoRepository)
                .findByMercadoIdAndStatusPedido(
                        1L,
                        StatusPedido.ATIVO
                );
    }

    // public List<PedidoResponseDTO> listarHistoricoPedidos(Long mercadoId)
    @Test
    void deveListarHistoricoPedidos() {

        Mercado mercado = new Mercado();
        mercado.setId(1L);

        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setMercado(mercado);
        pedido.setStatusPedido(StatusPedido.CONCLUIDO);
        pedido.setValorTotal(new BigDecimal("100.00"));
        pedido.setValorPago(new BigDecimal("100.00"));
        pedido.setItens(new ArrayList<>());

        when(pedidoRepository.findByStatusPedidoIn(
                List.of(StatusPedido.CONCLUIDO)))
                .thenReturn(List.of(pedido));

        List<PedidoResponseDTO> resultado =
                pedidoService.listarHistoricoPedidos(null);

        assertEquals(1, resultado.size());

        verify(pedidoRepository)
                .findByStatusPedidoIn(
                        List.of(StatusPedido.CONCLUIDO)
                );
    }

    // public void atualizarStatusPedido(Long id, StatusPedido novoStatus)
    @Test
    void deveAtualizarStatusPedido() {

        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setStatusPedido(StatusPedido.ATIVO);

        when(pedidoRepository.findById(1L))
                .thenReturn(Optional.of(pedido));

        pedidoService.atualizarStatusPedido(
                1L,
                StatusPedido.CONCLUIDO
        );

        assertEquals(
                StatusPedido.CONCLUIDO,
                pedido.getStatusPedido()
        );

        verify(pedidoRepository).findById(1L);
        verify(pedidoRepository).save(pedido);
    }

    // public void registrarPagamento(Long id, BigDecimal valorPagamento)
    // pagamento parcial -> ainda falta pagar uma parte do pedido
    @Test
    void deveRegistrarPagamentoParcial() {

        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setValorTotal(new BigDecimal("100.00"));
        pedido.setValorPago(new BigDecimal("20.00"));
        pedido.setStatusPedido(StatusPedido.ATIVO);

        when(pedidoRepository.findById(1L))
                .thenReturn(Optional.of(pedido));

        pedidoService.registrarPagamento(
                1L,
                new BigDecimal("30.00")
        );

        assertEquals(
                new BigDecimal("50.00"),
                pedido.getValorPago()
        );

        assertEquals(
                StatusPedido.ATIVO,
                pedido.getStatusPedido()
        );

        verify(pedidoRepository).save(pedido);
    }

    // pagamento total -> o pedido foi totalmente pago.
    @Test
    void deveConcluirPedidoQuandoPagamentoForTotal() {

        Pedido pedido = new Pedido();
        pedido.setValorTotal(new BigDecimal("100.00"));
        pedido.setValorPago(new BigDecimal("50.00"));
        pedido.setStatusPedido(StatusPedido.ATIVO);

        when(pedidoRepository.findById(1L))
                .thenReturn(Optional.of(pedido));

        pedidoService.registrarPagamento(
                1L,
                new BigDecimal("50.00")
        );

        assertEquals(
                StatusPedido.CONCLUIDO,
                pedido.getStatusPedido()
        );

        verify(pedidoRepository).save(pedido);
    }

    // public void deletarPedido(Long id)
    @Test
    void deveExcluirPedido() {

        when(pedidoRepository.existsById(1L))
                .thenReturn(true);

        pedidoService.deletarPedido(1L);

        verify(pedidoRepository).existsById(1L);
        verify(pedidoRepository).deleteById(1L);
    }

    // public void removerItemDoPedido(Long pedidoId, Long itemId)
    @Test
    void deveRemoverItemDoPedido() {

        Produto produto = new Produto();
        produto.setNome("Tomate");

        ItemPedido item = new ItemPedido();
        item.setId(1L);
        item.setProduto(produto);
        item.setQuantidade(2);
        item.setPrecoUnitario(new BigDecimal("5.00"));

        Pedido pedido = new Pedido();
        pedido.setItens(new ArrayList<>(List.of(item)));
        pedido.setValorTotal(new BigDecimal("10.00"));

        when(pedidoRepository.findById(1L))
                .thenReturn(Optional.of(pedido));

        pedidoService.removerItemDoPedido(1L, 1L);

        assertTrue(pedido.getItens().isEmpty());

        verify(pedidoRepository).delete(pedido);
    }

    // PARTE DAS EXCEÇÕES

    // cria exceção quando mercado não existir
    @Test
    void deveLancarExcecaoQuandoMercadoNaoExistir() {

        PedidoRequestDTO dto = new PedidoRequestDTO();
        dto.setMercadoId(99L);

        when(mercadoRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> pedidoService.criarPedido(dto)
        );

        verify(mercadoRepository).findById(99L);
    }

    // cria exceção quando pedido não tiver itens
    @Test
    void deveLancarExcecaoQuandoPedidoNaoTiverItens() {

        Mercado mercado = new Mercado();
        mercado.setId(1L);

        PedidoRequestDTO dto = new PedidoRequestDTO();
        dto.setMercadoId(1L);
        dto.setItens(new ArrayList<>());

        when(mercadoRepository.findById(1L))
                .thenReturn(Optional.of(mercado));

        assertThrows(
                RegraNegocioException.class,
                () -> pedidoService.criarPedido(dto)
        );

        verify(mercadoRepository).findById(1L);

        verify(pedidoRepository, never())
                .save(any());
    }

    // cria exceção quando produto não existir
    @Test
    void deveLancarExcecaoQuandoProdutoNaoExistir() {

        Mercado mercado = new Mercado();
        mercado.setId(1L);

        ItemPedidoRequestDTO itemDTO = new ItemPedidoRequestDTO();
        itemDTO.setProdutoId(99L);
        itemDTO.setQuantidade(2);

        PedidoRequestDTO dto = new PedidoRequestDTO();
        dto.setMercadoId(1L);
        dto.setItens(List.of(itemDTO));

        when(mercadoRepository.findById(1L))
                .thenReturn(Optional.of(mercado));

        when(produtoRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> pedidoService.criarPedido(dto)
        );

        verify(produtoRepository).findById(99L);
    }

    // cria exceção quando pedido não existir ao atualizar status
    @Test
    void deveLancarExcecaoQuandoPedidoNaoExistirAoAtualizarStatus() {

        when(pedidoRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> pedidoService.atualizarStatusPedido(
                        1L,
                        StatusPedido.CONCLUIDO
                )
        );

        verify(pedidoRepository).findById(1L);
    }

    // cria exceção quando pedido já estiver finalizado
    @Test
    void deveLancarExcecaoAoAtualizarPedidoFinalizado() {

        Pedido pedido = new Pedido();
        pedido.setStatusPedido(StatusPedido.CONCLUIDO);

        when(pedidoRepository.findById(1L))
                .thenReturn(Optional.of(pedido));

        assertThrows(
                RegraNegocioException.class,
                () -> pedidoService.atualizarStatusPedido(
                        1L,
                        StatusPedido.ATIVO
                )
        );

        verify(pedidoRepository).findById(1L);

        verify(pedidoRepository, never())
                .save(any());
    }

    // cria exceção quando pedido não existir ao registrar pagamento
    @Test
    void deveLancarExcecaoQuandoPedidoNaoExistirAoRegistrarPagamento() {

        when(pedidoRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> pedidoService.registrarPagamento(
                        1L,
                        new BigDecimal("50.00")
                )
        );

        verify(pedidoRepository).findById(1L);
    }

    // cria exceção quando pagamento exceder valor total
    @Test
    void deveLancarExcecaoQuandoPagamentoExcederValorTotal() {

        Pedido pedido = new Pedido();
        pedido.setValorTotal(new BigDecimal("100.00"));
        pedido.setValorPago(new BigDecimal("90.00"));

        when(pedidoRepository.findById(1L))
                .thenReturn(Optional.of(pedido));

        assertThrows(
                RegraNegocioException.class,
                () -> pedidoService.registrarPagamento(
                        1L,
                        new BigDecimal("20.00")
                )
        );

        verify(pedidoRepository, never())
                .save(any());
    }

    // cria exceção quando pedido não existir ao excluir
    @Test
    void deveLancarExcecaoAoExcluirPedidoInexistente() {

        when(pedidoRepository.existsById(1L))
                .thenReturn(false);

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> pedidoService.deletarPedido(1L)
        );

        verify(pedidoRepository).existsById(1L);

        verify(pedidoRepository, never())
                .deleteById(any());
    }

    // cria exceção quando pedido não existir ao remover item
    @Test
    void deveLancarExcecaoQuandoPedidoNaoExistirAoRemoverItem() {

        when(pedidoRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> pedidoService.removerItemDoPedido(1L, 1L)
        );

        verify(pedidoRepository).findById(1L);
    }

    // cria exceção quando item não existir no pedido
    @Test
    void deveLancarExcecaoQuandoItemNaoExistirNoPedido() {

        Pedido pedido = new Pedido();
        pedido.setItens(new ArrayList<>());

        when(pedidoRepository.findById(1L))
                .thenReturn(Optional.of(pedido));

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> pedidoService.removerItemDoPedido(1L, 99L)
        );

        verify(pedidoRepository).findById(1L);

        verify(pedidoRepository, never())
                .save(any());

        verify(pedidoRepository, never())
                .delete(any());
    }
}