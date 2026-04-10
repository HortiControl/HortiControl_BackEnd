package sptech.horticontrol.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;

public class UsuarioPerfilRequestDTO {

    @Schema(example = "Maria Silva", description = "Nome do usuário")
    private String nome;

    @Schema(example = "maria@email.com", description = "Email do usuário")
    private String email;

    @Schema(example = "11999998888", description = "Telefone do usuário")
    private String telefone;

    public UsuarioPerfilRequestDTO() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}
