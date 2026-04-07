package sptech.horticontrol.dtos.request;

import sptech.horticontrol.enums.TipoMercado;

public class MercadoRequestDTO {

    private String nome;
    private TipoMercado tipoMercado;
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