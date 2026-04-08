package sptech.horticontrol.dtos.request;

public class ItemPedidoRequestDTO {

    private Long produtoId;
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
