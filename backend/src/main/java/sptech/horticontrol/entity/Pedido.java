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
    @Column(name = "id")
    private Long id;

    @Column(name = "data_solicitacao", length = 10, nullable = false)
    private LocalDate dataSolicitacao;

    @Column(name = "valor_total", precision = 10, scale = 2, nullable = false)
    private BigDecimal valorTotal;

    @Column(name = "status_pedido", length = 15, nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusPedido statusPedido;

    @ManyToOne
    @JoinColumn(name = "mercado_id")
    private Mercado mercado;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    public Pedido() {
    }

    public Pedido(Long id, LocalDate dataSolicitacao, BigDecimal valorTotal, StatusPedido statusPedido, Mercado mercado) {
        this.id = id;
        this.dataSolicitacao = dataSolicitacao;
        this.valorTotal = valorTotal;
        this.statusPedido = statusPedido;
        this.mercado = mercado;
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

}
