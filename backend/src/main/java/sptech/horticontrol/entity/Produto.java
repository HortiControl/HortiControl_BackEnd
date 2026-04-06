package sptech.horticontrol.entity;

import jakarta.persistence.*;
import sptech.horticontrol.enumerators.TipoEmbalagem;

import java.math.BigDecimal;

@Entity
@Table(name = "produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nome", length = 45, nullable = false)
    private String nome;

    @Column(name = "preco", precision = 10, scale = 2, nullable = false)
    private BigDecimal preco;

    @Column(name = "embalagem", length = 45, nullable = false)
    private TipoEmbalagem tipoEmbalagem;

    public Produto() {
    }

    public Produto(String nome, Long id, BigDecimal preco, TipoEmbalagem tipoEmbalagem) {
        this.nome = nome;
        this.id = id;
        this.preco = preco;
        this.tipoEmbalagem = tipoEmbalagem;
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
}