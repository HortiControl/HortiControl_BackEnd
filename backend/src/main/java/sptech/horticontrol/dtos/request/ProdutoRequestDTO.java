package sptech.horticontrol.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import sptech.horticontrol.enums.TipoEmbalagem;

import java.math.BigDecimal;

@Schema(description = "Dados para criação/atualização de produto")
public class ProdutoRequestDTO {

    @Schema(
            description = "Nome do produto",
            example = "Alface Crespa",
            required = true
    )
    private String nome;

    @Schema(
            description = "Preço do produto",
            example = "9.99",
            required = true
    )
    private BigDecimal preco;

    @Schema(
            description = "Tipo de embalagem do produto",
            example = "BANDEJA",
            required = true
    )
    private TipoEmbalagem tipoEmbalagem;

    public ProdutoRequestDTO() {
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
