package sptech.horticontrol.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

    @NotBlank(message = "cep é obrigatório")
    @Pattern(
            regexp = "\\d{5}-?\\d{3}",
            message = "CEP inválido"
    )
    private String cep;

    @NotBlank(message = "número é obrigatório")
    @Size(max = 10)
    private String numero;

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