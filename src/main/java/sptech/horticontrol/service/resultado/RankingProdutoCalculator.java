package sptech.horticontrol.service.resultado;

import org.springframework.stereotype.Component;
import sptech.horticontrol.dtos.resultados.RankingProdutoDTO;
import sptech.horticontrol.entity.ItemPedido;
import sptech.horticontrol.entity.Pedido;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/*
 * Responsável apenas por calcular o ranking dos produtos mais vendidos
 * (em quantidade) no período analisado.
 *
 * Extraído de ResultadoService para isolar essa regra de negócio e permitir
 * testá-la sem depender do restante do relatório.
 */
@Component
public class RankingProdutoCalculator {

    private static final int LIMITE_RANKING = 4;

    public List<RankingProdutoDTO> calcular(List<Pedido> pedidos) {
        return pedidos.stream()
                .flatMap(p -> p.getItens().stream())
                .collect(Collectors.groupingBy(ItemPedido::getProduto))
                .entrySet().stream()
                .map(e -> new RankingProdutoDTO(
                        e.getKey().getNome(),
                        e.getKey().getTipoProduto().toString(),
                        e.getValue().stream().mapToLong(ItemPedido::getQuantidade).sum()
                ))
                .sorted(Comparator.comparing(RankingProdutoDTO::quantidadeVendida).reversed())
                .limit(LIMITE_RANKING)
                .toList();
    }
}
