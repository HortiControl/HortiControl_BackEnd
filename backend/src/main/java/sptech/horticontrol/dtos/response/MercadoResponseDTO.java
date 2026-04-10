package sptech.horticontrol.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import sptech.horticontrol.enums.TipoMercado;

public class MercadoResponseDTO {

    @Schema(description = "ID do mercado", example = "1")
    private Long id;

    @Schema(description = "Nome do mercado", example = "Mercado Central")
    private String nome;

    @Schema(description = "Tipo do mercado", example = "SUPERMERCADO")
    private TipoMercado tipoMercado;

    @Schema(description = "Observações adicionais", example = "Atendimento 24 horas")
    private String observacao;

    public MercadoResponseDTO() {
    }

    public MercadoResponseDTO(Long id, String nome, TipoMercado tipo, String observacao) {
        this.id = id;
        this.nome = nome;
        this.tipoMercado = tipo;
        this.observacao = observacao;
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