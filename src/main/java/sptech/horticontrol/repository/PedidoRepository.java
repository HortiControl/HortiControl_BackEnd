package sptech.horticontrol.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /*
     * 1) Busca-se apenas os IDs da página desejada, com uma query simples
     *    (sem fetch de coleção), o banco aplique
     *    LIMIT/OFFSET e que o Spring Data gere a contagem
     *    total (COUNT) para montar o Page<Long>.
     *
     * 2) Com a lista de IDs da página, busca-se os pedidos completos (com
     *    JOIN FETCH de mercado/itens/produto) via WHERE id IN :ids. 
     */

    @Query("""
            SELECT p.id FROM Pedido p
            WHERE p.mercado.id = :mercadoId AND p.statusPedido = :status
            """)
    Page<Long> findIdsByMercadoIdAndStatusPedido(
            @Param("mercadoId") Long mercadoId,
            @Param("status") StatusPedido status,
            Pageable pageable
    );

    @Query("""
            SELECT p.id FROM Pedido p
            WHERE p.statusPedido = :status
            """)
    Page<Long> findIdsByStatusPedido(
            @Param("status") StatusPedido status,
            Pageable pageable
    );

    @Query("""
            SELECT p.id FROM Pedido p
            WHERE p.mercado.id = :mercadoId AND p.statusPedido IN :statuses
            """)
    Page<Long> findIdsByMercadoIdAndStatusPedidoIn(
            @Param("mercadoId") Long mercadoId,
            @Param("statuses") List<StatusPedido> statuses,
            Pageable pageable
    );

    @Query("""
            SELECT p.id FROM Pedido p
            WHERE p.statusPedido IN :statuses
            """)
    Page<Long> findIdsByStatusPedidoIn(
            @Param("statuses") List<StatusPedido> statuses,
            Pageable pageable
    );

    @Query("""
            SELECT DISTINCT p FROM Pedido p
            JOIN FETCH p.mercado
            LEFT JOIN FETCH p.itens i
            LEFT JOIN FETCH i.produto
            WHERE p.id IN :ids
            """)
    List<Pedido> findByIdInComFetch(@Param("ids") List<Long> ids);
}
