package sptech.horticontrol.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sptech.horticontrol.entity.Mercado;
import sptech.horticontrol.enums.TipoMercado;

@Repository
public interface MercadoRepository extends JpaRepository<Mercado, Long> {
    Page<Mercado> findByTipoMercado(TipoMercado tipo, Pageable pageable);
}
