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

public class EstaSemanaStrategy implements PeriodoStrategy {

    @Override
    public IntervaloDatas calcularIntervalo() {
        LocalDate hoje = LocalDate.now();
        return new IntervaloDatas(hoje.minusDays(hoje.getDayOfWeek().getValue() - 1), hoje);
    }

    @Override
    public List<ChartDataDTO> gerarEvolucao(List<Pedido> pedidos) {
        Map<String, BigDecimal> faturamento = inicializarDiasSemana();

        for (Pedido p : pedidos) {
            String dia = PeriodoHelper.traduzirDiaSemana(p.getDataSolicitacao().getDayOfWeek());
            faturamento.merge(dia, p.getValorTotal(), BigDecimal::add);
        }

        return faturamento.entrySet().stream()
                .map(e -> new ChartDataDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    static Map<String, BigDecimal> inicializarDiasSemana() {
        Map<String, BigDecimal> mapa = new LinkedHashMap<>();
        mapa.put("Seg", BigDecimal.ZERO);
        mapa.put("Ter", BigDecimal.ZERO);
        mapa.put("Qua", BigDecimal.ZERO);
        mapa.put("Qui", BigDecimal.ZERO);
        mapa.put("Sex", BigDecimal.ZERO);
        mapa.put("Sáb", BigDecimal.ZERO);
        mapa.put("Dom", BigDecimal.ZERO);
        return mapa;
    }
}
