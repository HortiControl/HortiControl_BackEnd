package sptech.horticontrol.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import sptech.horticontrol.enums.TipoMercado;

public class MercadoRequestDTO {

    @Schema(
            description = "Nome do mercado",
            example = "Mercado Central"
    )
    private String nome;
    @Schema(
            description = "Tipo do mercado",
            example = "SUPERMERCADO"
    )
    private TipoMercado tipoMercado;
    @Schema(
            description = "Observações adicionais",
            example = "Atendimento 24 horas"
    )
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