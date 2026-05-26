package sptech.horticontrol.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UsuarioPerfilRequestDTO {

    @NotBlank(message = "nome é obrigatório")
    @Size(max = 100, message = "nome deve ter no máximo 100 caracteres")
    @Schema(example = "Maria Silva", description = "Nome do usuário")
    private String nome;

    @NotBlank(message = "email é obrigatório")
    @Email(message = "email inválido")
    @Schema(example = "maria@email.com", description = "Email do usuário")
    private String email;


    @Pattern(regexp = "^$|\\d{10,11}", message = "telefone deve ter 10 ou 11 dígitos")
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
