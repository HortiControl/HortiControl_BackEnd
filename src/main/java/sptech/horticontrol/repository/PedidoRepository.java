package sptech.horticontrol.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sptech.horticontrol.entity.Pedido;
import sptech.horticontrol.enums.StatusPedido;

import java.time.LocalDate;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    /*
     * Todas as consultas abaixo usam JOIN FETCH para carregar mercado,
     * itens e o produto de cada item em uma única consulta.
     *
     * Sem isso, cada Pedido retornado dispararia consultas adicionais ao
     * ser convertido para DTO (acesso a getMercado(), getItens() e
     * item.getProduto()), gerando o clássico problema de N+1 queries.
     *
     * "mercado" usa JOIN FETCH (inner) porque a coluna fkMercado é
     * obrigatória (NOT NULL). "itens"/"produto" usam LEFT JOIN FETCH por
     * segurança, para não excluir da listagem um pedido que eventualmente
     * não possua itens.
     */

    @Query("""
            SELECT DISTINCT p FROM Pedido p
            JOIN FETCH p.mercado m
            LEFT JOIN FETCH p.itens i
            LEFT JOIN FETCH i.produto
            WHERE m.id = :mercadoId AND p.statusPedido = :status
            """)
    List<Pedido> findByMercadoIdAndStatusPedido(
            @Param("mercadoId") Long mercadoId,
            @Param("status") StatusPedido status
    );

    @Query("""
            SELECT DISTINCT p FROM Pedido p
            JOIN FETCH p.mercado m
            LEFT JOIN FETCH p.itens i
            LEFT JOIN FETCH i.produto
            WHERE m.id = :mercadoId AND p.statusPedido IN :statuses
            """)
    List<Pedido> findByMercadoIdAndStatusPedidoIn(
            @Param("mercadoId") Long mercadoId,
            @Param("statuses") List<StatusPedido> statuses
    );

    @Query("""
            SELECT DISTINCT p FROM Pedido p
            JOIN FETCH p.mercado
            LEFT JOIN FETCH p.itens i
            LEFT JOIN FETCH i.produto
            WHERE p.statusPedido = :status
            """)
    List<Pedido> findByStatusPedido(@Param("status") StatusPedido status);

    @Query("""
            SELECT DISTINCT p FROM Pedido p
            JOIN FETCH p.mercado
            LEFT JOIN FETCH p.itens i
            LEFT JOIN FETCH i.produto
            WHERE p.statusPedido IN :statuses
            """)
    List<Pedido> findByStatusPedidoIn(@Param("statuses") List<StatusPedido> statuses);

    @Query("""
            SELECT DISTINCT p FROM Pedido p
            JOIN FETCH p.mercado
            LEFT JOIN FETCH p.itens i
            LEFT JOIN FETCH i.produto
            WHERE p.dataSolicitacao BETWEEN :inicio AND :fim
            """)
    List<Pedido> findByDataSolicitacaoBetween(
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim
    );
}

