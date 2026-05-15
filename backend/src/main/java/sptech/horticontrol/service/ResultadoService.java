package sptech.horticontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sptech.horticontrol.dtos.resultados.*;
import sptech.horticontrol.entity.Pedido;
import sptech.horticontrol.enums.TipoEmbalagem;
import sptech.horticontrol.repository.PedidoRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResultadoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    public ResultadosResponseDTO gerarResultados(String periodo) {

        String periodoUpper = periodo.toUpperCase();
        IntervaloDatas intervalo = calcularIntervalo(periodoUpper);

        List<Pedido> pedidos = pedidoRepository.findByDataSolicitacaoBetween(
                intervalo.inicio(),
                intervalo.fim()
        );

        BigDecimal faturado = pedidos.stream()
                .map(Pedido::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int bandejas = somarPorEmbalagem(pedidos, TipoEmbalagem.BANDEJA);
        int potes = somarPorEmbalagem(pedidos, TipoEmbalagem.POTE);
        int sacos = somarPorEmbalagem(pedidos, TipoEmbalagem.SACO);

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

        String periodoNormalizado = periodoUpper.replace(" ", "_").replace("Ê", "E");
        List<ChartDataDTO> evolucao = gerarEvolucaoFaturamento(pedidos, periodoNormalizado, intervalo);

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

    private IntervaloDatas calcularIntervalo(String periodo) {

        LocalDate hoje = LocalDate.now();

        String periodoNormalizado = periodo.toUpperCase()
                .replace(" ", "_")
                .replace("Ê", "E");

        return switch (periodoNormalizado) {
            case "HOJE" -> new IntervaloDatas(hoje, hoje);

            case "ESTA_SEMANA" -> new IntervaloDatas(hoje.minusDays(hoje.getDayOfWeek().getValue() - 1), hoje);

            case "SEMANA_PASSADA" -> {
                LocalDate segundaPassada = hoje.minusWeeks(1).minusDays(hoje.getDayOfWeek().getValue() - 1);
                yield new IntervaloDatas(segundaPassada, segundaPassada.plusDays(6));
            }

            case "ESTE_MES" -> new IntervaloDatas(hoje.withDayOfMonth(1), hoje);

            case "MES_PASSADO" -> {
                LocalDate inicioMesPassado = hoje.minusMonths(1).withDayOfMonth(1);
                LocalDate fimMesPassado = inicioMesPassado.withDayOfMonth(inicioMesPassado.lengthOfMonth());
                yield new IntervaloDatas(inicioMesPassado, fimMesPassado);
            }

            case "ANO" -> new IntervaloDatas(hoje.withDayOfYear(1), hoje);

            default -> new IntervaloDatas(hoje.minusDays(30), hoje);
        };
    }

    private List<HistoricoEmbalagemDTO> gerarHistoricoAnual() {
        int anoAtual = LocalDate.now().getYear();

        List<Pedido> pedidosDoAno = pedidoRepository.findByDataSolicitacaoBetween(
                LocalDate.of(anoAtual, 1, 1),
                LocalDate.of(anoAtual, 12, 31)
        );

        var pedidosPorMes = pedidosDoAno.stream()
                .collect(Collectors.groupingBy(p -> p.getDataSolicitacao().getMonth()));

        return java.util.Arrays.stream(java.time.Month.values())
                .map(mes -> {
                    List<Pedido> pedidosMes = pedidosPorMes.getOrDefault(mes, List.of());
                    return new HistoricoEmbalagemDTO(
                            traduzirMes(mes),
                            somarPorEmbalagem(pedidosMes, TipoEmbalagem.BANDEJA),
                            somarPorEmbalagem(pedidosMes, TipoEmbalagem.POTE),
                            somarPorEmbalagem(pedidosMes, TipoEmbalagem.SACO)
                    );
                }).toList();
    }

    private String traduzirMes(java.time.Month mes) {
        return switch (mes) {
            case JANUARY -> "Jan";
            case FEBRUARY -> "Fev";
            case MARCH -> "Mar";
            case APRIL -> "Abr";
            case MAY -> "Mai";
            case JUNE -> "Jun";
            case JULY -> "Jul";
            case AUGUST -> "Ago";
            case SEPTEMBER -> "Set";
            case OCTOBER -> "Out";
            case NOVEMBER -> "Nov";
            case DECEMBER -> "Dez";
        };
    }

    private List<ChartDataDTO> gerarEvolucaoFaturamento(List<Pedido> pedidos, String periodoNormalizado, IntervaloDatas intervalo) {

        Map<String, BigDecimal> faturamentoAgrupado = new LinkedHashMap<>();

        switch (periodoNormalizado) {
            case "HOJE":
                String labelHoje = LocalDate.now().getDayOfMonth() + "/" + traduzirMes(LocalDate.now().getMonth());
                faturamentoAgrupado.put(labelHoje, BigDecimal.ZERO);

                for (Pedido p : pedidos) {
                    faturamentoAgrupado.put(labelHoje, faturamentoAgrupado.get(labelHoje).add(p.getValorTotal()));
                }
                break;

            case "ESTA_SEMANA":
            case "SEMANA_PASSADA":
                faturamentoAgrupado.put("Seg", BigDecimal.ZERO);
                faturamentoAgrupado.put("Ter", BigDecimal.ZERO);
                faturamentoAgrupado.put("Qua", BigDecimal.ZERO);
                faturamentoAgrupado.put("Qui", BigDecimal.ZERO);
                faturamentoAgrupado.put("Sex", BigDecimal.ZERO);
                faturamentoAgrupado.put("Sáb", BigDecimal.ZERO);
                faturamentoAgrupado.put("Dom", BigDecimal.ZERO);

                for (Pedido p : pedidos) {
                    String diaSemana = traduzirDiaSemana(p.getDataSolicitacao().getDayOfWeek());
                    faturamentoAgrupado.put(diaSemana, faturamentoAgrupado.get(diaSemana).add(p.getValorTotal()));
                }
                break;

            case "ESTE_MES":
            case "MES_PASSADO":
                faturamentoAgrupado.put("Semana 1", BigDecimal.ZERO);
                faturamentoAgrupado.put("Semana 2", BigDecimal.ZERO);
                faturamentoAgrupado.put("Semana 3", BigDecimal.ZERO);
                faturamentoAgrupado.put("Semana 4", BigDecimal.ZERO);
                faturamentoAgrupado.put("Semana 5", BigDecimal.ZERO);

                for (Pedido p : pedidos) {
                    int diaDoMes = p.getDataSolicitacao().getDayOfMonth();
                    int numSemana = ((diaDoMes - 1) / 7) + 1;
                    if (numSemana > 5) numSemana = 5;

                    String keySemana = "Semana " + numSemana;
                    faturamentoAgrupado.put(keySemana, faturamentoAgrupado.get(keySemana).add(p.getValorTotal()));
                }
                break;

            case "ANO":
                for (java.time.Month m : java.time.Month.values()) {
                    faturamentoAgrupado.put(traduzirMes(m), BigDecimal.ZERO);
                }

                for (Pedido p : pedidos) {
                    String mesNome = traduzirMes(p.getDataSolicitacao().getMonth());
                    faturamentoAgrupado.put(mesNome, faturamentoAgrupado.get(mesNome).add(p.getValorTotal()));
                }
                break;

            default:
                for (Pedido p : pedidos) {
                    String dataLabel = p.getDataSolicitacao().toString();
                    faturamentoAgrupado.put(dataLabel, faturamentoAgrupado.getOrDefault(dataLabel, BigDecimal.ZERO).add(p.getValorTotal()));
                }
                break;
        }

        return faturamentoAgrupado.entrySet().stream()
                .map(entry -> new ChartDataDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private String traduzirDiaSemana(java.time.DayOfWeek dia) {
        return switch (dia) {
            case MONDAY -> "Seg";
            case TUESDAY -> "Ter";
            case WEDNESDAY -> "Qua";
            case THURSDAY -> "Qui";
            case FRIDAY -> "Sex";
            case SATURDAY -> "Sáb";
            case SUNDAY -> "Dom";
        };
    }
}