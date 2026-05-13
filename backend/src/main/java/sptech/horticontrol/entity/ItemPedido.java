package sptech.horticontrol.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "itensPedido")
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idItensPedido")
    private Long id;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "precoUnitario", precision = 10, scale = 2, nullable = false)
    private BigDecimal precoUnitario;

    @Column(name = "subTotal", precision = 10, scale = 2, nullable = false)
    private BigDecimal subTotal;

    @ManyToOne
    @JoinColumn(name = "fkPedido", nullable = false)
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "fkProduto", nullable = false)
    private Produto produto;

    public ItemPedido() {
    }

    public ItemPedido(
            Long id,
            Integer quantidade,
            BigDecimal precoUnitario,
            Pedido pedido,
            Produto produto
    ) {

        this.id = id;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.pedido = pedido;
        this.produto = produto;

        calcularSubTotal();
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
        calcularSubTotal();
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
        calcularSubTotal();
    }

    private void calcularSubTotal() {

        if (this.quantidade != null && this.precoUnitario != null) {

            this.subTotal =
                    this.precoUnitario.multiply(
                            BigDecimal.valueOf(this.quantidade)
                    );

        } else {

            this.subTotal = BigDecimal.ZERO;

        }

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

}