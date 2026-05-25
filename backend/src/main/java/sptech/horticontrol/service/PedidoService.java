package sptech.horticontrol.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sptech.horticontrol.dtos.request.ItemPedidoRequestDTO;
import sptech.horticontrol.dtos.request.PedidoRequestDTO;
import sptech.horticontrol.dtos.response.ItemPedidoResponseDTO;
import sptech.horticontrol.dtos.response.MercadoResponseDTO;
import sptech.horticontrol.dtos.response.PedidoResponseDTO;
import sptech.horticontrol.entity.ItemPedido;
import sptech.horticontrol.entity.Mercado;
import sptech.horticontrol.entity.Pedido;
import sptech.horticontrol.entity.Produto;
import sptech.horticontrol.enums.StatusPedido;
import sptech.horticontrol.exceptions.RecursoNaoEncontradoException;
import sptech.horticontrol.exceptions.RegraNegocioException;
import sptech.horticontrol.repository.MercadoRepository;
import sptech.horticontrol.repository.PedidoRepository;
import sptech.horticontrol.repository.ProdutoRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private MercadoRepository mercadoRepository;

    public PedidoResponseDTO criarPedido(PedidoRequestDTO dto) {

        Mercado mercado = mercadoRepository.findById(dto.getMercadoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Mercado não encontrado"));

        if (dto.getItens() == null || dto.getItens().isEmpty()) {
            throw new RegraNegocioException("Não é possível criar um pedido sem produtos.");
        }

        Pedido novoPedido = new Pedido();
        novoPedido.setDataSolicitacao(LocalDate.now());
        novoPedido.setStatusPedido(StatusPedido.ATIVO);
        novoPedido.setMercado(mercado);

        List<ItemPedido> listaItens = processarItens(dto.getItens(), novoPedido);
        novoPedido.setItens(listaItens);

        BigDecimal total = calcularValorTotal(listaItens);
        novoPedido.setValorTotal(total);
        novoPedido.setValorPago(BigDecimal.ZERO);

        return converterParaResponse(pedidoRepository.save(novoPedido));
    }

    public List<PedidoResponseDTO> listarPedidosAtivos(Long mercadoId) {
        List<Pedido> pedidosAtivos;

        if (mercadoId != null) {
            pedidosAtivos = pedidoRepository.findByMercadoIdAndStatusPedido(mercadoId, StatusPedido.ATIVO);
        } else {
            pedidosAtivos = pedidoRepository.findByStatusPedido(StatusPedido.ATIVO);
        }

        return pedidosAtivos.stream()
                .map(this::converterParaResponse)
                .collect(Collectors.toList());
    }

    public List<PedidoResponseDTO> listarHistoricoPedidos(Long mercadoId) {

        List<StatusPedido> statusFechados = List.of(StatusPedido.CONCLUIDO);
        List<Pedido> pedidosHistorico;

        if (mercadoId != null) {
            pedidosHistorico = pedidoRepository.findByMercadoIdAndStatusPedidoIn(mercadoId, statusFechados);
        } else {
            pedidosHistorico = pedidoRepository.findByStatusPedidoIn(statusFechados);
        }

        return pedidosHistorico.stream()
                .map(this::converterParaResponse)
                .collect(Collectors.toList());
    }

    public void atualizarStatusPedido(Long id, StatusPedido novoStatus) {

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado"));

        if (pedido.getStatusPedido() != StatusPedido.ATIVO) {
            throw new RegraNegocioException("Pedido já finalizado não pode ser alterado.");
        }

        pedido.setStatusPedido(novoStatus);
        pedidoRepository.save(pedido);
    }

    @Transactional
    public void registrarPagamento(Long id, BigDecimal valorPagamento) {

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado"));

        BigDecimal novoValorPago = pedido.getValorPago().add(valorPagamento);

        if (novoValorPago.compareTo(pedido.getValorTotal()) > 0) {
            throw new RegraNegocioException("O valor informado excede o saldo devedor do pedido.");
        }

        pedido.setValorPago(novoValorPago);

        if (novoValorPago.compareTo(pedido.getValorTotal()) >= 0) {
            pedido.setStatusPedido(StatusPedido.CONCLUIDO);
        }

        pedidoRepository.save(pedido);

    }

    public void deletarPedido(Long id) {

        if (!pedidoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Pedido não encontrado");
        }

        pedidoRepository.deleteById(id);

    }

    public void removerItemDoPedido(Long pedidoId, Long itemId) {

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado"));

        boolean removido = pedido.getItens().removeIf(item -> item.getId().equals(itemId));

        if (!removido) {
            throw new RecursoNaoEncontradoException("Item não encontrado no pedido");
        }

        if (pedido.getItens().isEmpty()) {
            pedidoRepository.delete(pedido);
            return;
        }

        pedido.setValorTotal(calcularValorTotal(pedido.getItens()));
        pedidoRepository.save(pedido);
    }

    private List<ItemPedido> processarItens(List<ItemPedidoRequestDTO> itensDto, Pedido pedido) {

        List<ItemPedido> itens = new ArrayList<>();

        for (ItemPedidoRequestDTO dto : itensDto) {

            Produto produto = produtoRepository.findById(dto.getProdutoId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado"));

            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(dto.getQuantidade());
            item.setPrecoUnitario(produto.getPreco());

            itens.add(item);
        }

        return itens;
    }

    private BigDecimal calcularValorTotal(List<ItemPedido> itens) {
        return itens.stream()
                .map(ItemPedido::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private PedidoResponseDTO converterParaResponse(Pedido p) {

        BigDecimal valorAPagar = p.getValorTotal().subtract(p.getValorPago());

        MercadoResponseDTO mercado = new MercadoResponseDTO(
                p.getMercado().getId(),
                p.getMercado().getNome(),
                p.getMercado().getTipoMercado()
        );

        List<ItemPedidoResponseDTO> itens = p.getItens().stream()
                .map(i -> new ItemPedidoResponseDTO(
                        i.getId(),
                        i.getProduto().getNome(),
                        i.getProduto().getTipoProduto(),
                        i.getQuantidade(),
                        i.getPrecoUnitario(),
                        i.getSubTotal()
                )).collect(Collectors.toList());

        return new PedidoResponseDTO(
                p.getId(),
                p.getDataSolicitacao(),
                p.getValorTotal(),
                p.getStatusPedido(),
                p.getValorPago(),
                valorAPagar,
                mercado,
                itens
        );
    }
}