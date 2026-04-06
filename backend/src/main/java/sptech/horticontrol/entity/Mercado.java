package sptech.horticontrol.entity;

import jakarta.persistence.*;
import sptech.horticontrol.enumerators.TipoMercado;

@Entity
@Table(name = "mercado")
public class Mercado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nome", length = 50, nullable = false)
    private String nome;

    @Column(name = "tipo_embalagem", length = 20, nullable = false)
    private TipoMercado tipoMercado;

    @Column(name = "observacao", length = 255, nullable = false)
    private String observacao;

    public Mercado() {
    }

    public Mercado(Long id, String nome, TipoMercado tipoMercado, String observacao) {
        this.id = id;
        this.nome = nome;
        this.tipoMercado = tipoMercado;
        this.observacao = observacao;
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

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
