package sptech.horticontrol.dtos.response;

import sptech.horticontrol.enums.TipoMercado;

public class MercadoResponseDTO {
    private Long id;
    private String nome;
    private TipoMercado tipo;
    private String observacao;

    public MercadoResponseDTO() {
    }

    public MercadoResponseDTO(Long id, String nome, TipoMercado tipo, String observacao) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
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

    public TipoMercado getTipo() {
        return tipo;
    }

    public void setTipo(TipoMercado tipo) {
        this.tipo = tipo;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}