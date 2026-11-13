package sptech.horticontrol.dto.resultados;

import java.math.BigDecimal;

public record ChartDataDTO(
        String label,
        BigDecimal valor
) {}
