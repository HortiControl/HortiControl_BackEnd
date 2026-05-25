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

    @Schema(description = "CEP do mercado", example = "01234-567")
    private String cep;

    @Schema(description = "Número do endereço do mercado", example = "13")
    private String numero;

    public MercadoResponseDTO() {
    }

    public MercadoResponseDTO(Long id, String nome, TipoMercado tipo, String cep, String numero) {
        this.id = id;
        this.nome = nome;
        this.tipoMercado = tipo;
        this.cep = cep;
        this.numero = numero;
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

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }
}