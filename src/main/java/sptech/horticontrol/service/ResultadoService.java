package sptech.horticontrol.service;

import org.springframework.stereotype.Service;
import sptech.horticontrol.dtos.resultados.*;
import sptech.horticontrol.entity.Pedido;
import sptech.horticontrol.repository.PedidoRepository;
import sptech.horticontrol.service.resultado.EmbalagemCalculator;
import sptech.horticontrol.service.resultado.HistoricoEmbalagemCalculator;
import sptech.horticontrol.service.resultado.RankingClienteCalculator;
import sptech.horticontrol.service.resultado.RankingProdutoCalculator;
import sptech.horticontrol.service.strategy.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/*
 * Orquestra a geração do relatório de resultados.
 *
 * O cálculo de cada métrica (embalagens, ranking de clientes, ranking de
 * produtos e histórico anual) foi extraído para colaboradores dedicados em
 * service.resultado. Esta classe apenas define o intervalo de datas (via
 * PeriodoStrategy), busca os pedidos no repositório e delega cada cálculo
 * ao colaborador correspondente.
 */
@Service
public class ResultadoService {

    private final PedidoRepository pedidoRepository;
    private final EmbalagemCalculator embalagemCalculator;
    private final RankingClienteCalculator rankingClienteCalculator;
    private final RankingProdutoCalculator rankingProdutoCalculator;
    private final HistoricoEmbalagemCalculator historicoEmbalagemCalculator;

    private final Map<String, PeriodoStrategy> estrategias = Map.of(
            "HOJE",           new HojeStrategy(),
            "ESTA_SEMANA",    new EstaSemanaStrategy(),
            "SEMANA_PASSADA", new SemanaPassadaStrategy(),
            "ESTE_MES",       new EsteMesStrategy(),
            "MES_PASSADO",    new MesPassadoStrategy(),
            "ANO",            new AnoStrategy()
    );

    public ResultadoService(
            PedidoRepository pedidoRepository,
            EmbalagemCalculator embalagemCalculator,
            RankingClienteCalculator rankingClienteCalculator,
            RankingProdutoCalculator rankingProdutoCalculator,
            HistoricoEmbalagemCalculator historicoEmbalagemCalculator
    ) {
        this.pedidoRepository = pedidoRepository;
        this.embalagemCalculator = embalagemCalculator;
        this.rankingClienteCalculator = rankingClienteCalculator;
        this.rankingProdutoCalculator = rankingProdutoCalculator;
        this.historicoEmbalagemCalculator = historicoEmbalagemCalculator;
    }

    public ResultadosResponseDTO gerarResultados(String periodo) {

        String periodoNormalizado = periodo.toUpperCase()
                .replace(" ", "_")
                .replace("Ê", "E");

        PeriodoStrategy strategy = estrategias.getOrDefault(periodoNormalizado, new DefaultStrategy());

        IntervaloDatas intervalo = strategy.calcularIntervalo();

        List<Pedido> pedidos = pedidoRepository.findByDataSolicitacaoBetween(
                intervalo.inicio(),
                intervalo.fim()
        );

        BigDecimal faturado = pedidos.stream()
                .map(Pedido::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        EmbalagemDataDTO embalagens = embalagemCalculator.calcular(pedidos);

        List<RankingClienteDTO> rankingClientes = rankingClienteCalculator.calcular(pedidos);

        List<RankingProdutoDTO> rankingProdutos = rankingProdutoCalculator.calcular(pedidos);

        List<ChartDataDTO> evolucao = strategy.gerarEvolucao(pedidos);

        List<HistoricoEmbalagemDTO> historico = gerarHistoricoAnual();

        return new ResultadosResponseDTO(
                faturado,
                (long) pedidos.size(),
                embalagens,
                evolucao,
                rankingClientes,
                rankingProdutos,
                historico
        );
    }

    private List<HistoricoEmbalagemDTO> gerarHistoricoAnual() {
        int anoAtual = LocalDate.now().getYear();

        List<Pedido> pedidosDoAno = pedidoRepository.findByDataSolicitacaoBetween(
                LocalDate.of(anoAtual, 1, 1),
                LocalDate.of(anoAtual, 12, 31)
        );

        return historicoEmbalagemCalculator.gerar(pedidosDoAno);
    }
}