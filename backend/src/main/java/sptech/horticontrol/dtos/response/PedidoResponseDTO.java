package sptech.horticontrol.dtos.response;

import sptech.horticontrol.enums.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PedidoResponseDTO {

    private Long id;
    private LocalDate dataSolicitacao;
    private BigDecimal valorTotal;
    private StatusPedido statusPedido;
    private MercadoResponseDTO mercado;
    private List<ItemPedidoResponseDTO> itens;

    public PedidoResponseDTO() {
    }

    public PedidoResponseDTO(Long id, LocalDate dataSolicitacao, BigDecimal valorTotal, StatusPedido statusPedido, MercadoResponseDTO mercado, List<ItemPedidoResponseDTO> itens) {
        this.id = id;
        this.dataSolicitacao = dataSolicitacao;
        this.valorTotal = valorTotal;
        this.statusPedido = statusPedido;
        this.mercado = mercado;
        this.itens = itens;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(LocalDate dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public StatusPedido getStatusPedido() {
        return statusPedido;
    }

    public void setStatusPedido(StatusPedido statusPedido) {
        this.statusPedido = statusPedido;
    }

    public MercadoResponseDTO getMercado() {
        return mercado;
    }

    public void setMercado(MercadoResponseDTO mercado) {
        this.mercado = mercado;
    }

    public List<ItemPedidoResponseDTO> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedidoResponseDTO> itens) {
        this.itens = itens;
    }
}
