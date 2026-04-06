package sptech.horticontrol.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "produto")
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idProduto")
    private Long idProduto;

    @Column(name = "nome", length = 45, nullable = false)
    private String nome;

    @Column(name = "preco", precision = 10, scale = 2, nullable = false)
    private BigDecimal preco;

    @Column(name = "embalagem", length = 45, nullable = false)
    private String embalagem;

    public Produto() {
    }

    public Produto(String nome, Long idProduto, BigDecimal preco, String embalagem) {
        this.nome = nome;
        this.idProduto = idProduto;
        this.preco = preco;
        this.embalagem = embalagem;
    }

    public Long getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(Long idProduto) {
        this.idProduto = idProduto;
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

    public String getEmbalagem() {
        return embalagem;
    }

    public void setEmbalagem(String embalagem) {
        this.embalagem = embalagem;
    }

}