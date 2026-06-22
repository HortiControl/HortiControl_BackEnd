package sptech.horticontrol.service.strategy;

import sptech.horticontrol.dtos.resultados.ChartDataDTO;
import sptech.horticontrol.dtos.resultados.IntervaloDatas;
import sptech.horticontrol.entity.Pedido;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DefaultStrategy implements PeriodoStrategy {

    @Override
    public IntervaloDatas calcularIntervalo() {
        LocalDate hoje = LocalDate.now();
        return new IntervaloDatas(hoje.minusDays(30), hoje);
    }

    @Override
    public List<ChartDataDTO> gerarEvolucao(List<Pedido> pedidos) {
        Map<String, BigDecimal> faturamento = new LinkedHashMap<>();

        for (Pedido p : pedidos) {
            String dataLabel = p.getDataSolicitacao().toString();
            faturamento.merge(dataLabel, p.getValorTotal(), BigDecimal::add);
        }

        return faturamento.entrySet().stream()
                .map(e -> new ChartDataDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}
