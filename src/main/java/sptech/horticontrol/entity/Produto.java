package sptech.horticontrol.entity;

import jakarta.persistence.*;
import sptech.horticontrol.enums.TipoEmbalagem;
import sptech.horticontrol.enums.TipoProduto;

import java.math.BigDecimal;

@Entity
@Table(name = "produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idProduto")
    private Long id;

    @Column(name = "nome", length = 45, nullable = false)
    private String nome;

    @Column(name = "preco", precision = 10, scale = 2, nullable = false)
    private BigDecimal preco;

    @Column(name = "tipoEmbalagem", length = 45, nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoEmbalagem tipoEmbalagem;

    @Column(name = "tipoProduto", length = 45, nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoProduto tipoProduto;

    public Produto() {
    }

    public Produto(Long id, String nome, BigDecimal preco, TipoEmbalagem tipoEmbalagem, TipoProduto tipoProduto) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.tipoEmbalagem = tipoEmbalagem;
        this.tipoProduto = tipoProduto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public TipoEmbalagem getTipoEmbalagem() {
        return tipoEmbalagem;
    }

    public void setTipoEmbalagem(TipoEmbalagem tipoEmbalagem) {
        this.tipoEmbalagem = tipoEmbalagem;
    }

    public TipoProduto getTipoProduto() {
        return tipoProduto;
    }

    public void setTipoProduto(TipoProduto tipoProduto) {
        this.tipoProduto = tipoProduto;
    }
}