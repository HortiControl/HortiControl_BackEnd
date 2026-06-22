package sptech.horticontrol.entity;

import jakarta.persistence.*;
import sptech.horticontrol.enums.TipoMercado;

@Entity
@Table(name = "mercado")
public class Mercado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idMercado")
    private Long id;

    @Column(name = "nome", length = 50, nullable = false)
    private String nome;

    @Column(name = "tipoMercado", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoMercado tipoMercado;

    @Column(name = "cep", length = 9, nullable = false)
    private String cep;

    @Column(name = "numero", length = 10, nullable = false)
    private String numero;

    public Mercado() {
    }

    public Mercado(Long id, String nome, TipoMercado tipoMercado, String cep, String numero) {
        this.id = id;
        this.nome = nome;
        this.tipoMercado = tipoMercado;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mercado other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
