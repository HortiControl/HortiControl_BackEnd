package sptech.horticontrol.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sptech.horticontrol.entity.Mercado;
import sptech.horticontrol.enums.TipoMercado;

import java.util.List;

@Repository
public interface MercadoRepository extends JpaRepository<Mercado, Long> {
    List<Mercado> findByTipoMercado(TipoMercado tipo);
}
