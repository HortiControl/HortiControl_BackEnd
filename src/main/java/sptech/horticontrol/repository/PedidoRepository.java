package sptech.horticontrol.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sptech.horticontrol.entity.Pedido;
import sptech.horticontrol.enums.StatusPedido;

import java.time.LocalDate;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByMercadoIdAndStatusPedido(Long mercadoId, StatusPedido status);
    List<Pedido> findByMercadoIdAndStatusPedidoIn(Long mercadoId, List<StatusPedido> statuses);

    List<Pedido> findByStatusPedido(StatusPedido status);
    List<Pedido> findByStatusPedidoIn(List<StatusPedido> statuses);

    List<Pedido> findByDataSolicitacaoBetween(LocalDate inicio, LocalDate fim);
}
