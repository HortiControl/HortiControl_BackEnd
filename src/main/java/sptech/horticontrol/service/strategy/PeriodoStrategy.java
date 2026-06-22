package sptech.horticontrol.service.strategy;

import sptech.horticontrol.dtos.resultados.ChartDataDTO;
import sptech.horticontrol.dtos.resultados.IntervaloDatas;
import sptech.horticontrol.entity.Pedido;

import java.util.List;

public interface PeriodoStrategy {

    IntervaloDatas calcularIntervalo();

    List<ChartDataDTO> gerarEvolucao(List<Pedido> pedidos);
}
