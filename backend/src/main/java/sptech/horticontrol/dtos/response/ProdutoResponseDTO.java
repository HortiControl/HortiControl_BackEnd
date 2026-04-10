package sptech.horticontrol.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import sptech.horticontrol.enums.TipoEmbalagem;

import java.math.BigDecimal;

public class ProdutoResponseDTO {

    @Schema(description = "ID do produto", example = "1")
    private Long id;

    @Schema(description = "Nome do produto", example = "Alface Crespa")
    private String nome;

    @Schema(description = "Preço do produto", example = "9.99")
    private BigDecimal preco;

    @Schema(description = "Tipo de embalagem", example = "BANDEJA")
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
