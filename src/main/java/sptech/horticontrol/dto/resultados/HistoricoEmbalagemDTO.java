package sptech.horticontrol.dto.resultados;

public record HistoricoEmbalagemDTO(
        String mes,
        Integer bandejas,
        Integer potes,
        Integer sacos
) {}
