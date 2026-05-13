package sptech.horticontrol.entity;

import jakarta.persistence.*;
import sptech.horticontrol.enums.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPedido")
    private Long id;

    @Column(name = "dataSolicitacao", nullable = false)
    private LocalDate dataSolicitacao;

    @Column(name = "valorTotal", precision = 10, scale = 2, nullable = false)
    private BigDecimal valorTotal;

    @Column(name = "statusPedido", length = 15, nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusPedido statusPedido;

    @Column(name = "valorPago", precision = 10, scale = 2, nullable = false)
    private BigDecimal valorPago = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "fkMercado", nullable = false)
    private Mercado mercado;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    public Pedido() {
    }

    public Pedido(Long id, LocalDate dataSolicitacao, BigDecimal valorTotal, StatusPedido statusPedido, BigDecimal valorPago, Mercado mercado, List<ItemPedido> itens) {
        this.id = id;
        this.dataSolicitacao = dataSolicitacao;
        this.valorTotal = valorTotal;
        this.statusPedido = statusPedido;
        this.valorPago = valorPago;
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

    public Mercado getMercado() {
        return mercado;
    }

    public void setMercado(Mercado mercado) {
        this.mercado = mercado;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }
}
