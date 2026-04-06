package sptech.horticontrol.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sptech.horticontrol.entity.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
