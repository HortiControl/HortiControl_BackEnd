package sptech.horticontrol.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UsuarioSenhaRequestDTO {

    @NotBlank(message = "senhaAtual é obrigatória")
    @Schema(
            description = "Senha atual do usuário",
            example = "Senha@123"
    )
    private String senhaAtual;

    @NotBlank(message = "novaSenha é obrigatória")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{6,}$",
            message = "novaSenha deve ter no mínimo 6 caracteres, uma letra maiúscula e um número"
    )
    @Schema(
            description = "Nova senha",
            example = "NovaSenha@123"
    )
    private String novaSenha;

    public UsuarioSenhaRequestDTO() {
    }

    public String getSenhaAtual() {
        return senhaAtual;
    }

    public void setSenhaAtual(String senhaAtual) {
        this.senhaAtual = senhaAtual;
    }

    public String getNovaSenha() {
        return novaSenha;
    }

    public void setNovaSenha(String novaSenha) {
        this.novaSenha = novaSenha;
    }
}
