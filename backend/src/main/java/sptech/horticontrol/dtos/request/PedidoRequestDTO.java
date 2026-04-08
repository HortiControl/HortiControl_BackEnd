package sptech.horticontrol.dtos.request;

import sptech.horticontrol.enums.StatusPedido;

import java.time.LocalDate;
import java.util.List;

public class PedidoRequestDTO {

    private LocalDate dataSolicitacao;
    private StatusPedido statusPedido;
    private Long mercadoId;
    private List<ItemPedidoRequestDTO> itens;

    public PedidoRequestDTO() {
    }

    public LocalDate getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(LocalDate dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public StatusPedido getStatusPedido() {
        return statusPedido;
    }

    public void setStatusPedido(StatusPedido statusPedido) {
        this.statusPedido = statusPedido;
    }

    public Long getMercadoId() {
        return mercadoId;
    }

    public void setMercadoId(Long mercadoId) {
        this.mercadoId = mercadoId;
    }

    public List<ItemPedidoRequestDTO> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedidoRequestDTO> itens) {
        this.itens = itens;
    }
}
