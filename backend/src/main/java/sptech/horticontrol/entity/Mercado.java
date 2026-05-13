package sptech.horticontrol.entity;

import jakarta.persistence.*;
import sptech.horticontrol.enums.TipoMercado;

@Entity
@Table(name = "mercado")
public class Mercado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nome", length = 50, nullable = false)
    private String nome;

    @Column(name = "tipoMercado", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoMercado tipoMercado;

    public Mercado() {
    }

    public Mercado(Long id, String nome, TipoMercado tipoMercado) {
        this.id = id;
        this.nome = nome;
        this.tipoMercado = tipoMercado;
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

}
