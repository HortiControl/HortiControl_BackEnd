package sptech.horticontrol.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;

public class UsuarioSenhaRequestDTO {

    @Schema(
            description = "Senha atual do usuário",
            example = "Senha@123"
    )
    private String senhaAtual;

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
