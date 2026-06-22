package sptech.horticontrol.service.strategy;

import org.junit.jupiter.api.Test;
import sptech.horticontrol.dtos.resultados.ChartDataDTO;
import sptech.horticontrol.dtos.resultados.IntervaloDatas;
import sptech.horticontrol.entity.Mercado;
import sptech.horticontrol.entity.Pedido;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class PeriodoStrategyTest {

    // ────────────────────────── HojeStrategy ──────────────────────────

    @Test
    void hojeStrategy_intervaloDeveSerODiaDeHoje() {
        HojeStrategy strategy = new HojeStrategy();
        LocalDate hoje = LocalDate.now();

        IntervaloDatas intervalo = strategy.calcularIntervalo();

        assertEquals(hoje, intervalo.inicio());
        assertEquals(hoje, intervalo.fim());
    }

    @Test
    void hojeStrategy_evolucaoDeveConterUmPontoComValorSomado() {
        HojeStrategy strategy = new HojeStrategy();
        Pedido p1 = criarPedido(LocalDate.now(), new BigDecimal("100.00"));
        Pedido p2 = criarPedido(LocalDate.now(), new BigDecimal("50.00"));

        List<ChartDataDTO> evolucao = strategy.gerarEvolucao(List.of(p1, p2));

        assertEquals(1, evolucao.size());
        assertEquals(new BigDecimal("150.00"), evolucao.getFirst().valor());
    }

    @Test
    void hojeStrategy_evolucaoSemPedidosDeveRetornarZero() {
        HojeStrategy strategy = new HojeStrategy();

        List<ChartDataDTO> evolucao = strategy.gerarEvolucao(List.of());

        assertEquals(1, evolucao.size());
        assertEquals(BigDecimal.ZERO, evolucao.getFirst().valor());
    }

    // ────────────────────────── EstaSemanaStrategy ──────────────────────────

    @Test
    void estaSemanaStrategy_intervaloDeveIniciarNaSegunda() {
        EstaSemanaStrategy strategy = new EstaSemanaStrategy();
        LocalDate hoje = LocalDate.now();
        LocalDate segundaEsperada = hoje.minusDays(hoje.getDayOfWeek().getValue() - 1);

        IntervaloDatas intervalo = strategy.calcularIntervalo();

        assertEquals(segundaEsperada, intervalo.inicio());
        assertEquals(hoje, intervalo.fim());
    }

    @Test
    void estaSemanaStrategy_evolucaoDeveTerSeteDias() {
        EstaSemanaStrategy strategy = new EstaSemanaStrategy();

        List<ChartDataDTO> evolucao = strategy.gerarEvolucao(List.of());

        assertEquals(7, evolucao.size());
        assertEquals("Seg", evolucao.get(0).label());
        assertEquals("Dom", evolucao.get(6).label());
    }

    // ────────────────────────── SemanaPassadaStrategy ──────────────────────────

    @Test
    void semanaPassadaStrategy_intervaloDeveTerSeteDias() {
        SemanaPassadaStrategy strategy = new SemanaPassadaStrategy();

        IntervaloDatas intervalo = strategy.calcularIntervalo();
        long diasEntre = ChronoUnit.DAYS.between(intervalo.inicio(), intervalo.fim());

        assertEquals(6, diasEntre); 
    }

    @Test
    void semanaPassadaStrategy_evolucaoDeveTerSeteDias() {
        SemanaPassadaStrategy strategy = new SemanaPassadaStrategy();

        List<ChartDataDTO> evolucao = strategy.gerarEvolucao(List.of());

        assertEquals(7, evolucao.size());
        assertEquals("Seg", evolucao.get(0).label());
    }

    // ────────────────────────── EsteMesStrategy ──────────────────────────

    @Test
    void esteMesStrategy_intervaloDeveIniciarNoPrimeiroDiaDoMes() {
        EsteMesStrategy strategy = new EsteMesStrategy();
        LocalDate hoje = LocalDate.now();

        IntervaloDatas intervalo = strategy.calcularIntervalo();

        assertEquals(hoje.withDayOfMonth(1), intervalo.inicio());
        assertEquals(hoje, intervalo.fim());
    }

    @Test
    void esteMesStrategy_evolucaoDeveTerCincoSemanas() {
        EsteMesStrategy strategy = new EsteMesStrategy();

        List<ChartDataDTO> evolucao = strategy.gerarEvolucao(List.of());

        assertEquals(5, evolucao.size());
        assertEquals("Semana 1", evolucao.get(0).label());
        assertEquals("Semana 5", evolucao.get(4).label());
    }

    @Test
    void esteMesStrategy_pedidoDoDia15DeveCairNaSemana3() {
        EsteMesStrategy strategy = new EsteMesStrategy();
        LocalDate dia15 = LocalDate.now().withDayOfMonth(15);
        Pedido pedido = criarPedido(dia15, new BigDecimal("200.00"));

        List<ChartDataDTO> evolucao = strategy.gerarEvolucao(List.of(pedido));

        ChartDataDTO semana3 = evolucao.stream()
                .filter(c -> c.label().equals("Semana 3"))
                .findFirst()
                .orElseThrow();
        assertEquals(new BigDecimal("200.00"), semana3.valor());
    }

    // ────────────────────────── MesPassadoStrategy ──────────────────────────

    @Test
    void mesPassadoStrategy_intervaloDeveAbrangerMesPassadoCompleto() {
        MesPassadoStrategy strategy = new MesPassadoStrategy();
        LocalDate hoje = LocalDate.now();
        LocalDate inicioEsperado = hoje.minusMonths(1).withDayOfMonth(1);
        LocalDate fimEsperado = inicioEsperado.withDayOfMonth(inicioEsperado.lengthOfMonth());

        IntervaloDatas intervalo = strategy.calcularIntervalo();

        assertEquals(inicioEsperado, intervalo.inicio());
        assertEquals(fimEsperado, intervalo.fim());
    }

    @Test
    void mesPassadoStrategy_evolucaoDeveTerCincoSemanas() {
        MesPassadoStrategy strategy = new MesPassadoStrategy();

        List<ChartDataDTO> evolucao = strategy.gerarEvolucao(List.of());

        assertEquals(5, evolucao.size());
    }

    // ────────────────────────── AnoStrategy ──────────────────────────

    @Test
    void anoStrategy_intervaloDeveIniciarEmPrimeiroDejaneiroDoAnoAtual() {
        AnoStrategy strategy = new AnoStrategy();
        LocalDate hoje = LocalDate.now();

        IntervaloDatas intervalo = strategy.calcularIntervalo();

        assertEquals(hoje.withDayOfYear(1), intervalo.inicio());
        assertEquals(hoje, intervalo.fim());
    }

    @Test
    void anoStrategy_evolucaoDeveTerDozeEntradas() {
        AnoStrategy strategy = new AnoStrategy();

        List<ChartDataDTO> evolucao = strategy.gerarEvolucao(List.of());

        assertEquals(12, evolucao.size());
        assertEquals("Jan", evolucao.get(0).label());
        assertEquals("Dez", evolucao.get(11).label());
    }

    @Test
    void anoStrategy_pedidoDeJanDeveAcumularEmJan() {
        AnoStrategy strategy = new AnoStrategy();
        LocalDate janeiro = LocalDate.now().withMonth(1).withDayOfMonth(10);
        Pedido pedido = criarPedido(janeiro, new BigDecimal("300.00"));

        List<ChartDataDTO> evolucao = strategy.gerarEvolucao(List.of(pedido));

        ChartDataDTO jan = evolucao.stream()
                .filter(c -> c.label().equals("Jan"))
                .findFirst()
                .orElseThrow();
        assertEquals(new BigDecimal("300.00"), jan.valor());
    }

    // ────────────────────────── DefaultStrategy ──────────────────────────

    @Test
    void defaultStrategy_intervaloDeveSerOsUltimosTrintaDias() {
        DefaultStrategy strategy = new DefaultStrategy();
        LocalDate hoje = LocalDate.now();

        IntervaloDatas intervalo = strategy.calcularIntervalo();

        assertEquals(hoje.minusDays(30), intervalo.inicio());
        assertEquals(hoje, intervalo.fim());
    }

    @Test
    void defaultStrategy_evolucaoDeveAgruparPorData() {
        DefaultStrategy strategy = new DefaultStrategy();
        LocalDate data = LocalDate.now().minusDays(5);
        Pedido p1 = criarPedido(data, new BigDecimal("100.00"));
        Pedido p2 = criarPedido(data, new BigDecimal("50.00"));

        List<ChartDataDTO> evolucao = strategy.gerarEvolucao(List.of(p1, p2));

        assertEquals(1, evolucao.size());
        assertEquals(new BigDecimal("150.00"), evolucao.getFirst().valor());
    }

    // ────────────────────────── PeriodoHelper ──────────────────────────

    @Test
    void periodoHelper_deveTradurizirTodosOsMeses() {
        assertEquals("Jan", PeriodoHelper.traduzirMes(java.time.Month.JANUARY));
        assertEquals("Fev", PeriodoHelper.traduzirMes(java.time.Month.FEBRUARY));
        assertEquals("Dez", PeriodoHelper.traduzirMes(java.time.Month.DECEMBER));
    }

    @Test
    void periodoHelper_deveTradurizirTodosOsDiasDaSemana() {
        assertEquals("Seg", PeriodoHelper.traduzirDiaSemana(java.time.DayOfWeek.MONDAY));
        assertEquals("Sex", PeriodoHelper.traduzirDiaSemana(java.time.DayOfWeek.FRIDAY));
        assertEquals("Dom", PeriodoHelper.traduzirDiaSemana(java.time.DayOfWeek.SUNDAY));
    }

    // ────────────────────────── Auxiliar ──────────────────────────

    private Pedido criarPedido(LocalDate data, BigDecimal valor) {
        Pedido pedido = new Pedido();
        pedido.setDataSolicitacao(data);
        pedido.setValorTotal(valor);
        pedido.setMercado(new Mercado());
        pedido.setItens(List.of());
        return pedido;
    }
}
