package sptech.horticontrol.dtos.resultados;

import java.math.BigDecimal;

public record ChartDataDTO(
        String label,
        BigDecimal valor
) {}
