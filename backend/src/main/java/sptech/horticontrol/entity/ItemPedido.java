package sptech.horticontrol.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "item_pedido")
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "preco_unitario", precision = 10, scale = 2, nullable = false)
    private BigDecimal precoUnitario;

    @Column(name = "sub_total", precision = 10, scale = 2, nullable = false)
    private BigDecimal subTotal;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;

    public ItemPedido() {
    }

    public ItemPedido(Long id, Integer quantidade, BigDecimal precoUnitario, BigDecimal subTotal, Pedido pedido, Produto produto) {
        this.id = id;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.subTotal = subTotal;
        this.pedido = pedido;
        this.produto = produto;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
        this.calcularSubTotal();
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
        this.calcularSubTotal();
    }

    private void calcularSubTotal() {
        if (this.quantidade != null && this.precoUnitario != null) {
            this.subTotal = this.precoUnitario.multiply(new BigDecimal(this.quantidade));
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

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
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