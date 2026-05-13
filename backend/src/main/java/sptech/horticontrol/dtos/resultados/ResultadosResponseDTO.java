package sptech.horticontrol.dtos.resultados;

import java.math.BigDecimal;
import java.util.List;

public record ResultadosResponseDTO(
        BigDecimal faturadoTotal,
        Long totalPedidos,
        EmbalagemDataDTO consumoEmbalagens,
        List<ChartDataDTO> evolucaoFaturamento,
        List<RankingClienteDTO> melhoresClientes,
        List<RankingProdutoDTO> produtosMaisVendidos,
        List<HistoricoEmbalagemDTO> historicoEmbalagens
) {}
