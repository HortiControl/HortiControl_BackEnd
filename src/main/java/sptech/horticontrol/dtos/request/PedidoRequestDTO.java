package sptech.horticontrol.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import sptech.horticontrol.enums.StatusPedido;

import java.time.LocalDate;
import java.util.List;

public class PedidoRequestDTO {

    @Schema(description = "ID do mercado associado ao pedido", example = "1")
    private Long mercadoId;

    @NotEmpty(message = "pedido deve ter ao menos um item")
    @Valid
    @Schema(description = "Lista de itens do pedido")
    private List<ItemPedidoRequestDTO> itens;

    public PedidoRequestDTO() {
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
