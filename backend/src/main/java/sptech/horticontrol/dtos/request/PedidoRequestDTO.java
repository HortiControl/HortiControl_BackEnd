package sptech.horticontrol.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import sptech.horticontrol.enums.StatusPedido;

import java.time.LocalDate;
import java.util.List;

public class PedidoRequestDTO {

    @Schema(
            description = "Data da solicitação do pedido",
            example = "2026-04-10"
    )
    private LocalDate dataSolicitacao;

    @Schema(
            description = "Status inicial do pedido",
            example = "EM_ANDAMENTO"
    )
    private StatusPedido statusPedido;

    @Schema(
            description = "ID do mercado associado ao pedido",
            example = "1"
    )
    private Long mercadoId;

    @Schema(
            description = "Lista de itens do pedido"
    )
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
