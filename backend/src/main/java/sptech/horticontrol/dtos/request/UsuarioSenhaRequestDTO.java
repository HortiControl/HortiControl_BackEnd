package sptech.horticontrol.dtos.request;

public class UsuarioSenhaRequestDTO {

    private String senhaAtual;
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
