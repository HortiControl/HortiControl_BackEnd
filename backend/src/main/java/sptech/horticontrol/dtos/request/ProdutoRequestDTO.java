package sptech.horticontrol.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import sptech.horticontrol.enums.TipoEmbalagem;
import sptech.horticontrol.enums.TipoProduto;

import java.math.BigDecimal;

@Schema(description = "Dados para criação/atualização de produto")
public class ProdutoRequestDTO {

    @NotBlank(message = "nome é obrigatório")
    @Size(max = 100, message = "nome deve ter no máximo 100 caracteres")
    @Schema(
            description = "Nome do produto",
            example = "Alface Crespa",
            required = true
    )
    private String nome;

    @NotNull(message = "preco é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "preco deve ser maior que zero")
    @Schema(
            description = "Preço do produto",
            example = "9.99",
            required = true
    )
    private BigDecimal preco;

    @NotNull(message = "tipoEmbalagem é obrigatório")
    @Schema(
            description = "Tipo de embalagem do produto",
            example = "BANDEJA",
            required = true
    )
    private TipoEmbalagem tipoEmbalagem;

    @NotNull(message = "tipoProduto é obrigatório")
    @Schema(
            description = "Tipo de produto",
            example = "NAO_LAVADO",
            required = true
    )
    private TipoProduto tipoProduto;

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

    public TipoProduto getTipoProduto() {
        return tipoProduto;
    }

    public void setTipoProduto(TipoProduto tipoProduto) {
        this.tipoProduto = tipoProduto;
    }
}
