package sptech.horticontrol.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

public class UsuarioResponseDTO {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "Maria Silva")
    private String nome;

    @Schema(example = "maria@email.com")
    private String email;

    @Schema(example = "11999998888")
    private String telefone;

    public UsuarioResponseDTO() {
    }

    public UsuarioResponseDTO(Long id, String nome, String email, String telefone) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
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
