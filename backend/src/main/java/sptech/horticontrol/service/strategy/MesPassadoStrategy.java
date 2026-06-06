package sptech.horticontrol.service.strategy;

import sptech.horticontrol.dtos.resultados.ChartDataDTO;
import sptech.horticontrol.dtos.resultados.IntervaloDatas;
import sptech.horticontrol.entity.Pedido;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MesPassadoStrategy implements PeriodoStrategy {

    @Override
    public IntervaloDatas calcularIntervalo() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioMesPassado = hoje.minusMonths(1).withDayOfMonth(1);
        LocalDate fimMesPassado = inicioMesPassado.withDayOfMonth(inicioMesPassado.lengthOfMonth());
        return new IntervaloDatas(inicioMesPassado, fimMesPassado);
    }

    @Override
    public List<ChartDataDTO> gerarEvolucao(List<Pedido> pedidos) {
        Map<String, BigDecimal> faturamento = EsteMesStrategy.inicializarSemanasMes();

        for (Pedido p : pedidos) {
            int diaDoMes = p.getDataSolicitacao().getDayOfMonth();
            int numSemana = Math.min(((diaDoMes - 1) / 7) + 1, 5);
            faturamento.merge("Semana " + numSemana, p.getValorTotal(), BigDecimal::add);
        }

        return faturamento.entrySet().stream()
                .map(e -> new ChartDataDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}
