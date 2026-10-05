package sptech.horticontrol.service.resultado;

import org.springframework.stereotype.Component;
import sptech.horticontrol.dtos.resultados.HistoricoEmbalagemDTO;
import sptech.horticontrol.entity.Pedido;
import sptech.horticontrol.enums.TipoEmbalagem;
import sptech.horticontrol.service.strategy.PeriodoHelper;

import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/*
 * Responsável apenas por montar o histórico anual de consumo por embalagem,
 * mês a mês.
 *
 * Extraído de ResultadoService para isolar essa regra de negócio e permitir
 * testá-la sem depender do restante do relatório. Reaproveita o
 * EmbalagemCalculator para a contagem de cada mês.
 */
@Component
public class HistoricoEmbalagemCalculator {

    private final EmbalagemCalculator embalagemCalculator;

    public HistoricoEmbalagemCalculator(EmbalagemCalculator embalagemCalculator) {
        this.embalagemCalculator = embalagemCalculator;
    }

    public List<HistoricoEmbalagemDTO> gerar(List<Pedido> pedidosDoAno) {

        var pedidosPorMes = pedidosDoAno.stream()
                .collect(Collectors.groupingBy(p -> p.getDataSolicitacao().getMonth()));

        return Arrays.stream(Month.values())
                .map(mes -> {
                    List<Pedido> pedidosMes = pedidosPorMes.getOrDefault(mes, List.of());

                    return new HistoricoEmbalagemDTO(
                            PeriodoHelper.traduzirMes(mes),
                            embalagemCalculator.somarPorEmbalagem(pedidosMes, TipoEmbalagem.BANDEJA),
                            embalagemCalculator.somarPorEmbalagem(pedidosMes, TipoEmbalagem.POTE),
                            embalagemCalculator.somarPorEmbalagem(pedidosMes, TipoEmbalagem.SACO)
                    );
                }).toList();
    }
}
