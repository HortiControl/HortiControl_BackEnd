package sptech.horticontrol.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sptech.horticontrol.entity.Produto;
import sptech.horticontrol.enums.TipoProduto;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByNomeContainingIgnoreCase(String nome);

    List<Produto> findByTipoProduto(TipoProduto tipo);
}
