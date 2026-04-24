package sptech.horticontrol.dtos.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ItemPedidoRequestDTO {

    private Long produtoId;
    @NotNull(message = "quantidade é obrigatória")
    @Min(value = 1, message = "quantidade deve ser no mínimo 1")
    private Integer quantidade;

    public ItemPedidoRequestDTO() {
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}
