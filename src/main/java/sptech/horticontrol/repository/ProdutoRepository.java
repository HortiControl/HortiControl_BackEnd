package sptech.horticontrol.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sptech.horticontrol.entity.Produto;
import sptech.horticontrol.enums.TipoProduto;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByTipoProduto(TipoProduto tipo);

    Page<Produto> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    Page<Produto> findByTipoProduto(TipoProduto tipo, Pageable pageable);
}
