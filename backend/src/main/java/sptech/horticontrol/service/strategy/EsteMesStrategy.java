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

public class EsteMesStrategy implements PeriodoStrategy {

    @Override
    public IntervaloDatas calcularIntervalo() {
        LocalDate hoje = LocalDate.now();
        return new IntervaloDatas(hoje.withDayOfMonth(1), hoje);
    }

    @Override
    public List<ChartDataDTO> gerarEvolucao(List<Pedido> pedidos) {
        Map<String, BigDecimal> faturamento = inicializarSemanasMes();

        for (Pedido p : pedidos) {
            int diaDoMes = p.getDataSolicitacao().getDayOfMonth();
            int numSemana = Math.min(((diaDoMes - 1) / 7) + 1, 5);
            faturamento.merge("Semana " + numSemana, p.getValorTotal(), BigDecimal::add);
        }

        return faturamento.entrySet().stream()
                .map(e -> new ChartDataDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    static Map<String, BigDecimal> inicializarSemanasMes() {
        Map<String, BigDecimal> mapa = new LinkedHashMap<>();
        mapa.put("Semana 1", BigDecimal.ZERO);
        mapa.put("Semana 2", BigDecimal.ZERO);
        mapa.put("Semana 3", BigDecimal.ZERO);
        mapa.put("Semana 4", BigDecimal.ZERO);
        mapa.put("Semana 5", BigDecimal.ZERO);
        return mapa;
    }
}
