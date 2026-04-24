package sptech.horticontrol.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import sptech.horticontrol.enums.TipoMercado;

public class MercadoRequestDTO {

    @NotBlank(message = "nome é obrigatório")
    @Size(max = 100, message = "nome deve ter no máximo 100 caracteres")
    @Schema(description = "Nome do mercado", example = "Mercado Central")
    private String nome;

    @NotNull(message = "tipoMercado é obrigatório")
    @Schema(description = "Tipo do mercado", example = "SUPERMERCADO")
    private TipoMercado tipoMercado;

    @Size(max = 255, message = "observacao deve ter no máximo 255 caracteres")
    @Schema(description = "Observações adicionais", example = "Atendimento 24 horas")
    private String observacao;

    public MercadoRequestDTO() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoMercado getTipoMercado() {
        return tipoMercado;
    }

    public void setTipoMercado(TipoMercado tipoMercado) {
        this.tipoMercado = tipoMercado;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}