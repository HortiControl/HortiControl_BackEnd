package sptech.horticontrol.dtos.response;

import sptech.horticontrol.enums.TipoProduto;

import java.math.BigDecimal;

public class ItemPedidoResponseDTO {

    private Long id;
    private String nomeProduto;
    private TipoProduto tipoProduto;
    private Integer quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal subTotal;

    public ItemPedidoResponseDTO() {
    }

    public ItemPedidoResponseDTO(Long id, String nomeProduto, TipoProduto tipoProduto, Integer quantidade, BigDecimal precoUnitario, BigDecimal subTotal) {
        this.id = id;
        this.nomeProduto = nomeProduto;
        this.tipoProduto = tipoProduto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.subTotal = subTotal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public TipoProduto getTipoProduto() {
        return tipoProduto;
    }

    public void setTipoProduto(TipoProduto tipoProduto) {
        this.tipoProduto = tipoProduto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }
}
