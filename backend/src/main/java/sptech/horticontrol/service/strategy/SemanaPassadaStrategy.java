package sptech.horticontrol.service.strategy;

import sptech.horticontrol.dtos.resultados.ChartDataDTO;
import sptech.horticontrol.dtos.resultados.IntervaloDatas;
import sptech.horticontrol.entity.Pedido;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SemanaPassadaStrategy implements PeriodoStrategy {

    @Override
    public IntervaloDatas calcularIntervalo() {
        LocalDate hoje = LocalDate.now();
        LocalDate segundaPassada = hoje.minusWeeks(1).minusDays(hoje.getDayOfWeek().getValue() - 1);
        return new IntervaloDatas(segundaPassada, segundaPassada.plusDays(6));
    }

    @Override
    public List<ChartDataDTO> gerarEvolucao(List<Pedido> pedidos) {
        Map<String, BigDecimal> faturamento = EstaSemanaStrategy.inicializarDiasSemana();

        for (Pedido p : pedidos) {
            String dia = PeriodoHelper.traduzirDiaSemana(p.getDataSolicitacao().getDayOfWeek());
            faturamento.merge(dia, p.getValorTotal(), BigDecimal::add);
        }

        return faturamento.entrySet().stream()
                .map(e -> new ChartDataDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}
