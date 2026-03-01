package sptech.horticontrol.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sptech.horticontrol.entity.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
    
}
