package sptech.horticontrol.dtos.response;

import sptech.horticontrol.enums.TipoMercado;

public class MercadoResponseDTO {

    private Long id;
    private String nome;
    private TipoMercado tipoMercado;

    public MercadoResponseDTO() {
    }

    public MercadoResponseDTO(Long id, String nome, TipoMercado tipoMercado) {
        this.id = id;
        this.nome = nome;
        this.tipoMercado = tipoMercado;
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
}
