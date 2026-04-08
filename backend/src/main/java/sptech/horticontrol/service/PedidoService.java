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
import sptech.horticontrol.repository.MercadoRepository;
import sptech.horticontrol.repository.PedidoRepository;
import sptech.horticontrol.repository.ProdutoRepository;

import java.math.BigDecimal;
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

    @Transactional
    public PedidoResponseDTO criarPedido(PedidoRequestDTO dto) {

        Mercado mercado = mercadoRepository.findById(dto.getMercadoId())
                .orElseThrow(() -> new RuntimeException("Mercado não encontrado"));

        Pedido novoPedido = new Pedido();
        novoPedido.setDataSolicitacao(dto.getDataSolicitacao());
        novoPedido.setStatusPedido(StatusPedido.PENDENTE); // Pedido nasce como Ativo
        novoPedido.setMercado(mercado);

        List<ItemPedido> listaItens = processarItens(dto.getItens(), novoPedido);
        novoPedido.setItens(listaItens);

        BigDecimal total = calcularValorTotal(listaItens);
        novoPedido.setValorTotal(total);

        return converterParaResponse(pedidoRepository.save(novoPedido));
    }

    public List<PedidoResponseDTO> listarPedidosAtivos(Long mercadoId) {
        List<Pedido> pedidosAtivos;

        if (mercadoId != null) {
            pedidosAtivos = pedidoRepository.findByMercadoIdAndStatusPedido(mercadoId, StatusPedido.PENDENTE);
        } else {
            pedidosAtivos = pedidoRepository.findByStatusPedido(StatusPedido.PENDENTE);
        }

        return pedidosAtivos.stream()
                .map(this::converterParaResponse)
                .collect(Collectors.toList());
    }

    public List<PedidoResponseDTO> listarHistoricoPedidos(Long mercadoId) {

        List<StatusPedido> statusFechados = List.of(StatusPedido.CONCLUIDO, StatusPedido.CANCELADO);
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

    @Transactional
    public void atualizarStatusPedido(Long id, StatusPedido novoStatus) {

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        if (pedido.getStatusPedido() != StatusPedido.PENDENTE) {
            throw new RuntimeException("Não é possível alterar o status de um pedido já finalizado.");
        }

        pedido.setStatusPedido(novoStatus);
        pedidoRepository.save(pedido);

    }

    private List<ItemPedido> processarItens(List<ItemPedidoRequestDTO> itensDto, Pedido pedido) {
        List<ItemPedido> entidades = new ArrayList<>();

        for (ItemPedidoRequestDTO itemDto : itensDto) {
            Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(itemDto.getQuantidade());
            item.setPrecoUnitario(produto.getPreco());

            entidades.add(item);
        }
        return entidades;
    }

    private BigDecimal calcularValorTotal(List<ItemPedido> itens) {
        return itens.stream()
                .map(ItemPedido::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private PedidoResponseDTO converterParaResponse(Pedido p) {

        MercadoResponseDTO converterMercado = new MercadoResponseDTO(
                p.getMercado().getId(), p.getMercado().getNome(),
                p.getMercado().getTipoMercado(), p.getMercado().getObservacao()
        );

        List<ItemPedidoResponseDTO> converterItens = p.getItens().stream()
                .map(i -> new ItemPedidoResponseDTO(
                        i.getId(), i.getProduto().getNome(), i.getQuantidade(),
                        i.getPrecoUnitario(), i.getSubTotal()
                )).collect(Collectors.toList());

        return new PedidoResponseDTO(
                p.getId(), p.getDataSolicitacao(), p.getValorTotal(),
                p.getStatusPedido(), converterMercado, converterItens
        );
    }
}