package sptech.horticontrol.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import sptech.horticontrol.enums.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PedidoResponseDTO {

    @Schema(description = "ID do pedido", example = "10")
    private Long id;

    @Schema(description = "Data da solicitação", example = "2026-04-10")
    private LocalDate dataSolicitacao;

    @Schema(description = "Valor total do pedido", example = "249.90")
    private BigDecimal valorTotal;

    @Schema(description = "Status atual do pedido", example = "EM_ANDAMENTO")
    private StatusPedido statusPedido;

    @Schema(description = "Mercado associado ao pedido")
    private MercadoResponseDTO mercado;

    @Schema(description = "Itens do pedido")
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
