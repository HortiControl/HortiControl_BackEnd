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

public class HojeStrategy implements PeriodoStrategy {

    @Override
    public IntervaloDatas calcularIntervalo() {
        LocalDate hoje = LocalDate.now();
        return new IntervaloDatas(hoje, hoje);
    }

    @Override
    public List<ChartDataDTO> gerarEvolucao(List<Pedido> pedidos) {
        LocalDate hoje = LocalDate.now();
        String label = hoje.getDayOfMonth() + "/" + PeriodoHelper.traduzirMes(hoje.getMonth());

        Map<String, BigDecimal> faturamento = new LinkedHashMap<>();
        faturamento.put(label, BigDecimal.ZERO);

        for (Pedido p : pedidos) {
            faturamento.merge(label, p.getValorTotal(), BigDecimal::add);
        }

        return faturamento.entrySet().stream()
                .map(e -> new ChartDataDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}
