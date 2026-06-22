package sptech.horticontrol.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUsuario", nullable = false)
    private Long idUsuario;

    @Column(name = "nome", nullable = false, length = 255)
    private String nome;

    @Column(name = "email", nullable = false, length = 255, unique = true)
    private String email;

    @Column(name = "telefone", nullable = true, length = 11)
    private String telefone;

    @Column(name = "senha", nullable = false, length = 255)
    private String senha;

    public Usuario() {
    }

    public Usuario(Long idUsuario, String email, String senha, String nome, String telefone) {
        this.idUsuario = idUsuario;
        this.email = email;
        this.senha = senha;
        this.nome = nome;
        this.telefone = telefone;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setId(Long idUsuario) {
        this.idUsuario = idUsuario;
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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
