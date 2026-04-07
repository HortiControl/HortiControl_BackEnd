package sptech.horticontrol.dtos.response;

import sptech.horticontrol.enums.TipoEmbalagem;

import java.math.BigDecimal;

public class ProdutoResponseDTO {

    private Long id;
    private String nome;
    private BigDecimal preco;
    private TipoEmbalagem tipoEmbalagem;

    public ProdutoResponseDTO() {
    }

    public ProdutoResponseDTO(Long id, String nome, BigDecimal preco, TipoEmbalagem tipoEmbalagem) {
        this.id = id;
        this.nome = nome;
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
