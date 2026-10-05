package sptech.horticontrol.service.resultado;

import org.springframework.stereotype.Component;
import sptech.horticontrol.dtos.resultados.RankingClienteDTO;
import sptech.horticontrol.entity.Pedido;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/*
 * Responsável apenas por calcular o ranking dos mercados (clientes) com
 * maior valor total pedido no período analisado.
 *
 * Extraído de ResultadoService para isolar essa regra de negócio e permitir
 * testá-la sem depender do restante do relatório.
 */
@Component
public class RankingClienteCalculator {

    private static final int LIMITE_RANKING = 4;

    public List<RankingClienteDTO> calcular(List<Pedido> pedidos) {
        return pedidos.stream()
                .collect(Collectors.groupingBy(Pedido::getMercado))
                .entrySet().stream()
                .map(e -> new RankingClienteDTO(
                        e.getKey().getNome(),
                        (long) e.getValue().size(),
                        e.getValue().stream()
                                .map(Pedido::getValorTotal)
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                ))
                .sorted(Comparator.comparing(RankingClienteDTO::valorTotal).reversed())
                .limit(LIMITE_RANKING)
                .toList();
    }
}
