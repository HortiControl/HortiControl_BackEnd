package sptech.horticontrol.dtos.resultados;

import java.math.BigDecimal;

public record RankingClienteDTO(
        String nome,
        Long totalPedidos,
        BigDecimal valorTotal
) {}
