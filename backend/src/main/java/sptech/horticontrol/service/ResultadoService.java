package sptech.horticontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sptech.horticontrol.dtos.resultados.*;
import sptech.horticontrol.entity.Pedido;
import sptech.horticontrol.enums.TipoEmbalagem;
import sptech.horticontrol.repository.PedidoRepository;
import sptech.horticontrol.service.strategy.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResultadoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    private final Map<String, PeriodoStrategy> estrategias = Map.of(
            "HOJE",           new HojeStrategy(),
            "ESTA_SEMANA",    new EstaSemanaStrategy(),
            "SEMANA_PASSADA", new SemanaPassadaStrategy(),
            "ESTE_MES",       new EsteMesStrategy(),
            "MES_PASSADO",    new MesPassadoStrategy(),
            "ANO",            new AnoStrategy()
    );

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

        int bandejas = somarPorEmbalagem(pedidos, TipoEmbalagem.BANDEJA);
        int potes    = somarPorEmbalagem(pedidos, TipoEmbalagem.POTE);
        int sacos    = somarPorEmbalagem(pedidos, TipoEmbalagem.SACO);

        List<RankingClienteDTO> rankingClientes = pedidos.stream()
                .collect(Collectors.groupingBy(Pedido::getMercado))
                .entrySet().stream()
                .map(e -> new RankingClienteDTO(
                        e.getKey().getNome(),
                        (long) e.getValue().size(),
                        e.getValue().stream().map(Pedido::getValorTotal).reduce(BigDecimal.ZERO, BigDecimal::add)
                ))
                .sorted(Comparator.comparing(RankingClienteDTO::valorTotal).reversed())
                .limit(4).toList();

        List<RankingProdutoDTO> rankingProdutos = pedidos.stream()
                .flatMap(p -> p.getItens().stream())
                .collect(Collectors.groupingBy(item -> item.getProduto()))
                .entrySet().stream()
                .map(e -> new RankingProdutoDTO(
                        e.getKey().getNome(),
                        e.getKey().getTipoProduto().toString(),
                        e.getValue().stream().mapToLong(i -> i.getQuantidade()).sum()
                ))
                .sorted(Comparator.comparing(RankingProdutoDTO::quantidadeVendida).reversed())
                .limit(4).toList();

        List<ChartDataDTO> evolucao = strategy.gerarEvolucao(pedidos);

        List<HistoricoEmbalagemDTO> historico = gerarHistoricoAnual();

        return new ResultadosResponseDTO(
                faturado,
                (long) pedidos.size(),
                new EmbalagemDataDTO(bandejas, potes, sacos),
                evolucao,
                rankingClientes,
                rankingProdutos,
                historico
        );
    }

    private int somarPorEmbalagem(List<Pedido> pedidos, TipoEmbalagem tipo) {
        return pedidos.stream()
                .flatMap(p -> p.getItens().stream())
                .filter(i -> i.getProduto().getTipoEmbalagem() == tipo)
                .mapToInt(i -> i.getQuantidade())
                .sum();
    }

    private List<HistoricoEmbalagemDTO> gerarHistoricoAnual() {
        int anoAtual = LocalDate.now().getYear();

        List<Pedido> pedidosDoAno = pedidoRepository.findByDataSolicitacaoBetween(
                LocalDate.of(anoAtual, 1, 1),
                LocalDate.of(anoAtual, 12, 31)
        );

        var pedidosPorMes = pedidosDoAno.stream()
                .collect(Collectors.groupingBy(p -> p.getDataSolicitacao().getMonth()));

        return Arrays.stream(Month.values())
                .map(mes -> {
                    List<Pedido> pedidosMes = pedidosPorMes.getOrDefault(mes, List.of());
                    return new HistoricoEmbalagemDTO(
                            PeriodoHelper.traduzirMes(mes),
                            somarPorEmbalagem(pedidosMes, TipoEmbalagem.BANDEJA),
                            somarPorEmbalagem(pedidosMes, TipoEmbalagem.POTE),
                            somarPorEmbalagem(pedidosMes, TipoEmbalagem.SACO)
                    );
                }).toList();
    }
}